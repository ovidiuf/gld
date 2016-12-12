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

package io.novaordis.gld.driver.sampler;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.mock.AnotherTypeOfMockOperation;
import io.novaordis.gld.api.mock.MockOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SamplingIntervalImplTest extends SamplingIntervalTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SamplingIntervalImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullOperationTypesSet() throws Exception {

        try {

            getSamplingIntervalToTest(0L, 1L, null, new ArrayList<String>());
            fail("should fail on account of null operation types set");
        }
        catch(IllegalArgumentException iae) {
            log.info(iae.getMessage());
        }
    }

    @Test
    public void emptyOperationTypesSet() throws Exception
    {
        try
        {
            getSamplingIntervalToTest(0L, 1L, new HashSet<Class<? extends Operation>>(), new ArrayList<String>());
            fail("should fail on account of empty operation types set");
        }
        catch(IllegalArgumentException iae)
        {
            log.info(iae.getMessage());
        }
    }

    @Test
    public void nullAnnotationsList() throws Exception
    {
        SamplingIntervalImpl si = getSamplingIntervalToTest(
            0L, 1L, new HashSet<Class<? extends Operation>>(Collections.singletonList(MockOperation.class)), null);

        List<String> annotations = si.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.isEmpty());
    }

    @Test
    public void setCounterValues_and_addAnnotation() throws Exception
    {
        SamplingIntervalImpl si = getSamplingIntervalToTest(
            0L, 1L, new HashSet<Class<? extends Operation>>(Collections.singletonList(MockOperation.class)), null);


        List<String> annotations = si.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.isEmpty());

        CounterValues values = si.getCounterValues(MockOperation.class);

        // make sure there's a zero-value CounterValues even if nothing has been recorded yet
        assertNotNull(values);
        assertEquals(0L, values.getSuccessCount());
        assertEquals(0L, values.getSuccessCumulatedDurationNano());

        si.setCounterValues(MockOperation.class, new CounterValuesImpl(7L, 11L, null));
        si.addAnnotation("blah");
        si.addAnnotation("blah2");

        values = si.getCounterValues(MockOperation.class);
        assertEquals(7L, values.getSuccessCount());
        assertEquals(11L, values.getSuccessCumulatedDurationNano());

        annotations = si.getAnnotations();
        assertEquals(2, annotations.size());
        assertEquals("blah", annotations.get(0));
        assertEquals("blah2", annotations.get(1));
    }

    // incrementCounterValues() ----------------------------------------------------------------------------------------

    @Test
    public void incrementCounterValues() throws Exception
    {
        Set<Class<? extends Operation>> operationTypes =
            new HashSet<>(Arrays.asList(MockOperation.class, AnotherTypeOfMockOperation.class));

        SamplingIntervalImpl si = new SamplingIntervalImpl(0L, 1L, operationTypes);

        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));

        CounterValues cv = new CounterValuesImpl(5L, 6L, failureCounters);

        si.incrementCounterValues(MockOperation.class, cv);

        // increment has "set" semantics, so verify that

        Set<Class<? extends Throwable>> failureTypes;

        CounterValues cv1 = si.getCounterValues(MockOperation.class);
        assertEquals(5L, cv1.getSuccessCount());
        assertEquals(6L, cv1.getSuccessCumulatedDurationNano());
        assertEquals(1L + 3L, cv1.getFailureCount());
        assertEquals(2L + 4L, cv1.getFailureCumulatedDurationNano());
        failureTypes = cv1.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(1L, cv1.getFailureCount(SocketException.class));
        assertEquals(2L, cv1.getFailureCumulatedDurationNano(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(3L, cv1.getFailureCount(ConnectException.class));
        assertEquals(4L, cv1.getFailureCumulatedDurationNano(ConnectException.class));

        CounterValues cv2 = si.getCounterValues(AnotherTypeOfMockOperation.class);

        // there was no set or increment, must next counters set to zero

        assertEquals(0L, cv2.getSuccessCount());
        assertEquals(0L, cv2.getSuccessCumulatedDurationNano());
        assertEquals(0L, cv2.getFailureCount());
        assertEquals(0L, cv2.getFailureCumulatedDurationNano());
        assertTrue(cv2.getFailureTypes().isEmpty());

        // increment the MockOperation counters one more time and for the first time AnotherTypeOfMockOperation's

        failureCounters = new HashMap<>();
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(7L, 8L));
        failureCounters.put(IOException.class, new ImmutableFailureCounter(9L, 10L));
        cv = new CounterValuesImpl(11L, 12L, failureCounters);

        si.incrementCounterValues(MockOperation.class, cv);

        CounterValues cv3 = si.getCounterValues(MockOperation.class);
        assertEquals(5L + 11L, cv3.getSuccessCount());
        assertEquals(6L + 12L, cv3.getSuccessCumulatedDurationNano());
        assertEquals(1L + 3L + 7L + 9L, cv3.getFailureCount());
        assertEquals(2L + 4L + 8L + 10L, cv3.getFailureCumulatedDurationNano());
        failureTypes = cv3.getFailureTypes();
        assertEquals(3, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(1L, cv3.getFailureCount(SocketException.class));
        assertEquals(2L, cv3.getFailureCumulatedDurationNano(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(3L + 7L, cv3.getFailureCount(ConnectException.class));
        assertEquals(4L + 8L, cv3.getFailureCumulatedDurationNano(ConnectException.class));
        assertTrue(failureTypes.contains(IOException.class));
        assertEquals(9L, cv3.getFailureCount(IOException.class));
        assertEquals(10L, cv3.getFailureCumulatedDurationNano(IOException.class));

        failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(13L, 14L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(15L, 16L));

        cv = new CounterValuesImpl(17L, 18L, failureCounters);

        si.incrementCounterValues(AnotherTypeOfMockOperation.class, cv);

        CounterValues cv4 = si.getCounterValues(AnotherTypeOfMockOperation.class);
        assertEquals(17L, cv4.getSuccessCount());
        assertEquals(18L, cv4.getSuccessCumulatedDurationNano());
        assertEquals(13L + 15L, cv4.getFailureCount());
        assertEquals(14L + 16L, cv4.getFailureCumulatedDurationNano());
        failureTypes = cv4.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(13L, cv4.getFailureCount(SocketException.class));
        assertEquals(14L, cv4.getFailureCumulatedDurationNano(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(15L, cv4.getFailureCount(ConnectException.class));
        assertEquals(16L, cv4.getFailureCumulatedDurationNano(ConnectException.class));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected SamplingIntervalImpl getSamplingIntervalToTest(
        long intervalStartTimestamp, long durationMs, Set<Class<? extends Operation>> operationTypes,
        List<String> annotations)
        throws Exception
    {
        SamplingIntervalImpl result = new SamplingIntervalImpl(intervalStartTimestamp, durationMs, operationTypes);
        if (annotations != null)
        {
            for(String s: annotations)
            {
                result.addAnnotation(s);
            }
        }
        return result;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
