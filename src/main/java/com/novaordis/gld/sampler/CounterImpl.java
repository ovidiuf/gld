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

import java.util.concurrent.atomic.AtomicLong;

/**
 * A standard counter implementation, that measures success count, cumulated time (in nanoseconds) and failures.
 */
public class CounterImpl implements Counter
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CounterImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Class operationType;

    private AtomicLong successCount;
    private AtomicLong cumulatedTimeNano;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CounterImpl(Class operationType)
    {
        if (!Operation.class.isAssignableFrom(operationType))
        {
            throw new IllegalArgumentException(operationType + " is not assignable from Operation");
        }

        this.operationType = operationType;
        this.successCount = new AtomicLong(0L);
        this.cumulatedTimeNano = new AtomicLong(0L);

        log.debug(this + " constructed");
    }

    // Counter implementation ------------------------------------------------------------------------------------------

    @Override
    public Class getOperationType()
    {
        return operationType;
    }

    /**
     * @see Counter#update(long, long, long, Throwable...)
     */
    @Override
    public void update(long t0Ms, long t0Nano, long t1Nano, Throwable... t)
    {
        long duration = t1Nano - t0Nano;

        if (duration < 0)
        {
            throw new IllegalArgumentException("t1 " + t1Nano + " precedes t0 " + t0Nano);
        }

        if (t.length == 0)
        {
            // success

            successCount.incrementAndGet();
            cumulatedTimeNano.addAndGet(duration);
        }
        else
        {
            // failure

            throw new RuntimeException("NOT YET IMPLEMENTED");
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public long getSuccessCount()
    {
        return successCount.get();
    }

    public long getSuccessCountAndReset()
    {
        return successCount.getAndSet(0L);
    }

    public long getCumulatedTime()
    {
        return cumulatedTimeNano.get();
    }

    public long getCumulatedTimeAndReset()
    {
        return cumulatedTimeNano.getAndSet(0L);
    }

    @Override
    public String toString()
    {
        return "Counter[" + (operationType == null ? null : operationType.getSimpleName()) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
