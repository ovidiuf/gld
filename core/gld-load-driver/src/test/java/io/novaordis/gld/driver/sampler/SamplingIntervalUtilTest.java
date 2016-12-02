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

import com.novaordis.gld.Operation;
import io.novaordis.gld.driver.sampler.metrics.FreePhysicalMemorySize;
import io.novaordis.gld.driver.sampler.metrics.Metric;
import io.novaordis.gld.driver.sampler.metrics.SystemCpuLoad;
import io.novaordis.gld.driver.sampler.metrics.SystemLoadAverage;
import io.novaordis.gld.driver.sampler.metrics.TotalPhysicalMemorySize;
import com.novaordis.gld.strategy.load.cache.AnotherTypeOfMockOperation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SamplingIntervalUtilTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplingIntervalUtilTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // extrapolate() ----------------------------------------------------------------------------------------------------

    @Test
    public void extrapolate_null() throws Exception
    {
        try
        {
            SamplingIntervalUtil.extrapolate(null, 0);
            fail("should fail because we're passing a null sampling interval");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void extrapolate_extraSamples_is_0() throws Exception
    {
        long durationMs = 5L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(1L, durationMs, operationTypes);
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));
        CounterValuesImpl cv = new CounterValuesImpl(5L, 6L, failureCounters);
        si.setCounterValues(MockOperation.class, cv);
        si.addAnnotation("annotation 1");
        si.addAnnotation("annotation 2");
        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(7));
        metrics.add(new TotalPhysicalMemorySize(11));
        metrics.add(new SystemLoadAverage(13.0));
        metrics.add(new SystemCpuLoad(0.17));
        si.setMetrics(metrics);

        SamplingInterval[] result = SamplingIntervalUtil.extrapolate(si, 0);

        assertEquals(1, result.length);

        SamplingInterval si2 = result[0];

        assertEquals(si, si2);

        // make sure nothing changed

        assertEquals(1L, si2.getStartMs());
        assertEquals(durationMs, si2.getDurationMs());
        assertEquals(1, si2.getOperationTypes().size());
        assertTrue(si2.getOperationTypes().contains(MockOperation.class));
        CounterValues cv2 = si2.getCounterValues(MockOperation.class);
        assertEquals(5L, cv2.getSuccessCount());
        assertEquals(6L, cv2.getSuccessCumulatedDurationNano());
        assertEquals(2, cv2.getFailureTypes().size());
        assertTrue(cv2.getFailureTypes().contains(SocketException.class));
        assertTrue(cv2.getFailureTypes().contains(ConnectException.class));
        assertEquals(1L, cv2.getFailureCount(SocketException.class));
        assertEquals(2L, cv2.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(3L, cv2.getFailureCount(ConnectException.class));
        assertEquals(4L, cv2.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(2, si2.getAnnotations().size());
        assertEquals("annotation 1", si2.getAnnotations().get(0));
        assertEquals("annotation 2", si2.getAnnotations().get(1));

        metrics = si2.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }
    }

    @Test
    public void extrapolate_extraSamples_is_1() throws Exception
    {
        long durationMs = 5L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(1L, durationMs, operationTypes);
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));
        CounterValuesImpl cv = new CounterValuesImpl(5L, 6L, failureCounters);
        si.setCounterValues(MockOperation.class, cv);
        si.addAnnotation("annotation 1");
        si.addAnnotation("annotation 2");
        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(7));
        metrics.add(new TotalPhysicalMemorySize(11));
        metrics.add(new SystemLoadAverage(13.0));
        metrics.add(new SystemCpuLoad(0.17));
        si.setMetrics(metrics);

        SamplingInterval[] result = SamplingIntervalUtil.extrapolate(si, 1);

        assertEquals(2, result.length);

        SamplingInterval si2 = result[0];
        SamplingInterval si3 = result[1];

        // first sampling interval

        assertEquals(1L, si2.getStartMs());
        assertEquals(durationMs, si2.getDurationMs());
        assertEquals(6L, si2.getEndMs());
        assertEquals(1, si2.getOperationTypes().size());
        assertTrue(si2.getOperationTypes().contains(MockOperation.class));
        CounterValues cv2 = si2.getCounterValues(MockOperation.class);

        assertEquals(2L, cv2.getSuccessCount());
        assertEquals(3L, cv2.getSuccessCumulatedDurationNano());

        assertEquals(2, cv2.getFailureTypes().size());
        assertTrue(cv2.getFailureTypes().contains(SocketException.class));
        assertTrue(cv2.getFailureTypes().contains(ConnectException.class));

        // first sampling interval - failure counters

        assertEquals(0L, cv2.getFailureCount(SocketException.class));
        assertEquals(0L, cv2.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(1L, cv2.getFailureCount(ConnectException.class));
        assertEquals(2L, cv2.getFailureCumulatedDurationNano(ConnectException.class));

        // all annotations are stored in the first interval
        assertEquals(2, si2.getAnnotations().size());
        assertEquals("annotation 1", si2.getAnnotations().get(0));
        assertEquals("annotation 2", si2.getAnnotations().get(1));

        // metrics should propagate the same values
        metrics = si2.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }

        // the second sampling interval

        assertEquals(6L, si3.getStartMs());
        assertEquals(durationMs, si3.getDurationMs());
        assertEquals(11L, si3.getEndMs());
        assertEquals(1, si3.getOperationTypes().size());
        assertTrue(si3.getOperationTypes().contains(MockOperation.class));
        CounterValues cv3 = si3.getCounterValues(MockOperation.class);

        assertEquals(3L, cv3.getSuccessCount());
        assertEquals(3L, cv3.getSuccessCumulatedDurationNano());

        assertEquals(2, cv3.getFailureTypes().size());
        assertTrue(cv3.getFailureTypes().contains(SocketException.class));
        assertTrue(cv3.getFailureTypes().contains(ConnectException.class));

        // second sampling interval - failure counters

        assertEquals(1L, cv3.getFailureCount(SocketException.class));
        assertEquals(2L, cv3.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(2L, cv3.getFailureCount(ConnectException.class));
        assertEquals(2L, cv3.getFailureCumulatedDurationNano(ConnectException.class));

        // all annotations are stored in the first interval
        assertTrue(si3.getAnnotations().isEmpty());

        // metrics should propagate the same values
        metrics = si3.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }
    }

    @Test
    public void extrapolate_extraSamples_is_1_twoOperations() throws Exception
    {
        long durationMs = 200L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        operationTypes.add(AnotherTypeOfMockOperation.class);

        SamplingIntervalImpl si = new SamplingIntervalImpl(100L, durationMs, operationTypes);

        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));
        CounterValuesImpl cv = new CounterValuesImpl(5L, 6L, failureCounters);
        si.setCounterValues(MockOperation.class, cv);

        failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(7L, 8L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(9L, 10L));
        cv = new CounterValuesImpl(11L, 12L, failureCounters);
        si.setCounterValues(AnotherTypeOfMockOperation.class, cv);

        si.addAnnotation("annotation 1");
        si.addAnnotation("annotation 2");

        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(7));
        metrics.add(new TotalPhysicalMemorySize(11));
        metrics.add(new SystemLoadAverage(13.0));
        metrics.add(new SystemCpuLoad(0.17));
        si.setMetrics(metrics);

        SamplingInterval[] result = SamplingIntervalUtil.extrapolate(si, 1);

        assertEquals(2, result.length);

        SamplingInterval si2 = result[0];
        SamplingInterval si3 = result[1];

        // first sampling interval

        assertEquals(100L, si2.getStartMs());
        assertEquals(durationMs, si2.getDurationMs());
        assertEquals(300L, si2.getEndMs());

        assertEquals(2, si2.getOperationTypes().size());
        assertTrue(si2.getOperationTypes().contains(MockOperation.class));
        assertTrue(si2.getOperationTypes().contains(AnotherTypeOfMockOperation.class));

        //
        // 2.1
        //

        CounterValues cv21 = si2.getCounterValues(MockOperation.class);

        assertEquals(2L, cv21.getSuccessCount());
        assertEquals(3L, cv21.getSuccessCumulatedDurationNano());

        assertEquals(2, cv21.getFailureTypes().size());
        assertTrue(cv21.getFailureTypes().contains(SocketException.class));
        assertTrue(cv21.getFailureTypes().contains(ConnectException.class));

        // first sampling interval - failure counters

        assertEquals(0L, cv21.getFailureCount(SocketException.class));
        assertEquals(0L, cv21.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(1L, cv21.getFailureCount(ConnectException.class));
        assertEquals(2L, cv21.getFailureCumulatedDurationNano(ConnectException.class));

        //
        // 2.2
        //

        CounterValues cv22 = si2.getCounterValues(AnotherTypeOfMockOperation.class);

        assertEquals(5L, cv22.getSuccessCount());
        assertEquals(6L, cv22.getSuccessCumulatedDurationNano());

        assertEquals(2, cv22.getFailureTypes().size());
        assertTrue(cv22.getFailureTypes().contains(SocketException.class));
        assertTrue(cv22.getFailureTypes().contains(ConnectException.class));

        // first sampling interval - failure counters

        assertEquals(3L, cv22.getFailureCount(SocketException.class));
        assertEquals(4L, cv22.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(4L, cv22.getFailureCount(ConnectException.class));
        assertEquals(5L, cv22.getFailureCumulatedDurationNano(ConnectException.class));

        // all annotations are stored in the first interval
        assertEquals(2, si2.getAnnotations().size());
        assertEquals("annotation 1", si2.getAnnotations().get(0));
        assertEquals("annotation 2", si2.getAnnotations().get(1));

        // metrics should propagate the same values
        metrics = si2.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }

        // the second sampling interval

        assertEquals(300L, si3.getStartMs());
        assertEquals(durationMs, si3.getDurationMs());
        assertEquals(500L, si3.getEndMs());
        assertEquals(2, si3.getOperationTypes().size());
        assertTrue(si3.getOperationTypes().contains(MockOperation.class));
        assertTrue(si3.getOperationTypes().contains(AnotherTypeOfMockOperation.class));

        //
        // 3.1
        //

        CounterValues cv31 = si3.getCounterValues(MockOperation.class);

        assertEquals(3L, cv31.getSuccessCount());
        assertEquals(3L, cv31.getSuccessCumulatedDurationNano());

        assertEquals(2, cv31.getFailureTypes().size());
        assertTrue(cv31.getFailureTypes().contains(SocketException.class));
        assertTrue(cv31.getFailureTypes().contains(ConnectException.class));

        // second sampling interval - failure counters

        assertEquals(1L, cv31.getFailureCount(SocketException.class));
        assertEquals(2L, cv31.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(2L, cv31.getFailureCount(ConnectException.class));
        assertEquals(2L, cv31.getFailureCumulatedDurationNano(ConnectException.class));

        //
        // 3.2
        //

        CounterValues cv32 = si3.getCounterValues(AnotherTypeOfMockOperation.class);

        assertEquals(6L, cv32.getSuccessCount());
        assertEquals(6L, cv32.getSuccessCumulatedDurationNano());

        assertEquals(2, cv32.getFailureTypes().size());
        assertTrue(cv32.getFailureTypes().contains(SocketException.class));
        assertTrue(cv32.getFailureTypes().contains(ConnectException.class));

        // second sampling interval - failure counters

        assertEquals(4L, cv32.getFailureCount(SocketException.class));
        assertEquals(4L, cv32.getFailureCumulatedDurationNano(SocketException.class));

        assertEquals(5L, cv32.getFailureCount(ConnectException.class));
        assertEquals(5L, cv32.getFailureCumulatedDurationNano(ConnectException.class));

        // all annotations are stored in the first interval
        assertTrue(si3.getAnnotations().isEmpty());

        // metrics should propagate the same values
        metrics = si3.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }
    }

    @Test
    public void extrapolate_extraSamples_is_2() throws Exception
    {
        long durationMs = 785L;
        Set<Class<? extends Operation>> operationTypes = new HashSet<>();
        operationTypes.add(MockOperation.class);
        SamplingIntervalImpl si = new SamplingIntervalImpl(1000L, durationMs, operationTypes);
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounters = new HashMap<>();
        failureCounters.put(SocketException.class, new ImmutableFailureCounter(1L, 2L));
        failureCounters.put(ConnectException.class, new ImmutableFailureCounter(3L, 4L));
        failureCounters.put(IOException.class, new ImmutableFailureCounter(50000L, 60000L));
        CounterValuesImpl cv = new CounterValuesImpl(5L, 6L, failureCounters);
        si.setCounterValues(MockOperation.class, cv);
        si.addAnnotation("annotation 1");
        si.addAnnotation("annotation 2");
        Set<Metric> metrics = new HashSet<>();
        metrics.add(new FreePhysicalMemorySize(7));
        metrics.add(new TotalPhysicalMemorySize(11));
        metrics.add(new SystemLoadAverage(13.0));
        metrics.add(new SystemCpuLoad(0.17));
        si.setMetrics(metrics);

        SamplingInterval[] result = SamplingIntervalUtil.extrapolate(si, 2);

        assertEquals(3, result.length);

        SamplingInterval si2 = result[0];
        SamplingInterval si3 = result[1];
        SamplingInterval si4 = result[2];
        
        // sample 0

        assertEquals(1000L, si2.getStartMs());
        assertEquals(durationMs, si2.getDurationMs());
        assertEquals(1000L + durationMs, si2.getEndMs());

        assertEquals(2, si2.getAnnotations().size());
        assertEquals("annotation 1", si2.getAnnotations().get(0));
        assertEquals("annotation 2", si2.getAnnotations().get(1));

        assertEquals(1, si2.getOperationTypes().size());
        assertTrue(si2.getOperationTypes().contains(MockOperation.class));
        CounterValues cv2 = si2.getCounterValues(MockOperation.class);
        
        assertEquals(1L, cv2.getSuccessCount());
        assertEquals(2L, cv2.getSuccessCumulatedDurationNano());
        assertEquals(16667L, cv2.getFailureCount());
        assertEquals(20001L, cv2.getFailureCumulatedDurationNano());
        assertEquals(3, cv2.getFailureTypes().size());
        assertTrue(cv2.getFailureTypes().contains(SocketException.class));
        assertTrue(cv2.getFailureTypes().contains(ConnectException.class));
        assertTrue(cv2.getFailureTypes().contains(IOException.class));

        assertEquals(0L, cv2.getFailureCount(SocketException.class));
        assertEquals(0L, cv2.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, cv2.getFailureCount(ConnectException.class));
        assertEquals(1L, cv2.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(16666L, cv2.getFailureCount(IOException.class));
        assertEquals(20000L, cv2.getFailureCumulatedDurationNano(IOException.class));

        // metrics should propagate the same values
        metrics = si2.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }

        // sample 1

        assertEquals(1000L + durationMs, si3.getStartMs());
        assertEquals(durationMs, si3.getDurationMs());
        assertEquals(1000L + durationMs + durationMs, si3.getEndMs());

        assertEquals(0, si3.getAnnotations().size());

        assertEquals(1, si3.getOperationTypes().size());
        assertTrue(si3.getOperationTypes().contains(MockOperation.class));
        CounterValues cv3 = si3.getCounterValues(MockOperation.class);

        assertEquals(1L, cv3.getSuccessCount());
        assertEquals(2L, cv3.getSuccessCumulatedDurationNano());
        assertEquals(16667L, cv3.getFailureCount());
        assertEquals(20001L, cv3.getFailureCumulatedDurationNano());
        assertEquals(3, cv3.getFailureTypes().size());
        assertTrue(cv3.getFailureTypes().contains(SocketException.class));
        assertTrue(cv3.getFailureTypes().contains(ConnectException.class));
        assertTrue(cv3.getFailureTypes().contains(IOException.class));

        assertEquals(0L, cv3.getFailureCount(SocketException.class));
        assertEquals(0L, cv3.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, cv3.getFailureCount(ConnectException.class));
        assertEquals(1L, cv3.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(16666L, cv3.getFailureCount(IOException.class));
        assertEquals(20000L, cv3.getFailureCumulatedDurationNano(IOException.class));

        // metrics should propagate the same values
        metrics = si3.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }

        // sample 2

        assertEquals(1000L + durationMs + durationMs, si4.getStartMs());
        assertEquals(durationMs, si4.getDurationMs());
        assertEquals(1000L + durationMs + durationMs + durationMs, si4.getEndMs());

        assertEquals(0, si4.getAnnotations().size());

        assertEquals(1, si4.getOperationTypes().size());
        assertTrue(si4.getOperationTypes().contains(MockOperation.class));
        CounterValues cv4 = si4.getCounterValues(MockOperation.class);

        assertEquals(3L, cv4.getSuccessCount());
        assertEquals(2L, cv4.getSuccessCumulatedDurationNano());
        assertEquals(16670L, cv4.getFailureCount());
        assertEquals(20004L, cv4.getFailureCumulatedDurationNano());
        assertEquals(3, cv4.getFailureTypes().size());
        assertTrue(cv4.getFailureTypes().contains(SocketException.class));
        assertTrue(cv4.getFailureTypes().contains(ConnectException.class));
        assertTrue(cv4.getFailureTypes().contains(IOException.class));

        assertEquals(1L, cv4.getFailureCount(SocketException.class));
        assertEquals(2L, cv4.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, cv4.getFailureCount(ConnectException.class));
        assertEquals(2L, cv4.getFailureCumulatedDurationNano(ConnectException.class));
        assertEquals(16668L, cv4.getFailureCount(IOException.class));
        assertEquals(20000L, cv4.getFailureCumulatedDurationNano(IOException.class));


        assertEquals(1L + 3L + 50000L, cv2.getFailureCount() + cv3.getFailureCount() + cv4.getFailureCount());
        assertEquals(2L + 4L + 60000L,
            cv2.getFailureCumulatedDurationNano() +
                cv3.getFailureCumulatedDurationNano() +
                cv4.getFailureCumulatedDurationNano());

        // metrics should propagate the same values
        metrics = si4.getMetrics();
        assertEquals(4, metrics.size());
        for(Metric m: metrics)
        {
            if (m instanceof FreePhysicalMemorySize)
            {
                assertEquals(7L, m.getValue());
            }
            else if (m instanceof TotalPhysicalMemorySize)
            {
                assertEquals(11L, m.getValue());
            }
            else if (m instanceof SystemLoadAverage)
            {
                assertEquals(13.0, m.getValue());
            }
            else if (m instanceof SystemCpuLoad)
            {
                assertEquals(0.17, m.getValue());
            }
            else
            {
                fail("metric " + m + " should not be here");
            }
        }
    }

    // snapshotMetrics() -----------------------------------------------------------------------------------------------

    @Test
    public void snapshotMetrics() throws Exception
    {
        Set<Class<? extends Metric>> metricTypes = new HashSet<>();
        metricTypes.add(SystemLoadAverage.class);
        metricTypes.add(SystemCpuLoad.class);
        metricTypes.add(FreePhysicalMemorySize.class);
        metricTypes.add(TotalPhysicalMemorySize.class);

        Set<Metric> metrics = SamplingIntervalUtil.snapshotMetrics(metricTypes);

        assertEquals(4, metrics.size());

        for(Metric m: metrics)
        {
            assertNotNull(m.getValue());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
