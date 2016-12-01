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

package com.novaordis.gld.sampler.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MeasureUnitTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void kilobyte() throws Exception
    {
        MeasureUnit u = MeasureUnit.KILOBYTE;
        assertEquals("KB", u.abbreviation());
        assertEquals(MetricType.MEMORY, u.getMetricType());
    }

    @Test
    public void millisecond() throws Exception
    {
        MeasureUnit u = MeasureUnit.MILLISECOND;
        assertEquals("ms", u.abbreviation());
        assertEquals(MetricType.TIME, u.getMetricType());
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
