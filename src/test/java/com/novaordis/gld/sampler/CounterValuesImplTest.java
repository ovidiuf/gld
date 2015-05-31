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
        CounterValuesImpl cv = new CounterValuesImpl();

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
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));

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
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));

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

    // incrementWith() -------------------------------------------------------------------------------------------------

    @Test
    public void incrementWith() throws Exception
    {
        CounterValuesImpl base = getCounterValuesToTest();

        Set<Class<? extends Throwable>> failureTypes = base.getFailureTypes();
        assertEquals(0, failureTypes.size());
        assertEquals(0L, base.getSuccessCount());
        assertEquals(0L, base.getSuccessCumulatedDurationNano());
        assertEquals(0L, base.getFailureCount());
        assertEquals(0L, base.getFailureCumulatedDurationNano());
        assertEquals(0L, base.getFailureCount(SocketException.class));
        assertEquals(0L, base.getFailureCumulatedDurationNano(SocketException.class));

        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));

        CounterValuesImpl increment1 = new CounterValuesImpl(3L, 4L, failureCounters);

        base.incrementWith(increment1);

        failureTypes = base.getFailureTypes();
        assertEquals(1, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(3L, base.getSuccessCount());
        assertEquals(4L, base.getSuccessCumulatedDurationNano());
        assertEquals(1L, base.getFailureCount());
        assertEquals(2L, base.getFailureCumulatedDurationNano());
        assertEquals(1L, base.getFailureCount(SocketException.class));
        assertEquals(2L, base.getFailureCumulatedDurationNano(SocketException.class));

        failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(5L, 6L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(7L, 8L));
        CounterValuesImpl increment2 = new CounterValuesImpl(9L, 10L, failureCounters);

        base.incrementWith(increment2);

        failureTypes = base.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(3L + 9L, base.getSuccessCount());
        assertEquals(4L + 10L, base.getSuccessCumulatedDurationNano());
        assertEquals(1L + 5L + 7L, base.getFailureCount());
        assertEquals(2L + 6L + 8L, base.getFailureCumulatedDurationNano());
        assertEquals(1L + 5L, base.getFailureCount(SocketException.class));
        assertEquals(2L + 6L, base.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(7L, base.getFailureCount(ConnectException.class));
        assertEquals(8L, base.getFailureCumulatedDurationNano(ConnectException.class));

        failureCounters = new HashMap<>();
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(11L, 12L));
        failureCounters.put(IOException.class, new ImmutableFailureCounter(13L, 14L));
        CounterValuesImpl increment3 = new CounterValuesImpl(15L, 16L, failureCounters);

        base.incrementWith(increment3);

        failureTypes = base.getFailureTypes();
        assertEquals(3, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertTrue(failureTypes.contains(IOException.class));
        assertEquals(3L + 9L + 15L, base.getSuccessCount());
        assertEquals(4L + 10L + 16L, base.getSuccessCumulatedDurationNano());
        assertEquals(1L + 5L + 7L + 11L + 13L, base.getFailureCount());
        assertEquals(2L + 6L + 8L + 12L + 14L, base.getFailureCumulatedDurationNano());
        assertEquals(1L + 5L, base.getFailureCount(SocketException.class));
        assertEquals(2L + 6L, base.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(7L + 11L, base.getFailureCount(ConnectException.class));
        assertEquals(8L + 12L, base.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(13L, base.getFailureCount(IOException.class));
        assertEquals(14L, base.getFailureCumulatedDurationNano(IOException.class));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CounterValuesImpl getCounterValuesToTest() throws Exception
    {
        return new CounterValuesImpl();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
