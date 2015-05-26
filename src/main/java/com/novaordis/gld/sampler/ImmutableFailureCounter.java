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

public class ImmutableFailureCounter implements FailureCounter
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private final Class<? extends Throwable> failureType;
    private final long count;
    private final long cumulatedFailureDurationNano;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ImmutableFailureCounter(Class<? extends Throwable> failureType, long count, long cumulatedFailureDurationNano)
    {
        this.failureType = failureType;
        this.count = count;
        this.cumulatedFailureDurationNano = cumulatedFailureDurationNano;
    }

    // FailureCounter implementation -----------------------------------------------------------------------------------

    @Override
    public Class<? extends Throwable> getFailureType()
    {
        return failureType;
    }

    @Override
    public long getCount()
    {
        return count;
    }

    @Override
    public long getCumulatedDurationNano()
    {
        return cumulatedFailureDurationNano;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
