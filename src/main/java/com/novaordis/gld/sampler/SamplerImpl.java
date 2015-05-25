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
    private volatile boolean shutDown;

    final Map<Class<? extends Operation>, Counter> counters;
    final List<String> annotations;

    private List<SamplingConsumer> consumers;

    private long lastRunTimestampMs;

    private final Object mutex;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SamplerImpl()
    {
        this(DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS, DEFAULT_SAMPLING_INTERVAL_MS);
    }

    public SamplerImpl(long samplingTaskRunIntervalMs, long samplingIntervalMs)
    {
        this.samplingIntervalMs = samplingIntervalMs;
        this.samplingTaskRunIntervalMs = samplingTaskRunIntervalMs;
        this.consumers = new ArrayList<>();
        this.started = false;
        this.counters = new HashMap<>();
        this.annotations = new CopyOnWriteArrayList<>();
        this.lastRunTimestampMs = -1L;
        verifySamplingIntervalsRelationship();
        this.shutDown = false;

        this.mutex = new Object();

        log.debug(this + " created, sampling task run interval " + samplingTaskRunIntervalMs +
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

        log.debug(this + " started");
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

        // tell the last task to shut the timer down
        shutDown = true;

        // wait until the last sample is written, it will happen in at most
        // (samplingIntervalMs + samplingTaskRunIntervalMs) milliseconds or it won't happen at all

        try
        {
            waitUntilNextSamplingTaskFinishes(samplingIntervalMs + samplingTaskRunIntervalMs + 500L);
        }
        catch(InterruptedException e)
        {
            throw new IllegalStateException("interrupted while waiting for last sampling task to finish", e);
        }

        // redundantly cancel the sampler in case the sampling tasks didn't, no harm in canceling it twice
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
    public synchronized Counter registerOperation(Class<? extends Operation> operationType)
    {
        if (isStarted())
        {
            throw new IllegalStateException("can't register an operation after the sampler was started");
        }

        if (!Operation.class.isAssignableFrom(operationType))
        {
            throw new IllegalArgumentException(operationType + " is not assignable from Operation");
        }

        Counter counter = new NonBlockingCounter(operationType);
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

    @Override
    public void annotate(String line)
    {
        annotations.add(line);
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

        // locate the counter corresponding to the operation being recorded - the map is never written concurrently so
        // we don't need to take any special synchronization precautions.

        Counter counter = counters.get(op.getClass());

        if (counter == null)
        {
            throw new IllegalArgumentException(
                "no operation of type " + op.getClass() + " was registered with this sampler before startup");
        }

        counter.update(t0Ms, t0Nano, t1Nano, t);
    }

    // TimerTask implementation ----------------------------------------------------------------------------------------

    @Override
    public void run()
    {
        try
        {
            long thisRunTimestampMs = System.currentTimeMillis();

            if (lastRunTimestampMs == -1L)
            {
                // this is the first run, we don't have the beginning of the sampling interval, set it and wait until
                // the next sampling task run
                lastRunTimestampMs = thisRunTimestampMs;
                return;
            }

            long duration = thisRunTimestampMs - lastRunTimestampMs;
            lastRunTimestampMs = thisRunTimestampMs;

            if (debug) { log.debug("sampling task running, it covers the last " + duration + " ms ..."); }

            SamplingIntervalImpl si = new SamplingIntervalImpl(thisRunTimestampMs, duration, counters.keySet());

            // make a local copy and reset the counters; it's fine to access the holding map as it will be never be
            // written concurrently
            for (Counter c : counters.values())
            {
                Class<? extends Operation> ot = c.getOperationType();
                CounterValues cvs = c.getCounterValuesAndReset();
                si.setCounterValues(ot, cvs);
            }

            // collect annotations and place them in the sampling interval instance
            Object[] aa = annotations.toArray();
            annotations.clear();
            for (Object o : aa)
            {
                si.addAnnotation((String) o);
            }

            for (SamplingConsumer c : consumers)
            {
                try
                {
                    c.consume(si);
                }
                catch (Throwable t)
                {
                    // protect ourselves against malfunctioning consumers
                    log.warn("sampling consumer " + c + " failed to handle a sampling interval instance", t);
                }
            }

            if (shutDown)
            {
                // cancel the timer, no further tasks will be scheduled. From javadoc: Note that calling this method
                // from within the run method of a timer task that was invoked by this timer absolutely guarantees that
                // the ongoing task execution is the last task execution that will ever be performed by this timer.
                samplingTimer.cancel();
            }
        }
        catch(Throwable t)
        {
            log.warn(this + "'s sampling thread failed", t);
        }
        finally
        {
            // release all threads waiting on mutex for the sampler task to finish, regardless whether it was
            // successful or it failed
            synchronized (mutex)
            {
                if (debug) { log.debug("releasing all other threads waiting on " + this + "'s mutex ..."); }
                mutex.notifyAll();
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "Sampler[" + getSamplingIntervalMs() + " ms]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Puts the calling thread on wait until the next sampler task finishes running or timeout occurs. Currently
     * only used for testing.
     *
     * @param timeout in ms. null means wait until notified.
     *
     * @see Object#wait(long)
     *
     * @throws InterruptedException
     */
    void waitUntilNextSamplingTaskFinishes(Long timeout) throws InterruptedException
    {
        synchronized (mutex)
        {
            if (timeout == null)
            {
                log.debug("blocking on " + this + "'s mutex with no timeout");
                mutex.wait();
            }
            else
            {
                log.debug("blocking on " + this + "'s mutex with a timeout of " + timeout + " ms");
                mutex.wait(timeout);
            }
        }
    }

    /**
     * For testing only.
     */
    void setConsumers(List<SamplingConsumer> consumers)
    {
        this.consumers = consumers;
    }

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
