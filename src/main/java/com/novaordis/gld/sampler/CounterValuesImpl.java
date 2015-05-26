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

import java.util.Collections;
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
    private long successCumulatedDuration;

    private Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters;

    // null means it was not calculated yet
    private Long failureCount;

    // null means it was not calculated yet
    private Long failureCumulatedDurationNano;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * "zero-value" constructor.
     */
    public CounterValuesImpl()
    {
        this(0L, 0L, null);
    }

    /**
     * @param failureCounters a map associating failure types to failure counters.
     *
     * @throws IllegalArgumentException on invalid failure array
     */
    public CounterValuesImpl(long successCount, long successCumulatedDuration,
                             Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters)
    {
        this.successCount = successCount;
        this.successCumulatedDuration = successCumulatedDuration;

        if (failureCounters == null)
        {
            this.failureCounters = Collections.emptyMap();
        }
        else
        {
            this.failureCounters = failureCounters;
        }
    }

    // CounterValues implementation ------------------------------------------------------------------------------------

    @Override
    public long getSuccessCount()
    {
        return successCount;
    }

    @Override
    public long getSuccessCumulatedDuration()
    {
        return successCumulatedDuration;
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
