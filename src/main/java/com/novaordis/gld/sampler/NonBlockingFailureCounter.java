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

import java.util.concurrent.atomic.AtomicLong;

public class NonBlockingFailureCounter
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Class<? extends Throwable> failureType;
    private AtomicLong count;
    private AtomicLong cumulatedFailureTimeNano;

    // Constructors ----------------------------------------------------------------------------------------------------

    public NonBlockingFailureCounter(Class<? extends Throwable> failureType)
    {
        this.failureType = failureType;
        this.count = new AtomicLong();
        this.cumulatedFailureTimeNano = new AtomicLong();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Class<? extends Throwable> getFailureType()
    {
        return failureType;
    }

    /**
     * @param durationNano the duration (in nanoseconds) of the failed call.
     */
    public void increment(long durationNano)
    {
        count.incrementAndGet();
        cumulatedFailureTimeNano.addAndGet(durationNano);
    }

    /**
     * @return a long[2] containing the invocation count on the first position and the cumulated time (in nanoseconds)
     * on the second position.
     */
    public long[] getCountersAndReset()
    {
        return new long[] {count.getAndSet(0), cumulatedFailureTimeNano.getAndSet(0)};
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
