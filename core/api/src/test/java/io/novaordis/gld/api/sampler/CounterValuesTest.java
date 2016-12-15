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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.sampler.CounterValues;
import org.junit.Test;

import java.net.SocketException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class CounterValuesTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void defaultBehavior() throws Exception
    {
        CounterValues v = getCounterValuesToTest();

        assertEquals(0, v.getSuccessCount());
        assertEquals(0L, v.getSuccessCumulatedDurationNano());
        assertEquals(0, v.getFailureCount());
        assertEquals(0L, v.getFailureCumulatedDurationNano());
        assertTrue(v.getFailureTypes().isEmpty());
        assertEquals(0, v.getFailureCount(SocketException.class));
        assertEquals(0L, v.getFailureCumulatedDurationNano(SocketException.class));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract CounterValues getCounterValuesToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
