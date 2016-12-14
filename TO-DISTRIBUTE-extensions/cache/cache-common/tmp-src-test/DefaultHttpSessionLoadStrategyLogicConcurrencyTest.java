/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class DefaultHttpSessionLoadStrategyLogicConcurrencyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(DefaultHttpSessionLoadStrategyLogicConcurrencyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void next_concurrencyTest() throws Exception {

        int sessions = 3;
        int writesPerSession = 100;

        int threadCount = 500;
        final int totalInvocationCount = 500000;

        final DefaultHttpSessionLoadStrategyLogic logic =
                new DefaultHttpSessionLoadStrategyLogic(sessions, writesPerSession, null);

        final AtomicLong invocationCount = new AtomicLong(0);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final SessionStatistics sessionStats = new SessionStatistics(writesPerSession);

        for(int i = 0; i < threadCount; i ++) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    while(true) {

                        if (invocationCount.incrementAndGet() > totalInvocationCount) {
                            log.info(Thread.currentThread() + " done");
                            latch.countDown();
                            return;
                        }

                        HttpSessionOperation o = logic.next(false);

                        sessionStats.updateStatistics(o);
                    }
                }
            }, "concurrency test thread " + i).start();
        }

        latch.await();
        log.info("all threads done");

        log.info("updates:           " + sessionStats.getTotalUpdates());
        assertEquals(totalInvocationCount, sessionStats.getTotalUpdates());

        log.info("created sessions:  " + sessionStats.getSessionCount());

        long expectedSessions = totalInvocationCount / (1 + writesPerSession + 1);
        log.info("expected sessions: " + expectedSessions);

        sessionStats.operationStatistics();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------


    /**
     * An instance that maintains all created sessions and the operations generated for each session.
     */
    private static class SessionStatistics {

        final private Map<HttpSessionSimulation, List<HttpSessionOperation>> stats = new HashMap<>();

        private int writesPerSession;

        private long totalUpdates;

        public SessionStatistics(int writesPerSession) {
            this.writesPerSession = writesPerSession;
            this.totalUpdates = 0;
        }

        public void updateStatistics(HttpSessionOperation o) {

            synchronized (stats) {

                totalUpdates ++;

                HttpSessionSimulation s = o.getHttpSession();

                List<HttpSessionOperation> operations = stats.get(s);

                if (operations == null) {
                    operations = new ArrayList<>();
                    stats.put(s, operations);
                }

                operations.add(o);
            }
        }

        public long getTotalUpdates() {
            return totalUpdates;
        }

        public int getSessionCount() {

            synchronized (stats) {
                return stats.size();
            }
        }

        public void operationStatistics() {

            synchronized (stats) {

                for(HttpSessionSimulation s: stats.keySet()) {

                    List<HttpSessionOperation> operations = stats.get(s);

                    int size = operations.size();
                    int expectedSize = 1 + writesPerSession + 1;

                    if (size != expectedSize) {

                        log.error("expected size: " + expectedSize + ", size: " + size);
                    }
                }
            }
        }
    }
}
