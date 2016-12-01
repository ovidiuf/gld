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

import com.novaordis.gld.Operation;
import com.novaordis.gld.sampler.CounterValuesImpl;
import com.novaordis.gld.sampler.SamplingIntervalImpl;
import com.novaordis.gld.sampler.metrics.FreePhysicalMemorySize;
import com.novaordis.gld.sampler.metrics.MeasureUnit;
import com.novaordis.gld.sampler.metrics.Metric;
import com.novaordis.gld.sampler.metrics.MockMetric;
import com.novaordis.gld.sampler.metrics.SystemCpuLoad;
import com.novaordis.gld.sampler.metrics.SystemLoadAverage;
import com.novaordis.gld.sampler.metrics.TotalPhysicalMemorySize;
import com.novaordis.gld.strategy.load.cache.AnotherTypeOfMockOperation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CSVFormatterTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CSVFormatterTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void toLine_OneOperation_NoFailures() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);

        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(777L, 777L * 220000L));
        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(2L * 1024 * 1024 * 1024));
        metrics.add(new TotalPhysicalMemorySize(8L * 1024 * 1024 * 1024));
        metrics.add(new SystemCpuLoad(50.5));
        metrics.add(new SystemLoadAverage(4.4));
        si.setMetrics(metrics);
        si.addAnnotation("this is an annotation");

        String expected =
            "Time, " +
                "MockOperation Success Rate (ops/sec), " +
                "MockOperation Average Duration (ms), " +
                "MockOperation Failure Rate (ops/sec), " +
                "System Load Average, " +
                "System CPU Load (%), " +
                "Free O/S Physical Memory (GB), " +
                "Total O/S Physical Memory (GB), " +
                "Notes";


        CSVFormat csvFormat = new CSVFormat();
        csvFormat.setMemoryUnit(MeasureUnit.GIGABYTE);

        String headers = CSVFormatter.toLine(si, csvFormat, true);

        log.info(expected);
        log.info(headers);

        assertEquals(expected, headers);

        String line = CSVFormatter.toLine(si, csvFormat, false);

        expected =
            CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", " +
                "777, " +
                "0.22, " +
                "0, " +
                "4.4, " +
                "50.5, " +
                "2.0, " +
                "8.0, " +
                "this is an annotation";

        log.info(expected);
        log.info(line);

        assertEquals(expected, line);
    }

    // @Test
    public void toLine_OneOperation_ThereAreFailures_FailureKindVariesOverTime() throws Exception
    {
        fail("RETURN HERE WHEN WORKING ON FAILURE HANDLING");
    }

    @Test
    public void protectionToNullMetrics() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(1L, 1L));

        assertNull(si.getMetrics());

        CSVFormat csvFormat = new CSVFormat();
        String line = CSVFormatter.toLine(si, csvFormat, false);
        String expected = CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", 1, 0.0, 0";

        log.info(expected);
        log.info(line);

        assertTrue(line.startsWith(expected));
    }

    @Test
    public void moreThanOneAnnotationPerSamplingInterval() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(1L, 1L));
        si.addAnnotation("ANNOTATION 1");
        si.addAnnotation("ANNOTATION 2");

        CSVFormat csvFormat = new CSVFormat();
        String line = CSVFormatter.toLine(si, csvFormat, false);
        String expected = CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", 1, 0.0, 0, ANNOTATION 1; ANNOTATION 2";

        log.info("expected: " + expected);
        log.info("produced: " + line);

        assertEquals(expected, line);
    }

    @Test
    public void annotationContainsComma() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(1L, 1L));
        si.addAnnotation("ANNOTATION 1, ANNOTATION 2");

        CSVFormat csvFormat = new CSVFormat();
        String line = CSVFormatter.toLine(si, csvFormat, false);
        String expected = CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", 1, 0.0, 0, \"ANNOTATION 1, ANNOTATION 2\"";

        log.info("expected: " + expected);
        log.info("produced: " + line);

        assertEquals(expected, line);
    }

    @Test
    public void toLine_TwoOperations_NoFailures() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        operationTypes.add(AnotherTypeOfMockOperation.class);

        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);

        si.setCounterValues(MockOperation.class, new CounterValuesImpl(17L, 17L * 500000L));
        si.setCounterValues(AnotherTypeOfMockOperation.class, new CounterValuesImpl(19L, 19L * 600000L));

        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(2L * 1024 * 1024 * 1024));
        metrics.add(new TotalPhysicalMemorySize(8L * 1024 * 1024 * 1024));
        metrics.add(new SystemCpuLoad(50.5));
        metrics.add(new SystemLoadAverage(4.4));
        si.setMetrics(metrics);
        si.addAnnotation("this is an annotation");

        String expected =
            "Time, " +
                "AnotherTypeOfMockOperation Success Rate (ops/sec), " +
                "AnotherTypeOfMockOperation Average Duration (ms), " +
                "AnotherTypeOfMockOperation Failure Rate (ops/sec), " +
                "MockOperation Success Rate (ops/sec), " +
                "MockOperation Average Duration (ms), " +
                "MockOperation Failure Rate (ops/sec), " +
                "System Load Average, " +
                "System CPU Load (%), " +
                "Free O/S Physical Memory (GB), " +
                "Total O/S Physical Memory (GB), " +
                "Notes";


        CSVFormat csvFormat = new CSVFormat();
        csvFormat.setMemoryUnit(MeasureUnit.GIGABYTE);

        String headers = CSVFormatter.toLine(si, csvFormat, true);

        log.info(expected);
        log.info(headers);

        assertEquals(expected, headers);

        String line = CSVFormatter.toLine(si, csvFormat, false);

        expected =
            CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", " +
                "19, " +
                "0.6, " +
                "0, " +
                "17, " +
                "0.5, " +
                "0, " +
                "4.4, " +
                "50.5, " +
                "2.0, " +
                "8.0, " +
                "this is an annotation";

        log.info(expected);
        log.info(line);

        assertEquals(expected, line);
    }

    // @Test
    public void toLine_TwoOperations_ThereAreFailures_FailureKindVariesOverTime() throws Exception
    {
        fail("RETURN HERE WHEN WORKING ON FAILURE HANDLING");
    }

    @Test
    public void toCsvLine_Headers() throws Exception
    {
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        operationTypes.add(AnotherTypeOfMockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(0L, 1000L, operationTypes);

        MockMetric mm = new MockMetric("some memory metric", MeasureUnit.MEGABYTE);
        Set<Metric> metrics = new HashSet<>();
        metrics.add(mm);
        si.setMetrics(metrics);

        // "Notes" header present even without annotations
        assertTrue(si.getAnnotations().isEmpty());

        CSVFormat customCSVFormat = new CSVFormat();

        customCSVFormat.setAverageOperationDurationTimeUnit(MeasureUnit.SECOND);
        customCSVFormat.setMemoryUnit(MeasureUnit.KILOBYTE);

        String headers = CSVFormatter.toLine(si, customCSVFormat, true);

        String expected =
            "Time, " +
                "AnotherTypeOfMockOperation Success Rate (ops/sec), " +
                "AnotherTypeOfMockOperation Average Duration (s), " +
                "AnotherTypeOfMockOperation Failure Rate (ops/sec), " +
                "MockOperation Success Rate (ops/sec), " +
                "MockOperation Average Duration (s), " +
                "MockOperation Failure Rate (ops/sec), " +
                "some memory metric (KB), " +
                "Notes";

        log.info("expected: " + expected);
        log.info("produced: " + headers);

        assertEquals(expected, headers);

        String headers2 = CSVFormatter.toLine(si, customCSVFormat, true);

        assertEquals(expected, headers2);
    }

    @Test
    public void toCsvLine_CommentOnly() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);

        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(0L, 0L));
        si.addAnnotation("this is an annotation");

        String expected =
            CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", " +
                "0, " +
                "0.0, " +
                "0, " +
                "this is an annotation";

        CSVFormat format = new CSVFormat();
        String line = CSVFormatter.toLine(si, format, false);

        log.info(expected);
        log.info(line);

        assertEquals(expected, line);
    }

    @Test
    public void toCsvLine_SampleValuesOnly_NoComment() throws Exception
    {
        long ts = 20000L;
        long durationMs = 1000L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);

        SamplingIntervalImpl si = new SamplingIntervalImpl(ts, durationMs, operationTypes);
        si.setCounterValues(MockOperation.class,
            new CounterValuesImpl(10L, 20L * Statistics.NANOSECONDS_IN_A_MILLISECOND));

        assertTrue(si.getAnnotations().isEmpty());

        String expected =
            CSVFormat.TIMESTAMP_FORMAT.format(ts) + ", " +
                "10, " +
                "2.0, " +
                "0, ";

        CSVFormat format = new CSVFormat();
        String line = CSVFormatter.toLine(si, format, false);

        log.info(expected);
        log.info(line);

        assertEquals(expected, line);
    }

    // stop() ----------------------------------------------------------------------------------------------------------

    @Test
    public void stop_SystemOut() throws Exception
    {
        Writer w = new OutputStreamWriter(System.out);

        CSVFormatter csvFormatter = new CSVFormatter(w);

        csvFormatter.stop();
    }

    @Test
    public void stop_MyWriter() throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean(false);

        Writer w = new Writer()
        {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException
            {
            }

            @Override
            public void flush() throws IOException
            {
            }

            @Override
            public void close() throws IOException
            {
                closed.set(true);
            }
        };

        CSVFormatter csvFormatter = new CSVFormatter(w);

        csvFormatter.stop();

        assertTrue(closed.get());


    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
