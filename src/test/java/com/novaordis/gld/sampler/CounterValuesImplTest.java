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

import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CounterValuesImplTest extends CounterValuesTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullFailureMap() throws Exception
    {
        CounterValuesImpl cv = new CounterValuesImpl(0L, 0L, null);

        assertTrue(cv.getFailureTypes().isEmpty());
        assertEquals(0L, cv.getFailureCount());
        assertEquals(0L, cv.getFailureCumulatedDurationNano());
        assertEquals(0L, cv.getFailureCount(SocketException.class));
        assertEquals(0L, cv.getFailureCumulatedDurationNano(SocketException.class));
    }

    @Test
    public void knownAndUnknownFailures() throws Exception
    {
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(SocketException.class, 1L, 2L));

        CounterValuesImpl cv = new CounterValuesImpl(0L, 0L, failureCounters);

        // known failure

        assertEquals(1L, cv.getFailureCount(SocketException.class));
        assertEquals(2L, cv.getFailureCumulatedDurationNano(SocketException.class));

        // unknown failure

        assertEquals(0L, cv.getFailureCount(IOException.class));
        assertEquals(0L, cv.getFailureCumulatedDurationNano(IOException.class));
    }

    @Test
    public void aggregatedValues() throws Exception
    {
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(SocketException.class, 1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(ConnectException.class, 3L, 4L));

        CounterValuesImpl cv = new CounterValuesImpl(0L, 0L, failureCounters);

        Set<Class<? extends Throwable>> failureTypes = cv.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));

        assertEquals(1L + 3L, cv.getFailureCount());
        assertEquals(2L + 4L, cv.getFailureCumulatedDurationNano());

        assertEquals(1L, cv.getFailureCount(SocketException.class));
        assertEquals(3L, cv.getFailureCount(ConnectException.class));
        assertEquals(0L, cv.getFailureCount(IOException.class));

        assertEquals(2L, cv.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(4L, cv.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(0L, cv.getFailureCumulatedDurationNano(IOException.class));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CounterValuesImpl getCounterValuesToTest() throws Exception
    {
        return new CounterValuesImpl(0, 0, null);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
