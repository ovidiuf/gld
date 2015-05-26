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
    private volatile boolean shuttingDown;

    final Map<Class<? extends Operation>, Counter> counters;
    final List<String> annotations;

    private List<SamplingConsumer> consumers;

    private long lastRunTimestamp;

    private final Object mutex;

    private SamplingIntervalImpl current;

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
        this.lastRunTimestamp = -1L;
        verifySamplingIntervalsRelationship();
        this.shuttingDown = false;
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

        // execute the sampling timer functionality ourselves, to initiate the state
        run();

        if (samplingTaskRunIntervalMs <= 0)
        {
            log.warn("the sampling task run interval is " + (samplingTaskRunIntervalMs == 0 ? "0" : "negative") +
                ", no sampling tasks will run");

        }
        else
        {
            // start the timer and schedule a task only if the sampling task run interval is larger than 0, otherwise
            // just make it look like it started
            samplingTimer = new Timer("Sampling Thread", true);
            samplingTimer.scheduleAtFixedRate(this, samplingTaskRunIntervalMs, samplingTaskRunIntervalMs);
        }


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

        // tell the last task to shut the timer down
        shuttingDown = true;

        // wait until the last sample is written, it will happen in at most
        // (samplingIntervalMs + samplingTaskRunIntervalMs) milliseconds or it won't happen at all
        if (samplingTimer != null)
        {
            try
            {
                waitUntilNextSamplingTaskFinishes(samplingIntervalMs + samplingTaskRunIntervalMs + 500L);
            }
            catch (InterruptedException e)
            {
                throw new IllegalStateException("interrupted while waiting for last sampling task to finish", e);
            }

            // redundantly cancel the sampler in case the sampling tasks didn't, no harm in canceling it twice
            samplingTimer.cancel();
            samplingTimer = null;
        }

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
        long thisRunTimestamp = System.currentTimeMillis();

        try
        {
            if (lastRunTimestamp == -1L)
            {
                // this is the first run, we don't have the beginning of the sampling interval, we'll set it in the
                // finally clause and wait until the next sampling task run
                log.debug("sampling task timestamp initialization run, not collecting statistics yet ...");

                // done for this run ...
                return;
            }

            if (debug) { log.debug("sampling task running, it covers the last " + (thisRunTimestamp - lastRunTimestamp) + " ms ..."); }

            if (current == null)
            {
                // there was no sampling interval built yet, set it to be on a round second mark, preceding but as
                // close as possible to this run timestamp
                long siTs = (thisRunTimestamp / 1000) * 1000L;
                current = new SamplingIntervalImpl(siTs, samplingIntervalMs, counters.keySet());

                log.debug("sampling interval initialized, beginning to collect statistics ...");

                // done for this run ...
                return;
            }

            if (thisRunTimestamp - current.getTimestamp() < samplingIntervalMs)
            {
                // we are still strictly within the current sampling interval, increment the current sampling interval
                // counters, while resetting the concurrent counters; the sampling interval will never be accessed by
                // other thread except the sampling thread, so it does not need synchronization or any other type of
                // memory visibility control

                for (Counter c : counters.values())
                {
                    Class<? extends Operation> operationType = c.getOperationType();
                    CounterValues cvs = c.getCounterValuesAndReset();
                    current.incrementCounterValues(operationType, cvs);
                }

                // also collect the annotations (if any) and add them to the sampling interval
                Object[] aa = annotations.toArray();
                annotations.clear();
                for (Object o : aa)
                {
                    current.addAnnotation((String) o);
                }

                // done for this run ...
                return;
            }

            // we're right on the edge of the sampling interval or we went beyond it

            // if only a sample accumulated, send it to consumers

            // if more than one sample accumulated, distribute values across samples and send all of them to
            // consumers

            SamplingInterval wrappedUp = current;

            // TODO review this

            current = new SamplingIntervalImpl(
                wrappedUp.getTimestamp() + samplingIntervalMs, samplingIntervalMs, counters.keySet());

            sendSampleToConsumers(wrappedUp);

            if (shuttingDown)
            {
                // TODO review this to make sure I have time to collect all in-flight stuff

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
            lastRunTimestamp = thisRunTimestamp;

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

    /**
     * For testing only. May return null.
     */
    SamplingInterval getCurrent()
    {
        return current;
    }

    long getLastRunTimestamp()
    {
        return lastRunTimestamp;
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

    private void sendSampleToConsumers(SamplingInterval si)
    {
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
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
