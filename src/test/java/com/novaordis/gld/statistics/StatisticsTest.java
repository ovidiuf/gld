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

package com.novaordis.gld.statistics;

import com.novaordis.gld.sampler.metrics.MeasureUnit;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StatisticsTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(StatisticsTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void calculateRate() throws Exception
    {
        double rate = Statistics.calculateRate(100, 500L * 1000000, MeasureUnit.NANOSECOND, MeasureUnit.SECOND);
        assertEquals(200, rate, 0.0001);
    }

    @Test
    public void calculateAverageDuration() throws Exception
    {
        long count = 77;
        long durationPerCall = 5550000L; // 5.55 ms
        double ad = Statistics.calculateAverageDuration(
            count, count * durationPerCall, MeasureUnit.NANOSECOND, MeasureUnit.MILLISECOND);

        assertEquals(5.55, ad, 0.0001);
    }

    @Test
    public void calculateAverageDuration_ZeroCount() throws Exception
    {
        long count = 0;
        long duration = 0L;
        double ad = Statistics.calculateAverageDuration(count, duration, MeasureUnit.NANOSECOND, MeasureUnit.MILLISECOND);

        assertEquals(0, ad, 0.0001);
    }

    // multiplicationFactor --------------------------------------------------------------------------------------------

    @Test
    public void multiplicationFactor_NANOSECOND_to_SECOND() throws Exception
    {
        assertEquals(1000L * 1000L * 1000L, Statistics.multiplicationFactor(MeasureUnit.NANOSECOND, MeasureUnit.SECOND));
    }

    @Test
    public void multiplicationFactor_NANOSECOND_to_MILLISECOND() throws Exception
    {
        assertEquals(1000L * 1000L, Statistics.multiplicationFactor(MeasureUnit.NANOSECOND, MeasureUnit.MILLISECOND));
    }

    @Test
    public void multiplicationFactor_MILLISECOND_to_MILLISECOND() throws Exception
    {
        assertEquals(1L, Statistics.multiplicationFactor(MeasureUnit.MILLISECOND, MeasureUnit.MILLISECOND));
    }

    @Test
    public void multiplicationFactor_MILLISECOND_to_SECOND() throws Exception
    {
        assertEquals(1000L, Statistics.multiplicationFactor(MeasureUnit.MILLISECOND, MeasureUnit.SECOND));
    }

    @Test
    public void multiplicationFactor_BYTE_to_KILOBYTE() throws Exception
    {
        assertEquals(1024L, Statistics.multiplicationFactor(MeasureUnit.BYTE, MeasureUnit.KILOBYTE));
    }

    @Test
    public void multiplicationFactor_BYTE_to_MEGABYTE() throws Exception
    {
        assertEquals(1024L * 1024L, Statistics.multiplicationFactor(MeasureUnit.BYTE, MeasureUnit.MEGABYTE));
    }

    @Test
    public void multiplicationFactor_BYTE_to_GIGABYTE() throws Exception
    {
        assertEquals(1024L * 1024L * 1024L, Statistics.multiplicationFactor(MeasureUnit.BYTE, MeasureUnit.GIGABYTE));
    }

    @Test
    public void multiplicationFactor_incompatible_sourceNullType() throws Exception
    {
        try
        {
            Statistics.multiplicationFactor(MeasureUnit.NONE, MeasureUnit.SECOND);
            fail("should fail, the units are incompatible");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void multiplicationFactor_incompatible_destinationNullType() throws Exception
    {
        try
        {
            Statistics.multiplicationFactor(MeasureUnit.NANOSECOND, MeasureUnit.NONE);
            fail("should fail, the units are incompatible");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void multiplicationFactor_incompatible() throws Exception
    {
        try
        {
            Statistics.multiplicationFactor(MeasureUnit.SECOND, MeasureUnit.GIGABYTE);
            fail("should fail, the units are incompatible");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    // convert ---------------------------------------------------------------------------------------------------------

    @Test
    public void convert() throws Exception
    {
        long value = 8L * 1024L * 1024L * 1024L; // 8 GB
        double result = Statistics.convert(value, MeasureUnit.BYTE, MeasureUnit.GIGABYTE);
        assertEquals(8.0, result, 0.001);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
