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

import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class SamplingIntervalTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void happyPath() throws Exception
    {
        SamplingInterval si = getSamplingIntervalToTest(
            7L, 11L, new HashSet<Class>(Arrays.asList(MockOperation.class)), Arrays.asList("blah"));

        assertEquals(7L, si.getTimestamp());

        assertEquals(11L, si.getDuration());

        Set<Class> operationTypes = si.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));

        // make sure we return null and not throw any exception on unknown operation types
        assertNull(si.getCounterValues(Object.class));

        CounterValues values = si.getCounterValues(MockOperation.class);
        // since we did not recorded any operations, I expect the values to be 0
        assertEquals(0, values.getSuccessCount());
        assertEquals(0, values.getSuccessCumulatedTime());

        List<String> annotations = si.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains("blah"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract SamplingInterval getSamplingIntervalToTest(
        long intervalStartTimestamp, long durationMs, Set<Class> operationTypes, List<String> annotations)
        throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
