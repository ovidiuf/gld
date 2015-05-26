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
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SamplingIntervalImplTest extends SamplingIntervalTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplingIntervalImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullOperationTypesSet() throws Exception
    {
        try
        {
            getSamplingIntervalToTest(0L, 1L, null, new ArrayList<String>());
            fail("should fail on account of null operation types set");
        }
        catch(IllegalArgumentException iae)
        {
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
            0L, 1L, new HashSet<Class<? extends Operation>>(Arrays.asList(MockOperation.class)), null);

        List<String> annotations = si.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.isEmpty());
    }

    @Test
    public void setCounterValues_and_addAnnotation() throws Exception
    {
        SamplingIntervalImpl si = getSamplingIntervalToTest(
            0L, 1L, new HashSet<Class<? extends Operation>>(Arrays.asList(MockOperation.class)), null);


        List<String> annotations = si.getAnnotations();
        assertNotNull(annotations);
        assertTrue(annotations.isEmpty());

        CounterValues values = si.getCounterValues(MockOperation.class);

        // make sure there's a zero-value CounterValues even if nothing has been recorded yet
        assertNotNull(values);
        assertEquals(0L, values.getSuccessCount());
        assertEquals(0L, values.getSuccessCumulatedDuration());

        si.setCounterValues(MockOperation.class, new CounterValuesImpl(7L, 11L, null));
        si.addAnnotation("blah");
        si.addAnnotation("blah2");

        values = si.getCounterValues(MockOperation.class);
        assertEquals(7L, values.getSuccessCount());
        assertEquals(11L, values.getSuccessCumulatedDuration());

        annotations = si.getAnnotations();
        assertEquals(2, annotations.size());
        assertEquals("blah", annotations.get(0));
        assertEquals("blah2", annotations.get(1));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected SamplingIntervalImpl getSamplingIntervalToTest(
        long intervalStartTimestamp, long durationMs, Set<Class<? extends Operation>> operationTypes,
        List<String> annotations)
        throws Exception
    {
        return new SamplingIntervalImpl(intervalStartTimestamp, durationMs, operationTypes, annotations);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
