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

/**
 * @see CounterValues
 */
public class CounterValuesImpl implements CounterValues
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long successCount;
    private long successCumulatedTime;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * "zero-value" constructor.
     */
    public CounterValuesImpl()
    {
        this(0L, 0L);
    }

    public CounterValuesImpl(long successCount, long successCumulatedTime)
    {
        this.successCount = successCount;
        this.successCumulatedTime = successCumulatedTime;
    }

    // CounterValues implementation ------------------------------------------------------------------------------------

    @Override
    public long getSuccessCount()
    {
        return successCount;
    }

    @Override
    public long getSuccessCumulatedTime()
    {
        return successCumulatedTime;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
