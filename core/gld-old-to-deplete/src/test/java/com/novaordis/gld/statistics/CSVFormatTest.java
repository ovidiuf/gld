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

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.driver.sampler.metrics.FreePhysicalMemorySize;
import io.novaordis.gld.driver.sampler.metrics.MeasureUnit;
import io.novaordis.gld.driver.sampler.metrics.Metric;
import io.novaordis.gld.driver.sampler.metrics.MetricType;
import io.novaordis.gld.driver.sampler.metrics.MockMeasureUnit;
import io.novaordis.gld.driver.sampler.metrics.MockMetric;
import io.novaordis.gld.driver.sampler.metrics.SystemCpuLoad;
import io.novaordis.gld.driver.sampler.metrics.SystemLoadAverage;
import io.novaordis.gld.driver.sampler.metrics.TotalPhysicalMemorySize;
import com.novaordis.gld.strategy.load.cache.AnotherTypeOfMockOperation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CSVFormatTest extends FormatTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CSVFormatTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void timestampLabel() throws Exception
    {
        CSVFormat format = getFormatToTest();
        assertEquals(CSVFormat.TIMESTAMP_HEADER_LABEL, format.getTimestampLabel());
    }

    @Test
    public void formatTimestamp() throws Exception
    {
        CSVFormat format = getFormatToTest();

        long ts = 8452453454L;

        String result = format.formatTimestamp(ts);
        String expected = CSVFormat.TIMESTAMP_FORMAT.format(ts);
        assertEquals(expected, result);
    }

    @Test
    public void orderOperationTypes_NoOperations() throws Exception
    {
        CSVFormat format = getFormatToTest();
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();

        List<Class<? extends Operation>> ordered = format.orderOperationTypes(operationTypes);

        assertTrue(ordered.isEmpty());
    }

    @Test
    public void orderOperationTypes_OneOperations() throws Exception
    {
        CSVFormat format = getFormatToTest();
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);

        List<Class<? extends Operation>> ordered = format.orderOperationTypes(operationTypes);

        assertEquals(1, ordered.size());
        assertEquals(MockOperation.class, ordered.get(0));
    }

    @Test
    public void orderOperationTypes_TwoOperations() throws Exception
    {
        CSVFormat format = getFormatToTest();
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        operationTypes.add(AnotherTypeOfMockOperation.class);

        List<Class<? extends Operation>> ordered = format.orderOperationTypes(operationTypes);

        assertEquals(2, ordered.size());
        assertEquals(AnotherTypeOfMockOperation.class, ordered.get(0));
        assertEquals(MockOperation.class, ordered.get(1));
    }

    @Test
    public void getSuccessRateHeader_NoOperation() throws Exception
    {
        CSVFormat format = getFormatToTest();

        try
        {
            format.getSuccessRateHeader(null);
            fail("should fail, null argument");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getSuccessRateHeader() throws Exception
    {
        CSVFormat format = getFormatToTest();
        String result = format.getSuccessRateHeader(MockOperation.class);
        assertEquals("MockOperation Success Rate (ops/sec)", result);
    }

    @Test
    public void getSuccessAverageDurationHeader_NoOperation() throws Exception
    {
        CSVFormat format = getFormatToTest();

        try
        {
            format.getSuccessAverageDurationHeader(null);
            fail("should fail, null argument");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getSuccessAverageDurationHeader() throws Exception
    {
        CSVFormat format = getFormatToTest();
        format.setAverageOperationDurationTimeUnit(MeasureUnit.NANOSECOND);
        String result = format.getSuccessAverageDurationHeader(MockOperation.class);
        assertEquals("MockOperation Average Duration (ns)", result);
    }

    @Test
    public void getFailureRateHeader_NoOperation() throws Exception
    {
        CSVFormat format = getFormatToTest();

        try
        {
            format.getFailureRateHeader(null);
            fail("should fail, null argument");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getFailureRateHeader() throws Exception
    {
        CSVFormat format = getFormatToTest();
        String result = format.getFailureRateHeader(MockOperation.class);
        assertEquals("MockOperation Failure Rate (ops/sec)", result);
    }

    // metrics ---------------------------------------------------------------------------------------------------------

    @Test
    public void orderMetrics() throws Exception
    {
        CSVFormat format = getFormatToTest();

        Set<Metric> metrics = new HashSet<>();

        Metric m1 = new FreePhysicalMemorySize();
        Metric m2 = new TotalPhysicalMemorySize();
        Metric m3 = new SystemLoadAverage();
        Metric m4 = new SystemCpuLoad();

        metrics.add(m1);
        metrics.add(m2);
        metrics.add(m3);
        metrics.add(m4);

        List<Metric> ordered = format.orderMetrics(metrics);

        assertEquals(4, ordered.size());
        assertEquals(m3, ordered.get(0));
        assertEquals(m4, ordered.get(1));
        assertEquals(m1, ordered.get(2));
        assertEquals(m2, ordered.get(3));
    }

    @Test
    public void getMetricHeader_NoMeasureUnit() throws Exception
    {
        Metric m = new MockMetric("This and Only This Should Appear In Result", null);
        CSVFormat format = getFormatToTest();

        String result = format.getMetricHeader(m);
        assertEquals("This and Only This Should Appear In Result", result);
    }

    @Test
    public void getMetricHeader_NoMeasureUnitAbbreviation() throws Exception
    {
        Metric m = new MockMetric("This and Only This Should Appear In Result", new MockMeasureUnit(null));
        CSVFormat format = getFormatToTest();

        String result = format.getMetricHeader(m);
        assertEquals("This and Only This Should Appear In Result", result);
    }

    @Test
    public void getMetricHeader() throws Exception
    {
        Metric m = new MockMetric("This is a Metric with Measure Unit", new MockMeasureUnit("white"));
        CSVFormat format = getFormatToTest();

        String result = format.getMetricHeader(m);
        assertEquals("This is a Metric with Measure Unit (white)", result);
    }

    @Test
    public void getMetricHeader_Memory() throws Exception
    {
        Metric memoryMetric =
            new MockMetric("This is a Memory Metric", new MockMeasureUnit("white"), MetricType.MEMORY);

        CSVFormat format = getFormatToTest();
        format.setMemoryUnit(MeasureUnit.GIGABYTE);
        assertEquals(MeasureUnit.GIGABYTE, format.getMemoryUnit());

        String result = format.getMetricHeader(memoryMetric);
        assertEquals("This is a Memory Metric (GB)", result);
    }

    @Test
    public void getMetricHeader_MetricNameContainsCommas() throws Exception
    {
        MeasureUnit customTimeMeasureUnit = new MockMeasureUnit(MetricType.TIME, "supersec");
        Metric timeMetric = new MockMetric("This is a, Metric, whose Name contains commas,", customTimeMeasureUnit);

        assertEquals(MetricType.TIME, timeMetric.getMetricType());

        CSVFormat format = getFormatToTest();

        String expected = "\"This is a, Metric, whose Name contains commas, (supersec)\"";
        String result = format.getMetricHeader(timeMetric);

        log.info("expected: " + expected);
        log.info("produced: " + result);

        assertEquals(expected, result);
    }

    @Test
    public void formatMetric_MEMORY() throws Exception
    {
        CSVFormat format = getFormatToTest();

        MockMetric m = new MockMetric("MOCK MEMORY", MeasureUnit.BYTE);
        m.setValue(8L * 1024 * 1024 * 1024);
        format.setMemoryUnit(MeasureUnit.GIGABYTE);


        String result = format.formatMetric(m);
        log.info(result);
        assertEquals("8.0", result);
    }

    // notes -----------------------------------------------------------------------------------------------------------

    @Test
    public void notesLabel() throws Exception
    {
        CSVFormat format = getFormatToTest();
        assertEquals(CSVFormat.NOTES_HEADER_LABEL, format.getNotesHeader());
    }

    // formatRate() ----------------------------------------------------------------------------------------------------

    @Test
    public void formatRate() throws Exception
    {
        CSVFormat format = getFormatToTest();
        String result = format.formatRate(777.0, MeasureUnit.SECOND);
        assertEquals("777", result);
    }

    @Test
    public void formatRate2() throws Exception
    {
        CSVFormat format = getFormatToTest();
        String result = format.formatRate(123.456, MeasureUnit.SECOND);
        assertEquals("123", result);
    }

    @Test
    public void formatRate3() throws Exception
    {
        CSVFormat format = getFormatToTest();
        String result = format.formatRate(77.999, MeasureUnit.SECOND);
        assertEquals("78", result);
    }

    // formatAverageDuration() -----------------------------------------------------------------------------------------

    @Test
    public void formatAverageDuration_NANOSECOND_to_MILLISECOND() throws Exception
    {
        CSVFormat format = getFormatToTest();
        format.setAverageOperationDurationTimeUnit(MeasureUnit.MILLISECOND);

        String result = format.formatAverageDuration(5560000.0, MeasureUnit.NANOSECOND);
        assertEquals("5.56", result);
    }

    @Test
    public void formatAverageDuration_MILLISECOND_to_MILLISECOND() throws Exception
    {
        CSVFormat format = getFormatToTest();
        format.setAverageOperationDurationTimeUnit(MeasureUnit.MILLISECOND);

        String result = format.formatAverageDuration(0.11, MeasureUnit.MILLISECOND);
        assertEquals("0.11", result);
    }

    // formatNotes() ---------------------------------------------------------------------------------------------------

    @Test
    public void formatNotes_NullList() throws Exception
    {
        CSVFormat format = getFormatToTest();
        assertEquals("", format.formatNotes(null));
    }

    @Test
    public void formatNotes_EmptyList() throws Exception
    {
        CSVFormat format = getFormatToTest();
        assertEquals("", format.formatNotes(new ArrayList<String>()));
    }

    @Test
    public void formatNotes_OneElement() throws Exception
    {
        CSVFormat format = getFormatToTest();

        String result = format.formatNotes(Arrays.asList("this is an annotation"));
        assertEquals("this is an annotation", result);
    }

    @Test
    public void formatNotes_MultipleElements() throws Exception
    {
        CSVFormat format = getFormatToTest();

        String result = format.formatNotes(Arrays.asList("this is an annotation", "this is another annotation"));
        assertEquals("this is an annotation; this is another annotation", result);
    }

    @Test
    public void formatNotes_NoteContainsComma() throws Exception
    {
        CSVFormat format = getFormatToTest();

        String expected = "\"this, contains, several, commas\"";
        String result = format.formatNotes(Arrays.asList("this, contains, several, commas"));

        log.info("expected: " + expected);
        log.info("produced: " + result);
        assertEquals(expected, result);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @see FormatTest#getFormatToTest()
     */
    @Override
    protected CSVFormat getFormatToTest() throws Exception
    {
        return new CSVFormat();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
