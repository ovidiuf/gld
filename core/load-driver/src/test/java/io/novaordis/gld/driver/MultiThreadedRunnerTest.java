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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MultiThreadedRunnerTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MultiThreadedRunnerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

//    @Test
//    public void stop() throws Exception {
//
//        MockService ms = new MockService();
//        MockLdLoadStrategy mst = new MockLdLoadStrategy();
//        MockSampler msp = new MockSampler();
//        final boolean background = true;
//        int threadCount = 1;
//
//        MultiThreadedRunner r = getMultiThreadedRunnerToTest(ms, mst, msp, background, threadCount);
//
//        //
//        // make sure I can stop without problems
//        //
//
//        r.stop();
//
//        //
//        // make sure the second stop is a noop
//        //
//
//        r.stop();
//    }
//
//    @Test
//    public void timeLimitedRun_OneThread() throws Exception {
//
//        MockService ms = new MockService();
//        ms.setVerbose(false);
//        MockLdLoadStrategy mst = new MockLdLoadStrategy();
//        mst.setVerbose(false);
//        MockSampler msp = new MockSampler();
//        final boolean background = true;
//        int threadCount = 1;
//
//        MultiThreadedRunner r = getMultiThreadedRunnerToTest(ms, mst, msp, background, threadCount);
//
//        assertFalse(r.isRunning());
//
//        //
//        // init it
//        //
//
//        //
//        // the runner should run al least a second, then stop
//        //
//
//        Duration duration = new Duration(500L);
//
//        r.setDuration(duration);
//
//        //
//        // un-latch the exit guard, we don't need that now
//        //
//        r.getExitGuard().allowExit();
//
//        // the runner will execute on the test's thread and it it should release it after the set duration
//
//        long t0 = System.currentTimeMillis();
//
//        r.run();
//
//        long t1 = System.currentTimeMillis();
//
//        long elapsed = t1 - t0;
//
//        log.info("elapsed: " + elapsed + " ms");
//
//        assertTrue(elapsed >= duration.getMilliseconds());
//
//        //
//        // make sure there was actually activity
//        //
//
//        Map<Thread, Integer> m = ms.getPerThreadInvocationCountMap();
//        assertEquals(1, m.size());
//        Integer invocationCount = m.values().iterator().next();
//        log.info("invocation count " + invocationCount);
//        assertTrue(invocationCount > 0);
//    }
//
//    @Test
//    public void timeLimitedRun_SeveralThreads() throws Exception {
//
//        MockService ms = new MockService();
//        MockLdLoadStrategy mst = new MockLdLoadStrategy();
//        MockSampler msp = new MockSampler();
//        final boolean background = true;
//
//        //
//        // 10 threads
//        //
//
//        int threadCount = 10;
//
//
//        MultiThreadedRunner r = getMultiThreadedRunnerToTest(ms, mst, msp, background, threadCount);
//
//        //
//        // the runner should run al least a second, then stop
//        //
//
//        Duration duration = new Duration(500L);
//
//        r.setDuration(duration);
//
//        assertFalse(r.isRunning());
//
//        //
//        // un-latch the exit guard, we don't need that now
//        //
//        r.getExitGuard().allowExit();
//
//        // the runner will execute on the test's thread and it it should release it after the set duration
//
//        long t0 = System.currentTimeMillis();
//
//        r.run();
//
//        long t1 = System.currentTimeMillis();
//
//        long elapsed = t1 - t0;
//
//        log.info("elapsed: " + elapsed + " ms");
//
//        assertTrue(elapsed >= duration.getMilliseconds());
//
//        //
//        // make sure there was actually activity
//        //
//
//        Map<Thread, Integer> m = ms.getPerThreadInvocationCountMap();
//        assertEquals(threadCount, m.size());
//
//        for(Thread t: m.keySet()) {
//
//            Integer invocationCount = m.next(t);
//            log.info("invocation count for " + t + ": " + invocationCount);
//            assertTrue(invocationCount > 0);
//        }
//    }
//
//    @Test
//    public void exitGuardIsUnlatchedInBackground() throws Exception {
//
//        MockService ms = new MockService();
//        MockLdLoadStrategy mst = new MockLdLoadStrategy(3);
//        MockSampler msp = new MockSampler();
//        final boolean background = true;
//        int threadCount = 1;
//
//        //
//        // the mock load strategy should build three operations and exit; the multi-threaded runner must not block
//        //
//
//        final MultiThreadedRunner r = getMultiThreadedRunnerToTest(ms, mst, msp, background, threadCount);
//
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        //
//        // execute the runner on a separate thread, so if it blocks we can fail
//        //
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                try {
//
//                    r.run();
//                    latch.countDown();
//
//                }
//                catch(Exception e) {
//
//                    //
//                    // we failed
//                    //
//
//                    log.error("we failed", e);
//                    fail("runner threw an Exception when we were not expecting to");
//                }
//            }
//        }, "runner-executing thread").start();
//
//        int timeoutSecs = 5;
//
//        if (!latch.await(timeoutSecs, TimeUnit.SECONDS)) {
//
//            fail("multi-threaded run() took loner than " + timeoutSecs +
//                    " seconds, it means the exit guard was not unlatched as expected");
//        }
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract MultiThreadedRunner getMultiThreadedRunnerToTest(
            Service service, Sampler sampler, KeyStore keyStore, boolean background, int threadCount) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
