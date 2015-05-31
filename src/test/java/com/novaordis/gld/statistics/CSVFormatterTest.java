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
import com.novaordis.gld.sampler.metrics.SystemCpuLoad;
import com.novaordis.gld.sampler.metrics.SystemLoadAverage;
import com.novaordis.gld.sampler.metrics.TotalPhysicalMemorySize;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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
        si.setCounterValues(MockOperation.class, new CounterValuesImpl(777L, 777L * 220000L, durationMs * 1000000L));
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

    @Test
    public void toLine_OneOperation_ThereAreFailures_FailureKindVariesOverTime() throws Exception
    {
        fail("RETURN HERE");
    }

    @Test
    public void moreThanOneAnnotationPerSamplingInterval() throws Exception
    {
        fail("RETURN HERE");
    }

    @Test
    public void annotationContainsComma() throws Exception
    {
        fail("RETURN HERE");
    }

    @Test
    public void toLine_TwoOperations() throws Exception
    {
        fail("RETURN HERE");
    }

    @Test
    public void toLine_TwoOperations_ThereAreFailures_FailureKindVariesOverTime() throws Exception
    {
        fail("RETURN HERE");
    }

    @Test
    public void toCsvLine_Headers() throws Exception
    {
        fail("RETURN HERE");
//        String expected =
//            "time, throughput (ops/sec), reads/sec, hits/sec, hits (%), writes/sec,    average read duration (ms), average write duration (ms),    other failures count, 'connection refused' count, 'broken pipe' count, 'connection reset' count, 'connect timed out' count, 'read timed out' count, jedis 'closed connection' count, jedis 'unknown reply' count, jedis 'max number of clients reached' count, jedis 'connection timed out' count, pool out count,    system load average, system cpu load, process cpu load, used heap (MB), committed heap (MB), comments";
//
//        String headers = DeprecatedSamplingInterval.toCsvLine(
//            true, -1L, null, null, null, null, null,
//            RedisFailure.EMPTY_COUNTERS,
//            null, null, null, null,
//            null, null,
//            null);
//
//        assertEquals(expected, headers);
//
//        String headers2 = DeprecatedSamplingInterval.getCsvHeaders();
//
//        assertEquals(expected, headers2);
    }

    @Test
    public void toCsvLine_EmptyLine() throws Exception
    {
        fail("RETURN HERE");

//        long timestamp = 17L;
//
//        String emptyLine = DeprecatedSamplingInterval.toCsvLine(
//            false, timestamp, null, null, null, null, null,
//            RedisFailure.NULL_COUNTERS,
//            null, null, null, null,
//            null, null,
//            null);
//
//        String expected = CollectorBasedCsvStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) + ", , , , , ,    , ,    , , , , , , , , , , ,    , , , , , ";
//
//        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_CommentOnly() throws Exception
    {
        fail("RETURN HERE");

//        long timestamp = 18L;
//
//        String emptyLine = DeprecatedSamplingInterval.toCsvLine(
//            false, timestamp, null, null, null, null, null,
//            RedisFailure.NULL_COUNTERS,
//            null, null, null, null,
//            null, null,
//            "this is a comment");
//
//        String expected = CollectorBasedCsvStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) + ", , , , , ,    , ,    , , , , , , , , , , ,    , , , , , this is a comment";
//
//        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_SampleValuesOnly_NoComment() throws Exception
    {
        fail("RETURN HERE");

//        long timestamp = 18L;
//
//        String emptyLine = DeprecatedSamplingInterval.toCsvLine(
//            false, timestamp, 1L, 2L, 3L, 4L, 5L,
//            new Long[]{10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L},
//            31.1, 32.2, 33.3, 34.4,
//            50L, 51L,
//            null);
//
//        String expected = CollectorBasedCsvStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) +
//            ", 4, 1, 2, 200.00%, 3,    .0, .0,    10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,    31.10, 32.20, 33.30, .00, .00, ";
//
//        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_SampleValuesAndComment() throws Exception
    {
        fail("RETURN HERE");

//        long timestamp = 18L;
//
//        String emptyLine = DeprecatedSamplingInterval.toCsvLine(
//            false, timestamp, 1L, 2L, 3L, 4L, 5L,
//            new Long[]{10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L},
//            31.1, 32.2, 33.3, 34.4,
//            50L, 51L,
//            "this is a comment");
//
//        String expected = CollectorBasedCsvStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) +
//            ", 4, 1, 2, 200.00%, 3,    .0, .0,    10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,    31.10, 32.20, 33.30, .00, .00, this is a comment";
//
//        assertEquals(expected, emptyLine);
    }

    @Test
    public void completion_processedBeforeWait() throws Exception
    {
        fail("RETURN HERE");

//        DeprecatedSamplingInterval si = new DeprecatedSamplingInterval(0, 0, 0, 0, 0, 0, new long[0], 0, 0, 0, 0, 0, 0);
//
//        si.markProcessed();
//
//        si.waitUntilProcessed();
    }

    @Test
    public void completion_processedAfterWait() throws Exception
    {
        fail("RETURN HERE");

//        final DeprecatedSamplingInterval si = new DeprecatedSamplingInterval(0, 0, 0, 0, 0, 0, new long[0], 0, 0, 0, 0, 0, 0);
//        final BlockingQueue<Long> rendezvous = new ArrayBlockingQueue<>(1);
//
//        long t0 = System.currentTimeMillis();
//
//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                si.waitUntilProcessed();
//
//                try
//                {
//                    rendezvous.put(System.currentTimeMillis());
//                }
//                catch(Exception e)
//                {
//                    log.error(e);
//                }
//            }
//        }, "test thread that waits until the sampling interval is processed").start();
//
//        // sleep a bit and make sure the waiting thread does not skip waiting
//        long sleep = 100L;
//        Thread.sleep(sleep);
//
//        assertNull(rendezvous.peek());
//
//        si.markProcessed();
//
//        // at this point the waiting thread gets released, and the difference between the release time and t0
//        // must be higher or equal to 'sleep'
//
//        Long result = rendezvous.take();
//        assertNotNull(result);
//        assertTrue(result - t0 >= sleep);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
