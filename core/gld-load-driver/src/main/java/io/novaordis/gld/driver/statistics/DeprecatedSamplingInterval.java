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

package io.novaordis.gld.driver.statistics;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Deprecated
public class DeprecatedSamplingInterval {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Object RENDEZVOUS_TOKEN = new Object();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long intervalStartMs;

    private long validReadsCount;
    private long readHitsCount;
    private long validWritesCount;

    private long cumulatedValidReadsTimeNano;
    private long cumulatedValidWritesTimeNano;

    /**
     * An array with failure counters, indexed by failure type.
     * @see io.novaordis.gld.driver.RedisFailure
     */
    private long[] failureCounters;

    private double systemLoadAverage;
    private double systemCpuLoad;
    private double processCpuLoad;
    private double processCpuTime;

    private long usedHeap; // in bytes
    private long committedHeap; // in bytes

    private String comments;

    private final BlockingQueue<Object> rendezvous = new ArrayBlockingQueue<>(1);

    // Constructors ----------------------------------------------------------------------------------------------------

    public DeprecatedSamplingInterval(long intervalStartMs,
                                      long validReadsCount,
                                      long readHitsCount,
                                      long validWritesCount,
                                      long cumulatedValidReadsTimeNano,
                                      long cumulatedValidWritesTimeNano,
                                      long[] failureCounters,
                                      double systemLoadAverage,
                                      double systemCpuLoad,
                                      double processCpuLoad,
                                      double processCpuTime,
                                      long usedHeap,
                                      long committedHeap)
    {
        this.intervalStartMs = intervalStartMs;
        this.validReadsCount = validReadsCount;
        this.readHitsCount = readHitsCount;
        this.validWritesCount = validWritesCount;
        this.cumulatedValidReadsTimeNano = cumulatedValidReadsTimeNano;
        this.cumulatedValidWritesTimeNano = cumulatedValidWritesTimeNano;
        this.failureCounters = new long[failureCounters.length];
        System.arraycopy(failureCounters, 0, this.failureCounters, 0, failureCounters.length);
        this.systemLoadAverage = systemLoadAverage;
        this.systemCpuLoad = systemCpuLoad;
        this.processCpuLoad = processCpuLoad;
        this.processCpuTime = processCpuTime;
        this.usedHeap = usedHeap;
        this.committedHeap = committedHeap;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Start timestamp, in milliseconds.
     */
    public long getIntervalStartMs()
    {
        return intervalStartMs;
    }

    public long getValidOperationsCount()
    {
        return validReadsCount + validWritesCount;
    }

    public long getValidReadsCount()
    {
        return validReadsCount;
    }

    public long getValidWritesCount()
    {
        return validWritesCount;
    }

    public long getCumulatedValidReadsTimeNano()
    {
        return cumulatedValidReadsTimeNano;
    }

    public long getCumulatedValidWritesTimeNano()
    {
        return cumulatedValidWritesTimeNano;
    }

    public long[] getFailureCounters()
    {
        return failureCounters;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void waitUntilProcessed()
    {
        while(true)
        {
            try
            {
                Object o = rendezvous.take();

                if (o != null)
                {
                    return;
                }

            }
            catch (InterruptedException e)
            {
                // noop, fine
            }
        }

    }

    void markProcessed()
    {
        try
        {
            rendezvous.put(RENDEZVOUS_TOKEN);
        }
        catch(InterruptedException e)
        {
            throw new IllegalStateException("failed to mark " + this + " as processed", e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
