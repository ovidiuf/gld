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
import io.novaordis.gld.driver.MockOperation;
import io.novaordis.gld.driver.sampler.metrics.Metric;
import io.novaordis.gld.driver.sampler.metrics.SystemLoadAverage;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SamplerImplWorkBenchTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerImplWorkBenchTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void syntheticLife_NoSteppingOutsideTheBoundariesOfASamplingInterval() throws Exception
    {
        long samplingIntervalMs = 2 * 1000L; // 2 second to allow us time to experiment

        // the sampling task run interval is 0, meaning no timer task will be registered
        SamplerImpl s = new SamplerImpl(0L, samplingIntervalMs);
        s.registerOperation(MockOperation.class);
        MockSamplingConsumer msc = new MockSamplingConsumer();
        s.registerConsumer(msc);

        assertTrue(s.getLastRunTimestamp() <= 0);

        // run 1

        // we're not actually starting anything because the sampling task interval is 0, but the sampler will look
        // like it started; this will also run the first initialization run()
        s.start();

        assertTrue(s.isStarted());

        assertTrue(s.getLastRunTimestamp() > 0);

        SamplingInterval current = s.getCurrent();
        assertNotNull(current);

        // run 2 - collection run, should not collect anything
        s.run();

        long ts = current.getStartMs();
        // make sure it's rounded on the second
        assertEquals(0L, ts - ((ts / 1000) * 1000L));

        // run 3 - collection run, should not collect anything
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingIntervalMs, current.getDurationMs());
        Set<Class<? extends Operation>> operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        assertTrue(current.getAnnotations().isEmpty());
        CounterValues cvs = current.getCounterValues(MockOperation.class);
        assertEquals(0L, cvs.getSuccessCount());
        assertEquals(0L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(0L, cvs.getFailureCount());
        assertEquals(0L, cvs.getFailureCumulatedDurationNano());

        // record a success and a failure
        s.record(System.currentTimeMillis(), 1L, 2L, new MockOperation()); // 1
        s.record(System.currentTimeMillis(), 3L, 5L, new MockOperation(), new SocketException()); // 2

        // run 4 - another collection run, it should collect both the success and the failure
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingIntervalMs, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        assertTrue(current.getAnnotations().isEmpty());
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(1L, cvs.getSuccessCount());
        assertEquals(1L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(1L, cvs.getFailureCount());
        assertEquals(2L, cvs.getFailureCumulatedDurationNano());
        Set<Class<? extends Throwable>> failureTypes = cvs.getFailureTypes();
        assertEquals(1, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(1L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L, cvs.getFailureCumulatedDurationNano(SocketException.class));

        // record a success, the same type a failure, and an annotation
        s.record(System.currentTimeMillis(), 6L, 9L, new MockOperation()); // 3
        s.record(System.currentTimeMillis(), 10L, 14L, new MockOperation(), new SocketException()); // 4
        s.annotate("annotation 1");

        // run 5 - another collection run
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingIntervalMs, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        List<String> annotations = current.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains("annotation 1"));
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(2L, cvs.getSuccessCount());
        assertEquals(1L + 3L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(2L, cvs.getFailureCount());
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano());
        failureTypes = cvs.getFailureTypes();
        assertEquals(1, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(2L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano(SocketException.class));

        // record a success, a different type a failure, and another annotation
        s.record(System.currentTimeMillis(), 15L, 20L, new MockOperation()); // 5
        s.record(System.currentTimeMillis(), 21L, 27L, new MockOperation(), new ConnectException()); // 6
        s.annotate("annotation 2");

        // run 6 - another collection run
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingIntervalMs, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        annotations = current.getAnnotations();
        assertEquals(2, annotations.size());
        assertEquals("annotation 1", annotations.get(0));
        assertEquals("annotation 2", annotations.get(1));
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(3L, cvs.getSuccessCount());
        assertEquals(1L + 3L + 5L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(3L, cvs.getFailureCount());
        assertEquals(2L + 4L + 6L, cvs.getFailureCumulatedDurationNano());
        failureTypes = cvs.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(2L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, cvs.getFailureCount(ConnectException.class));
        assertEquals(6L, cvs.getFailureCumulatedDurationNano(ConnectException.class));

        // snapshot the statistics to make sure the sampling consumer gets the same thing
        long startMs = current.getStartMs();
        long durationMs = current.getDurationMs();
        long endMs = current.getEndMs();
        Set<Class<? extends Operation>> operationTypesSnapshot = new HashSet<>(current.getOperationTypes());
        assertEquals(1, operationTypesSnapshot.size());
        assertTrue(operationTypesSnapshot.contains(MockOperation.class));
        CounterValues counterValuesSnapshot = current.getCounterValues(MockOperation.class);
        long successCount = counterValuesSnapshot.getSuccessCount();
        long successCumulatedDurationNano = counterValuesSnapshot.getSuccessCumulatedDurationNano();
        long failureCount = counterValuesSnapshot.getFailureCount();
        long failureCumulatedDurationNano = counterValuesSnapshot.getFailureCumulatedDurationNano();
        Set<Class<? extends Throwable>> failureTypesSnapshot = new HashSet<>(counterValuesSnapshot.getFailureTypes());
        assertEquals(2, failureTypesSnapshot.size());
        assertTrue(failureTypesSnapshot.contains(SocketException.class));
        assertTrue(failureTypesSnapshot.contains(ConnectException.class));
        long socketExceptionFailureCount = counterValuesSnapshot.getFailureCount(SocketException.class);
        long socketExceptionCumulatedDurationNano =
            counterValuesSnapshot.getFailureCumulatedDurationNano(SocketException.class);
        long connectExceptionFailureCount = counterValuesSnapshot.getFailureCount(ConnectException.class);
        long connectExceptionCumulatedDurationNano =
            counterValuesSnapshot.getFailureCumulatedDurationNano(ConnectException.class);

        assertEquals(samplingIntervalMs, durationMs);
        assertEquals(samplingIntervalMs, endMs - startMs);
        assertEquals(3L, successCount);
        assertEquals(1L + 3L + 5L, successCumulatedDurationNano);
        assertEquals(3L, failureCount);
        assertEquals(2L + 4L + 6L, failureCumulatedDurationNano);
        assertEquals(2L, socketExceptionFailureCount);
        assertEquals(2L + 4L, socketExceptionCumulatedDurationNano);
        assertEquals(1L, connectExceptionFailureCount);
        assertEquals(6L, connectExceptionCumulatedDurationNano);

        long elapsedFromTheBeginningOfSamplingInterval = System.currentTimeMillis() - current.getStartMs();
        log.info("elapsed since sampling interval started " + elapsedFromTheBeginningOfSamplingInterval + " ms");

        // wait exactly until the end of the sampling interval and do another run

        long sleep = current.getDurationMs() - elapsedFromTheBeginningOfSamplingInterval;
        log.debug("sleeping for " + sleep + " ms");
        Thread.sleep(sleep);

        // run 7 - we should see a new sampling interval being installed

        s.run();

        SamplingInterval next = s.getCurrent();

        assertNotEquals(current, next);

        //
        // verify that the sampling consumer got the correct values
        //

        List<SamplingInterval> collectedSamplingIntervals = msc.getSamplingIntervals();

        assertEquals(1, collectedSamplingIntervals.size());
        SamplingInterval si = collectedSamplingIntervals.get(0);

        assertEquals(startMs, si.getStartMs());
        assertEquals(durationMs, si.getDurationMs());
        assertEquals(endMs, si.getEndMs());
        Set<Class<? extends Operation>> collectedOperationTypes = si.getOperationTypes();
        assertEquals(1, collectedOperationTypes.size());
        assertTrue(collectedOperationTypes.contains(MockOperation.class));
        CounterValues collectedCounterValues = si.getCounterValues(MockOperation.class);
        assertEquals(successCount, collectedCounterValues.getSuccessCount());
        assertEquals(successCumulatedDurationNano, collectedCounterValues.getSuccessCumulatedDurationNano());
        assertEquals(failureCount, collectedCounterValues.getFailureCount());
        assertEquals(failureCumulatedDurationNano, collectedCounterValues.getFailureCumulatedDurationNano());
        Set<Class<? extends Throwable>> collectedFailureTypes = collectedCounterValues.getFailureTypes();
        assertEquals(2, collectedFailureTypes.size());
        assertTrue(collectedFailureTypes.contains(SocketException.class));
        assertTrue(collectedFailureTypes.contains(ConnectException.class));
        assertEquals(socketExceptionFailureCount, collectedCounterValues.getFailureCount(SocketException.class));
        assertEquals(socketExceptionCumulatedDurationNano,
            collectedCounterValues.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(connectExceptionFailureCount, collectedCounterValues.getFailureCount(ConnectException.class));
        assertEquals(connectExceptionCumulatedDurationNano,
            collectedCounterValues.getFailureCumulatedDurationNano(ConnectException.class));


        // make an extra record before stopping to make sure it makes it to the consumer
        // record a success, a failure and an annotation
        s.record(System.currentTimeMillis(), 28L, 35L, new MockOperation()); // 7
        s.record(System.currentTimeMillis(), 36L, 44L, new MockOperation(), new ConnectException()); // 8
        s.annotate("annotation 3");

        s.stop();


        // make sure the statistics generated by the last record invocations make it to the consumer

        assertEquals(2, collectedSamplingIntervals.size());
        assertEquals(si, collectedSamplingIntervals.get(0));

        SamplingInterval si2 = collectedSamplingIntervals.get(1);

        assertEquals(si.getEndMs(), si2.getStartMs());
        assertEquals(samplingIntervalMs, si2.getEndMs() - si2.getStartMs());
        assertEquals(1, si2.getOperationTypes().size());
        assertTrue(si2.getOperationTypes().contains(MockOperation.class));
        CounterValues counterValues = si2.getCounterValues(MockOperation.class);
        assertEquals(1L, counterValues.getSuccessCount());
        assertEquals(7L, counterValues.getSuccessCumulatedDurationNano());
        assertEquals(1L, counterValues.getFailureCount());
        assertEquals(8L, counterValues.getFailureCumulatedDurationNano());
        assertEquals(2, counterValues.getFailureTypes().size());
        assertTrue(counterValues.getFailureTypes().contains(SocketException.class));
        assertTrue(counterValues.getFailureTypes().contains(ConnectException.class));
        assertEquals(0L, counterValues.getFailureCount(SocketException.class));
        assertEquals(0L, counterValues.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, counterValues.getFailureCount(ConnectException.class));
        assertEquals(8L, counterValues.getFailureCumulatedDurationNano(ConnectException.class));
        annotations = si2.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains("annotation 3"));

        assertTrue(!s.isStarted());
    }

    @Test
    public void syntheticLife_MultipleSamplesAccumulate() throws Exception
    {
        long samplingIntervalMs = 100L; // small, so we can overstep the boundaries easily

        // the sampling task run interval is 0, meaning no timer task will be registered
        SamplerImpl s = new SamplerImpl(0L, samplingIntervalMs);
        s.registerOperation(MockOperation.class);
        MockSamplingConsumer msc = new MockSamplingConsumer();
        s.registerConsumer(msc);

        // run 1 - internal to start(). We're not actually starting anything because the sampling task interval is 0,
        // but the sampler will look/ like it started; this will also run the first initialization run()
        s.start();

        assertTrue(s.isStarted());
        SamplingInterval current = s.getCurrent();
        assertNotNull(current);

        // run 2 - should not collect anything
        s.run();

        // record a success, a failure an an annotation and wait 5 sampling intervals
        s.record(System.currentTimeMillis(), 1L, 2L, new MockOperation()); // 1
        s.record(System.currentTimeMillis(), 3L, 5L, new MockOperation(), new SocketException()); // 2
        s.annotate("annotation 1");

        int numberOfSamplingIntervals = 5;
        long sleep = numberOfSamplingIntervals * samplingIntervalMs;
        log.info("sleeping " + sleep + " ms ...");
        Thread.sleep(sleep);

        // run 3 - it should send at least numberOfSamplingIntervals samples
        s.run();

        List<SamplingInterval> collectedSamplingIntervals = msc.getSamplingIntervals();

        int n = collectedSamplingIntervals.size();

        assertTrue(n >= numberOfSamplingIntervals);

        long lastStartTs = -1L;

        long sc = 0L;
        long sd = 0L;
        long fc = 0L;
        long fd = 0L;
        long fcse = 0L;
        long fdse = 0L;

        for(int i = 0; i < n; i ++)
        {
            SamplingInterval si = collectedSamplingIntervals.get(i);

            assertEquals(samplingIntervalMs, si.getDurationMs());
            assertEquals(si.getStartMs() + samplingIntervalMs, si.getEndMs());

            if (lastStartTs != -1L)
            {
                assertEquals(lastStartTs, si.getStartMs() - samplingIntervalMs);
            }

            lastStartTs = si.getStartMs();

            List<String> annotations = si.getAnnotations();

            if (i == 0)
            {
                assertEquals(1, annotations.size());
                assertEquals("annotation 1", annotations.get(0));
            }
            else
            {
                assertTrue(annotations.isEmpty());
            }

            assertEquals(1, si.getOperationTypes().size());
            assertTrue(si.getOperationTypes().contains(MockOperation.class));

            CounterValues cv = si.getCounterValues(MockOperation.class);

            assertEquals(1, cv.getFailureTypes().size());
            assertTrue(cv.getFailureTypes().contains(SocketException.class));

            sc += cv.getSuccessCount();
            sd += cv.getSuccessCumulatedDurationNano();
            fc += cv.getFailureCount();
            fd += cv.getFailureCumulatedDurationNano();
            fcse += cv.getFailureCount(SocketException.class);
            fdse += cv.getFailureCumulatedDurationNano(SocketException.class);
            assertEquals(0, cv.getFailureCount(IOException.class));
            assertEquals(0, cv.getFailureCumulatedDurationNano(IOException.class));
        }

        assertEquals(1L, sc);
        assertEquals(1L, sd);
        assertEquals(1L, fc);
        assertEquals(2L, fd);
        assertEquals(1L, fcse);
        assertEquals(2L, fdse);
    }

    @Test
    public void syntheticLife_StartRecordStop() throws Exception
    {
        long samplingIntervalMs = 1000L;

        // the sampling task run interval is 0, meaning no timer task will be registered
        SamplerImpl s = new SamplerImpl(0L, samplingIntervalMs);
        s.registerOperation(MockOperation.class);
        s.registerMetric(SystemLoadAverage.class);
        MockSamplingConsumer msc = new MockSamplingConsumer();
        s.registerConsumer(msc);

        s.start();

        s.record(System.currentTimeMillis(), 1L, 2L, new MockOperation()); // 1
        log.info("recorded successful mock operation");
        s.record(System.currentTimeMillis(), 3L, 5L, new MockOperation(), new SocketException()); // 2
        log.info("recorded failed mock operation");
        s.annotate("annotation 1");

        s.stop();

        List<SamplingInterval> collectedSamplingIntervals = msc.getSamplingIntervals();

        // normally we should generate one sample, but we could generate more if we debug and put the execution on hold
        assertFalse(collectedSamplingIntervals.isEmpty());

        long previousSampleTs = 0L;
        long sc = 0L;
        long sdc = 0L;
        long fc = 0L;
        long fdc = 0L;
        long sefc = 0L;
        long sefdc = 0L;
        Set<String> annotations = new HashSet<>();

        // sweep the sampling interval and make sure all our events are there
        for(SamplingInterval si: collectedSamplingIntervals)
        {
            if (previousSampleTs == 0L)
            {
                previousSampleTs = si.getStartMs();
            }
            else
            {
                assertEquals(si.getStartMs(), previousSampleTs + samplingIntervalMs);
                previousSampleTs = si.getStartMs();
            }

            assertEquals(samplingIntervalMs, si.getDurationMs());
            assertEquals(1, si.getOperationTypes().size());
            assertTrue(si.getOperationTypes().contains(MockOperation.class));

            CounterValues cv = si.getCounterValues(MockOperation.class);
            sc += cv.getSuccessCount();
            sdc += cv.getSuccessCumulatedDurationNano();
            fc += cv.getFailureCount();
            fdc += cv.getFailureCumulatedDurationNano();

            assertEquals(1, cv.getFailureTypes().size());
            assertTrue(cv.getFailureTypes().contains(SocketException.class));

            sefc += cv.getFailureCount(SocketException.class);
            sefdc += cv.getFailureCumulatedDurationNano(SocketException.class);

            annotations.addAll(si.getAnnotations());

            Set<Metric> metrics = si.getMetrics();
            assertEquals(1, metrics.size());
            Metric m = metrics.iterator().next();
            assertTrue(m instanceof SystemLoadAverage);
            assertNotNull(m.getValue());
        }

        assertEquals(1, sc);
        assertEquals(1L, sdc);
        assertEquals(1, fc);
        assertEquals(2L, fdc);
        assertEquals(1, sefc);
        assertEquals(2L, sefdc);
        assertTrue(annotations.contains("annotation 1"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
