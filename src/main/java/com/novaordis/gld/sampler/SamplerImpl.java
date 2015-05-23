/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novaordis.gld.sampler;

import com.novaordis.gld.Operation;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class SamplerImpl extends TimerTask implements Sampler
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerImpl.class);
    private static final boolean debug = log.isDebugEnabled();

    public static final long DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS = 500L;
    public static final long DEFAULT_SAMPLING_INTERVAL_MS = 1000L;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long samplingIntervalMs;
    private long samplingTaskRunIntervalMs;

    private Timer samplingTimer;

    private volatile boolean started;

    final Map<Class, Counter> counters;
    final List<String> annotations;

    private List<SamplingConsumer> consumers;

    private long lastRunTimestampMs;

    private final Object mutex;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SamplerImpl()
    {
        this.samplingIntervalMs = DEFAULT_SAMPLING_INTERVAL_MS;
        this.samplingTaskRunIntervalMs = DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS;
        this.consumers = new ArrayList<>();
        this.started = false;
        this.counters = new HashMap<>();
        this.annotations = new CopyOnWriteArrayList<>();
        this.lastRunTimestampMs = -1L;
        verifySamplingIntervalsRelationship();

        this.mutex = new Object();

        log.debug("SamplerImpl created, sampling task run interval " + samplingTaskRunIntervalMs +
            " ms, sampling interval " + samplingIntervalMs + " ms");
    }

    // Sampler implementation ------------------------------------------------------------------------------------------

    /**
     * @see Sampler#start()
     */
    @Override
    public synchronized void start()
    {
        if (started)
        {
            return;
        }

        if (samplingTimer != null)
        {
            throw new IllegalStateException(
                "a stopped sampler is supposed to have a null timer, and this one doesn't: " + samplingTimer);
        }

        if (counters.isEmpty())
        {
            throw new IllegalStateException("no operations were registered");
        }

        samplingTimer = new Timer("Sampling Thread", true);

        // execute the sampling timer functionality ourselves, to initiate the state
        run();

        samplingTimer.scheduleAtFixedRate(this, samplingTaskRunIntervalMs, samplingTaskRunIntervalMs);

        started = true;

        log.debug("sampler started");
    }

    @Override
    public synchronized boolean isStarted()
    {
        return started;
    }

    @Override
    public synchronized void stop()
    {
        if (!started)
        {
            return;
        }

        if (samplingTimer == null)
        {
            throw new IllegalStateException(
                "a started sampler is supposed to have a non-null timer, and this one doesn't");
        }

        samplingTimer.cancel();
        samplingTimer = null;
        started = false;
    }

    @Override
    public void setSamplingIntervalMs(long samplingIntervalMs)
    {
        if (isStarted())
        {
            throw new IllegalStateException("can't modify the sampling interval after the sampler was started");
        }

        this.samplingIntervalMs = samplingIntervalMs;
        verifySamplingIntervalsRelationship();
        log.debug("sampling interval set to " + samplingIntervalMs + " ms");
    }

    @Override
    public long getSamplingIntervalMs()
    {
        return samplingIntervalMs;
    }

    @Override
    public void setSamplingTaskRunIntervalMs(long ms)
    {
        if (isStarted())
        {
            throw new IllegalStateException("can't modify the sampling task run interval after the sampler was started");
        }

        this.samplingTaskRunIntervalMs = ms;
        verifySamplingIntervalsRelationship();
        log.debug("sampling task run interval set to " + samplingIntervalMs + " ms");
    }

    @Override
    public long getSamplingTaskRunIntervalMs()
    {
        return samplingTaskRunIntervalMs;
    }

    /**
     * @see Sampler#registerOperation(Class)
     */
    @Override
    public synchronized Counter registerOperation(Class operationType)
    {
        if (isStarted())
        {
            throw new IllegalStateException("can't register an operation after the sampler was started");
        }

        if (!Operation.class.isAssignableFrom(operationType))
        {
            throw new IllegalArgumentException(operationType + " is not assignable from Operation");
        }

        Counter counter = new CounterImpl(operationType);
        counters.put(operationType, counter);
        return counter;
    }

    /**
     * @see Sampler#getCounter(Class)
     */
    @Override
    public Counter getCounter(Class operationType)
    {
        return counters.get(operationType);
    }

    /**
     * @see Sampler#registerConsumer(SamplingConsumer)
     */
    @Override
    public synchronized boolean registerConsumer(SamplingConsumer consumer)
    {
        return consumers.add(consumer);
    }

    /**
     * @see Sampler#record(long, long, long, Operation, Throwable...)
     */
    @Override
    public void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable... t)
    {
        if (!started)
        {
            throw new IllegalStateException(this + " not started");
        }

        // locate the counters per operation - the map is never written concurrently so we don't need to take
        // any special precautions

        Counter counter = counters.get(op.getClass());

        if (counter == null)
        {
            throw new IllegalArgumentException(
                "no operation of type " + op.getClass() + " was registered with this sampler");
        }

        counter.update(t0Ms, t0Nano, t1Nano, t);
    }

    @Override
    public void annotate(String line)
    {
        annotations.add(line);
    }

    /**
     * @see Sampler#waitUntilNextSamplingTaskFinishes(long)
     */
    @Override
    public void waitUntilNextSamplingTaskFinishes(long timeout) throws InterruptedException
    {
        synchronized (mutex)
        {
            log.debug("blocking on " + this + "'s mutex with a timeout of " + timeout + " ms");
            mutex.wait(timeout);
        }
    }

    // TimerTask implementation ----------------------------------------------------------------------------------------

    @Override
    public void run()
    {
        long thisRunTimestampMs = System.currentTimeMillis();

        if (lastRunTimestampMs == -1L)
        {
            // this is the first run, we don't have the beginning of the sampling interval, set it and wait until the
            // next sampling task run
            lastRunTimestampMs = thisRunTimestampMs;
            return;
        }

        long duration = thisRunTimestampMs - lastRunTimestampMs;
        lastRunTimestampMs = thisRunTimestampMs;

        if (debug) { log.debug("sampling task starting, it covers the last " + duration + " ms ..."); }

        SamplingIntervalImpl si = new SamplingIntervalImpl(thisRunTimestampMs, counters.keySet());

        // make a local copy and reset the counters; it's fine to access the holding map as it will be never be
        // written concurrently
        for(Counter c: counters.values())
        {
            CounterImpl ci = (CounterImpl)c;
            Class ot = ci.getOperationType();
            si.setSuccessCount(ot, ci.getSuccessCountAndReset());
        }

        // collect annotations and place them in the sampling interval instance
        Object[] aa = annotations.toArray();
        annotations.clear();
        for(Object o: aa)
        {
            si.addAnnotation((String)o);
        }

        for(SamplingConsumer c: consumers)
        {
            try
            {
                c.consume(si);
            }
            catch(Throwable t)
            {
                // protect ourselves against malfunctioning consumers
                log.warn("sampling consumer " + c + " failed to handle a sampling interval instance", t);
            }
        }

        synchronized (mutex)
        {
            // release all threads waiting on mutex for the sampler task to finish
            log.debug("releasing all threads waiting on mutex");
            mutex.notifyAll();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "Sampler[" + getSamplingIntervalMs() + " ms]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void verifySamplingIntervalsRelationship() throws IllegalArgumentException
    {
        if (samplingIntervalMs <= samplingTaskRunIntervalMs)
        {
            throw new IllegalArgumentException(
                "the sampling task run interval (" + samplingTaskRunIntervalMs +
                    " ms) must be strictly smaller than sampling interval (" + samplingIntervalMs +
                    " ms) to insure desired resolution");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
