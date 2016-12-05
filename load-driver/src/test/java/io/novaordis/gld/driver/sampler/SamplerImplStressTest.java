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

import io.novaordis.gld.driver.MockOperation;
import io.novaordis.gld.driver.sampler.metrics.MeasureUnit;
import io.novaordis.gld.driver.statistics.Statistics;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class SamplerImplStressTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerImplStressTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void stress() throws Exception {
//
//        long interval = 1000L;
//
//        final SamplerImpl s = new SamplerImpl(250L, interval);
//        s.registerOperation(MockOperation.class);
//
//        MockSamplingConsumer msc = new MockSamplingConsumer();
//        s.registerConsumer(msc);
//
//        s.start();
//
//        int threads = 300;
//        final int operationsPerThread = 1200000;
//        final CyclicBarrier barrier = new CyclicBarrier(threads + 1);
//        final AtomicLong totalTimeAcrossThreadsNs = new AtomicLong(0L);
//
//        for(int i = 0; i < threads; i ++) {
//
//            new Thread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    long totalTimeNs = 0;
//
//                    for(int j = 0; j < operationsPerThread; j ++) {
//                        long t0Nano = System.nanoTime();
//                        MockOperation mo = new MockOperation();
//                        long t1Nano = System.nanoTime();
//                        s.record(System.currentTimeMillis(), t0Nano, t1Nano, mo);
//                        totalTimeNs += (t1Nano - t0Nano);
//                    }
//
//                    log.info(Thread.currentThread().getName() + " finished");
//
//                    totalTimeAcrossThreadsNs.addAndGet(totalTimeNs);
//
//                    try {
//                        barrier.await();
//
//                        log.info(Thread.currentThread().getName() + " released from barrier");
//
//                    }
//                    catch(Exception e) {
//                        throw new IllegalStateException(e);
//                    }
//
//                }
//            }, "Pump " + i).start();
//        }
//
//        // wait for all pump threads to finish
//
//        barrier.await();
//        log.info(Thread.currentThread().getName() + " released from barrier");
//
//        log.info("sleeping for " + (interval / 1000L) + " seconds ...");
//        Thread.sleep(interval);
//
//        log.info("stopping the sampler ...");
//
//        s.stop();
//
//        log.info("sampler stopped");
//
//        List<SamplingInterval> samples = msc.getSamplingIntervals();
//
//        log.info(samples.size() + " samples collected");
//
//        long successful = 0;
//
//        for(SamplingInterval si: samples) {
//            successful += si.getCounterValues(MockOperation.class).getSuccessCount();
//        }
//
//        log.info(successful + " successful operations");
//        assertEquals(successful, threads * operationsPerThread);
//
//        // make sure the interval duration is constant and equal with the sampling interval
//
//        for(SamplingInterval si: samples) {
//            assertEquals(
//                "the interval duration " + si.getDurationMs() + " ms is not equal to " + interval + " ms for " + si,
//                interval, si.getDurationMs());
//        }
//
//        log.info("average allocation time in nanoseconds:");
//
//        Format DECIMAL_FORMAT = new DecimalFormat("#.00");
//
//        int index = 0;
//        long totalSc = 0L;
//        long totalScd = 0L;
//
//        for(SamplingInterval si: samples) {
//
//            CounterValues cv = si.getCounterValues(MockOperation.class);
//
//            long successCount = cv.getSuccessCount();
//            long successCumulatedDuration = cv.getSuccessCumulatedDurationNano();
//
//            double averageRequestDuration = ((double) successCumulatedDuration)/successCount;
//            double successPerSec = Statistics.calculateRate(
//                    successCount, si.getDurationMs(), MeasureUnit.MILLISECOND, MeasureUnit.SECOND);
//
//            log.info("sample " + index++ + ": " + si + " " +
//                successCount + " invocations, " +
//                (successCount == 0 ? "N/A" : DECIMAL_FORMAT.format(averageRequestDuration)) + " ns/invocation, " +
//                successPerSec + " ops/sec");
//
//            assertEquals(0L, cv.getFailureCount());
//            assertEquals(0L, cv.getFailureCumulatedDurationNano());
//
//            totalSc += successCount;
//            totalScd += successCumulatedDuration;
//        }
//
//        double sampledAverage = ((double)totalScd)/totalSc;
//        double directlyReadAverage = ((double)totalTimeAcrossThreadsNs.get())/successful;
//
//        log.info("average operation time in nanoseconds (without sampling): " + DECIMAL_FORMAT.format(directlyReadAverage));
//        log.info("average operation time in nanoseconds (with sampling):    " + DECIMAL_FORMAT.format(sampledAverage));
//
//        assertEquals(directlyReadAverage, sampledAverage, 0.01);
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
