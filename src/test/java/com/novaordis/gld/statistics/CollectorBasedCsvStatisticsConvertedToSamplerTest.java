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

import com.novaordis.gld.operations.cache.Read;
import com.novaordis.gld.operations.cache.Write;
import com.novaordis.gld.sampler.CounterValues;
import com.novaordis.gld.sampler.MockSamplingConsumer;
import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.sampler.SamplerImpl;
import com.novaordis.gld.sampler.SamplingInterval;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CollectorBasedCsvStatisticsConvertedToSamplerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CollectorBasedCsvStatisticsConvertedToSamplerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Simulates a CollectorBasedStatistics lifecycle, as used in production.
     */
    @Test
    public void lifeCycleIntegrationTest() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));
        CSVFormatter csvFormatter = new CSVFormatter(pw);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);
        s.registerConsumer(csvFormatter);

        s.start();

        MockOperation mo = new MockOperation();

        // the sampling interval will start on the second mark right before this time:
        long t0 = System.currentTimeMillis();

        // 2 ms
        //noinspection PointlessArithmeticExpression
        s.record(1L, 1L * 1000000L, 3L * 1000000L, mo);

        // we don't allow it even a sampling interval - this is to make sure we capture even the fast
        // single operations
        s.stop();

        // make sure we can't call record anymore

        try
        {
            s.record(System.currentTimeMillis(), 0L, 0L, mo);
            fail("should have failed with IllegalStateException, sampler is stopped");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        // make sure we get the headers and statistics for 1 operation

        String output = new String(baos.toByteArray());
        log.info(output);

        StringTokenizer st = new StringTokenizer(output, "\n");
        assertTrue(st.hasMoreTokens());
        String headers = st.nextToken();
        StringTokenizer lineTokenizer = new StringTokenizer(headers, ",");

        assertTrue(lineTokenizer.hasMoreTokens());
        String token = lineTokenizer.nextToken().trim();
        assertEquals("Time", token);

        assertTrue(lineTokenizer.hasMoreTokens());
        token = lineTokenizer.nextToken().trim();
        assertEquals("MockOperation Success Rate (ops/sec)", token);

        assertTrue(lineTokenizer.hasMoreTokens());
        token = lineTokenizer.nextToken().trim();
        assertEquals("MockOperation Average Duration (ms)", token);

        assertTrue(lineTokenizer.hasMoreTokens());
        token = lineTokenizer.nextToken().trim();
        assertEquals("MockOperation Failure Rate (ops/sec)", token);

        assertTrue(lineTokenizer.hasMoreTokens());
        token = lineTokenizer.nextToken().trim();
        assertEquals("Notes", token);

        assertFalse(lineTokenizer.hasMoreTokens());

        assertTrue(st.hasMoreTokens());
        String samples = st.nextToken();
        assertFalse(st.hasMoreTokens());

        // make sure we record one sample
        lineTokenizer = new StringTokenizer(samples, ",");
        String timestamp = lineTokenizer.nextToken();
        long t = ((Date)CSVFormat.TIMESTAMP_FORMAT.parseObject(timestamp)).getTime();
        long secondMark = (t0 / 1000L) * 1000L;
        assertEquals(secondMark, t);

        // success count
        String success = lineTokenizer.nextToken();
        assertEquals("1", success.trim());

        // duration
        String duration = lineTokenizer.nextToken();
        assertEquals("2.0", duration.trim());
    }

    // record ----------------------------------------------------------------------------------------------------------

    @Test
    public void recordSimpleRead() throws Exception
    {
        MockSamplingConsumer mc = new MockSamplingConsumer();

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(Read.class);
        s.registerConsumer(mc);

        s.start();

        Read r = new Read("a");

        s.record(0L, 10L, 20L, r);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        Read r2 = new Read("b");

        s.record(2L, 10L, 20L, r2);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        Read r3 = new Read("c");

        s.record(4L, 10L, 20L, r3);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        // this triggers collection and a SamplingInterval to be sent to consumer

        //
        // WARNING: IF DEBUGGING, WE'LL ACCUMULATE MORE THAN ONE SAMPLE
        //

        s.stop();

        List<SamplingInterval> samplingIntervals = mc.getSamplingIntervals();

        assertEquals(1, samplingIntervals.size());

        SamplingInterval i = samplingIntervals.get(0);

        CounterValues cv = i.getCounterValues(Read.class);

        assertEquals(3, cv.getSuccessCount());
        assertEquals(30L, cv.getSuccessCumulatedDurationNano());
    }

    @Test
    public void recordCombinedReadAndWrite() throws Exception
    {
        MockSamplingConsumer mc = new MockSamplingConsumer();

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(Read.class);
        s.registerOperation(Write.class);
        s.registerConsumer(mc);

        s.start();

        Read r = new Read("a");

        s.record(0L, 10L, 11L, r);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        Write w = new Write("TEST-KEY", "TEST-VALUE");

        s.record(2L, 20L, 22L, w);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        Read r2 = new Read("b");

        s.record(4L, 30L, 33L, r2);

        assertTrue(mc.getSamplingIntervals().isEmpty());

        // this triggers collection and a SamplingInterval to be sent to consumer

        //
        // WARNING: IF DEBUGGING, WE'LL ACCUMULATE MORE THAN ONE SAMPLE
        //

        s.stop();

        List<SamplingInterval> samplingIntervals = mc.getSamplingIntervals();

        assertEquals(1, samplingIntervals.size());

        SamplingInterval i = samplingIntervals.get(0);

        CounterValues readValues = i.getCounterValues(Read.class);
        CounterValues writeValues = i.getCounterValues(Write.class);

        assertEquals(2, readValues.getSuccessCount());
        assertEquals(4L, readValues.getSuccessCumulatedDurationNano());

        assertEquals(1, writeValues.getSuccessCount());
        assertEquals(2L, writeValues.getSuccessCumulatedDurationNano());
    }

    // @Test
    public void recordEmptyInterval() throws Exception
    {
        //
        // TODO convert this test from Collector-based sampler to the new Sampler, preserving semantics
        //

        fail("NEEDS CONVERSION FROM COLLECTOR-BASED SAMPLER TO Sampler");

//        MockHandler mh = new MockHandler();
//        Collector mc = new MockCollector(mh);
//
//        CollectorBasedCsvStatistics s = new CollectorBasedCsvStatistics(mc, 4L);
//
//        Read r = new Read("a");
//
//        s.record(0L, 10L, 11L, r, null);
//
//        assertTrue(mh.getSamplingIntervals().isEmpty());
//
//        Read r2 = new Read("b");
//
//        // this skips into the third sampling interval
//        s.record(9L, 20L, 22L, r2, null);
//
//        List<DeprecatedSamplingInterval> samplingIntervals = mh.getSamplingIntervals();
//
//        assertEquals(2, samplingIntervals.size());
//
//        DeprecatedSamplingInterval i = samplingIntervals.get(0);
//
//        assertEquals(0L, i.getIntervalStartMs());
//        assertEquals(1, i.getValidOperationsCount());
//        assertEquals(1, i.getValidReadsCount());
//        assertEquals(0, i.getValidWritesCount());
//        assertEquals(1L, i.getCumulatedValidReadsTimeNano());
//        assertEquals(0L, i.getCumulatedValidWritesTimeNano());
//
//        i = samplingIntervals.get(1);
//
//        assertEquals(4L, i.getIntervalStartMs());
//        assertEquals(0, i.getValidOperationsCount());
//        assertEquals(0, i.getValidReadsCount());
//        assertEquals(0, i.getValidWritesCount());
//        assertEquals(0L, i.getCumulatedValidReadsTimeNano());
//        assertEquals(0L, i.getCumulatedValidWritesTimeNano());
//
//        samplingIntervals.clear();
//
//        Read r3 = new Read("c");
//
//        // make sure the 9 ms sample is counted
//
//        s.record(13L, 1L, 2L, r3, null);
//
//        samplingIntervals = mh.getSamplingIntervals();
//
//        assertEquals(1, samplingIntervals.size());
//
//        i = samplingIntervals.get(0);
//
//        assertEquals(8L, i.getIntervalStartMs());
//        assertEquals(1, i.getValidOperationsCount());
//        assertEquals(1, i.getValidReadsCount());
//        assertEquals(0, i.getValidWritesCount());
//        assertEquals(2L, i.getCumulatedValidReadsTimeNano());
//        assertEquals(0L, i.getCumulatedValidWritesTimeNano());
    }

    // error counters --------------------------------------------------------------------------------------------------

    // @Test
    public void connectionRefusedIndex_OnePerInterval() throws Exception
    {
        //
        // TODO convert this test from Collector-based sampler to the new Sampler, preserving semantics
        //

        fail("NEEDS CONVERSION FROM COLLECTOR-BASED SAMPLER TO Sampler WHEN WORKING ON ERROR HANDLING");

//        MockHandler mh = new MockHandler();
//        Collector mc = new MockCollector(mh);
//
//        CollectorBasedCsvStatistics s = new CollectorBasedCsvStatistics(mc, 10L);
//
//        @SuppressWarnings("ThrowableInstanceNeverThrown")
//        Throwable t = new java.net.ConnectException("Connection refused");
//
//        Read r = new Read("a");
//
//        s.record(0L, 1L, 2L, r, t);
//
//        Read r2 = new Read("b");
//
//        s.record(11L, -1L, -1L, r2, null);
//
//        List<DeprecatedSamplingInterval> sis = mh.getSamplingIntervals();
//        assertEquals(1, sis.size());
//
//        DeprecatedSamplingInterval si = sis.get(0);
//
//        long[] failureCounters = si.getFailureCounters();
//
//        for(int i = 0; i < failureCounters.length; i ++)
//        {
//            if (i == RedisFailure.CONNECTION_REFUSED_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
//
//        long[] tfc = s.getTotalFailureCounters();
//
//        for(int i = 0; i < tfc.length; i ++)
//        {
//            if (i == RedisFailure.CONNECTION_REFUSED_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
    }

    // @Test
    public void readTimedOut_TwoPerInterval() throws Exception
    {
        //
        // TODO convert this test from Collector-based sampler to the new Sampler, preserving semantics
        //

        fail("NEEDS CONVERSION FROM COLLECTOR-BASED SAMPLER TO Sampler WHEN WORKING ON ERROR HANDLING");

//        MockHandler mh = new MockHandler();
//        Collector mc = new MockCollector(mh);
//
//        CollectorBasedCsvStatistics s = new CollectorBasedCsvStatistics(mc, 10L);
//
//
//        @SuppressWarnings("ThrowableInstanceNeverThrown")
//        Throwable t = new java.net.SocketTimeoutException("Read timed out");
//
//        Read r = new Read("a");
//
//        s.record(0L, 1L, 2L, r, t);
//
//        Read r2 = new Read("b");
//
//        s.record(5L, 10L, 22L, r2, t);
//
//        Read r3 = new Read("c");
//
//        s.record(11L, -1L, -1L, r3, null);
//
//        List<DeprecatedSamplingInterval> sis = mh.getSamplingIntervals();
//        assertEquals(1, sis.size());
//
//        DeprecatedSamplingInterval si = sis.get(0);
//
//        long[] failureCounters = si.getFailureCounters();
//
//        for(int i = 0; i < failureCounters.length; i ++)
//        {
//            if (i == RedisFailure.READ_TIMED_OUT_INDEX)
//            {
//                assertEquals(2, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
//
//        long[] tfc = s.getTotalFailureCounters();
//
//        for(int i = 0; i < tfc.length; i ++)
//        {
//            if (i == RedisFailure.READ_TIMED_OUT_INDEX)
//            {
//                assertEquals(2, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
    }

    // @Test
    public void unknownException() throws Exception
    {
        //
        // TODO convert this test from Collector-based sampler to the new Sampler, preserving semantics
        //

        fail("NEEDS CONVERSION FROM COLLECTOR-BASED SAMPLER TO Sampler WHEN WORKING ON ERROR HANDLING");

//        MockHandler mh = new MockHandler();
//        Collector mc = new MockCollector(mh);
//
//        CollectorBasedCsvStatistics s = new CollectorBasedCsvStatistics(mc, 10L);
//
//        @SuppressWarnings("ThrowableInstanceNeverThrown")
//        Throwable t = new Throwable("TEST");
//
//        Read r = new Read("a");
//
//        s.record(0L, 1L, 2L, r, t);
//
//        Read r2 = new Read("b");
//
//        s.record(11L, -1L, -1L, r2, null);
//
//        List<DeprecatedSamplingInterval> sis = mh.getSamplingIntervals();
//        assertEquals(1, sis.size());
//
//        DeprecatedSamplingInterval si = sis.get(0);
//
//        long[] failureCounters = si.getFailureCounters();
//
//        for(int i = 0; i < failureCounters.length; i ++)
//        {
//            if (i == RedisFailure.OTHERS_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
//
//        long[] tfc = s.getTotalFailureCounters();
//
//        for(int i = 0; i < tfc.length; i ++)
//        {
//            if (i == RedisFailure.OTHERS_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
    }

    // @Test
    public void combinedJedisUnknownReplyAndUnknownException() throws Exception
    {
        //
        // TODO convert this test from Collector-based sampler to the new Sampler, preserving semantics
        //

        fail("NEEDS CONVERSION FROM COLLECTOR-BASED SAMPLER TO Sampler WHEN WORKING ON ERROR HANDLING");

//        MockHandler mh = new MockHandler();
//        Collector mc = new MockCollector(mh);
//
//        CollectorBasedCsvStatistics s = new CollectorBasedCsvStatistics(mc, 10L);
//
//        @SuppressWarnings("ThrowableInstanceNeverThrown")
//        Throwable t = new redis.clients.jedis.exceptions.JedisConnectionException("Unknown reply: something");
//
//        Write w = new Write("TEST-KEY", "TEST-VALUE");
//
//        s.record(0L, 1L, 2L, w, t);
//
//        @SuppressWarnings("ThrowableInstanceNeverThrown")
//        Throwable t2 = new RuntimeException("SYNTHETIC");
//
//        Write w2 = new Write("TEST-KEY2", "TEST-VALUE2");
//
//        s.record(5L, 3L, 4L, w2, t2);
//
//        Write w3 = new Write("TEST-KEY3", "TEST-VALUE3");
//
//        s.record(11L, -1L, -1L, w3, null);
//
//        List<DeprecatedSamplingInterval> sis = mh.getSamplingIntervals();
//        assertEquals(1, sis.size());
//
//        DeprecatedSamplingInterval si = sis.get(0);
//
//        long[] failureCounters = si.getFailureCounters();
//
//        for(int i = 0; i < failureCounters.length; i ++)
//        {
//            if (i == RedisFailure.JEDIS_UNKNOWN_REPLY_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else if (i == RedisFailure.OTHERS_INDEX)
//            {
//                assertEquals(1, failureCounters[i]);
//            }
//            else
//            {
//                assertEquals(0, failureCounters[i]);
//            }
//        }
//
//        long[] tfc = s.getTotalFailureCounters();
//
//        for(int i = 0; i < tfc.length; i ++)
//        {
//            if (i == RedisFailure.JEDIS_UNKNOWN_REPLY_INDEX)
//            {
//                assertEquals(1, tfc[i]);
//            }
//            else if (i == RedisFailure.OTHERS_INDEX)
//            {
//                assertEquals(1, tfc[i]);
//            }
//            else
//            {
//                assertEquals(0, tfc[i]);
//            }
//        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
