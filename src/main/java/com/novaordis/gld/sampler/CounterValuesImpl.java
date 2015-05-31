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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @see CounterValues
 */
public class CounterValuesImpl implements CounterValues
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long successCount;
    private long successCumulatedDurationNano;

    private Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters;

    // null means it was not calculated yet
    private Long failureCount;

    // null means it was not calculated yet
    private Long failureCumulatedDurationNano;

    private long intervalNano;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * "zero-value" constructor.
     */
    public CounterValuesImpl()
    {
        this(0L, 0L, 0L, null);
    }

    public CounterValuesImpl(long successCount, long successCumulatedDurationNano, long intervalNano)
    {
        this(successCount, successCumulatedDurationNano, intervalNano, null);
    }

    /**
     * @param failureCounters a map associating failure types to failure counters.
     * @param intervalNano the time interval in nanoseconds the counter correspond to.
     *
     * @throws IllegalArgumentException on invalid failure array
     */
    public CounterValuesImpl(long successCount, long successCumulatedDurationNano, long intervalNano,
                             Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters)
    {
        this.intervalNano = intervalNano;
        this.successCount = successCount;
        this.successCumulatedDurationNano = successCumulatedDurationNano;

        if (failureCounters == null)
        {
            this.failureCounters = new HashMap<>();
        }
        else
        {
            this.failureCounters = failureCounters;
        }
    }

    // CounterValues implementation ------------------------------------------------------------------------------------

    @Override
    public long getIntervalNano()
    {
        return intervalNano;
    }

    @Override
    public long getSuccessCount()
    {
        return successCount;
    }

    @Override
    public long getSuccessCumulatedDurationNano()
    {
        return successCumulatedDurationNano;
    }

    @Override
    public Set<Class<? extends Throwable>> getFailureTypes()
    {
        return failureCounters.keySet();
    }

    /**
     * @see CounterValues#getFailureCount()
     */
    @Override
    public long getFailureCount()
    {
        // lazy evaluation - we do it late because we might never have to do it

        if (failureCount == null)
        {
            long c = 0L;
            long d = 0L;
            for(FailureCounter fc: failureCounters.values())
            {
                c += fc.getCount();
                d += fc.getCumulatedDurationNano();
            }

            failureCount = c;
            failureCumulatedDurationNano = d;
        }

        return failureCount;
    }

    /**
     * @see CounterValues#getFailureCount(Class)
     */
    @Override
    public long getFailureCount(Class<? extends Throwable> failureType)
    {
        ImmutableFailureCounter c = failureCounters.get(failureType);

        if (c == null)
        {
            return 0L;
        }

        return c.getCount();
    }

    /**
     * @see CounterValues#getFailureCumulatedDurationNano()
     */
    @Override
    public long getFailureCumulatedDurationNano()
    {
        // lazy evaluation - we do it late because we might never have to do it

        if (failureCumulatedDurationNano == null)
        {
            long c = 0L;
            long d = 0L;
            for(FailureCounter fc: failureCounters.values())
            {
                c += fc.getCount();
                d += fc.getCumulatedDurationNano();
            }

            failureCount = c;
            failureCumulatedDurationNano = d;
        }

        return failureCumulatedDurationNano;
    }

    /**
     * @see CounterValues#getFailureCumulatedDurationNano(Class)
     */
    @Override
    public long getFailureCumulatedDurationNano(Class<? extends Throwable> failureType)
    {
        ImmutableFailureCounter c = failureCounters.get(failureType);

        if (c == null)
        {
            return 0L;
        }

        return c.getCumulatedDurationNano();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The implementation operates under the assumption that the only thread invoking this method is the sampling
     * thread, so the implementation is NOT thread safe.
     */
    public void incrementWith(CounterValues other)
    {
        successCount += other.getSuccessCount();
        successCumulatedDurationNano += other.getSuccessCumulatedDurationNano();

        Set<Class<? extends Throwable>> otherFailureTypes = other.getFailureTypes();

        if (otherFailureTypes.isEmpty())
        {
            // no failure counters to add, we're done here
            return;
        }

        for(Class<? extends Throwable> ft : otherFailureTypes)
        {
            long oc = other.getFailureCount(ft);
            long ocd = other.getFailureCumulatedDurationNano(ft);

            ImmutableFailureCounter thisFailureCounter = failureCounters.get(ft);

            long baseOc = 0L;
            long baseOcd = 0L;

            if (thisFailureCounter != null)
            {
                baseOc = thisFailureCounter.getCount();
                baseOcd = thisFailureCounter.getCumulatedDurationNano();
            }

            failureCounters.put(ft, new ImmutableFailureCounter(baseOc + oc, baseOcd + ocd));
        }

        // since we modified the failure counters, we re-set the pre-calculated aggregated values
        failureCount = null;
        failureCumulatedDurationNano = null;

        throw new RuntimeException("incrementWith() THIS IS WHERE WE SHOULD ALSO INCREMENT THE INTERVAL");
    }

    @Override
    public String toString()
    {
        return "" + successCount + ", " + (failureCount == null ? 0 : failureCount);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
