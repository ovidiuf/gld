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
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import io.novaordis.utilities.UserErrorException;
import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SingleThreadedRunnerTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SingleThreadedRunnerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Use this to enable run() for individual invocations.
     */
    public static void setRunning(SingleThreadedRunner st) {
        st.running = true;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullConfig() throws Exception {

        try {

            new SingleThreadedRunner(
                    "TEST", null, new MockLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1),
                    new AtomicBoolean(false), -1L);

            fail("should fail with IllegalArgumentException, null config");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullSampler() throws Exception {

        try  {

            new SingleThreadedRunner(
                    "TEST", new MockService(), new MockLoadStrategy(), null, new CyclicBarrier(1),
                    new AtomicBoolean(false), -1L);

            fail("should fail with IllegalArgumentException, null sampler");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void nullBarrier() throws Exception {

        try {

            new SingleThreadedRunner(
                "TEST", new MockService(), new MockLoadStrategy(), new SamplerImpl(), null,
                    new AtomicBoolean(false), -1L);

            fail("should fail with IllegalArgumentException, null barrier");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void nullService() throws Exception {

        try {

            new SingleThreadedRunner(
                "TEST", new MockService(), new MockLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1),
                    new AtomicBoolean(false), -1L);

            fail("should fail with IllegalArgumentException, null service");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void nullDurationExpiredBoolean() throws Exception {

        try {

            new SingleThreadedRunner(
                    "TEST", new MockService(), new MockLoadStrategy(), new SamplerImpl(),
                    new CyclicBarrier(1), null, -1L);

            fail("should fail with IllegalArgumentException, null duration expired");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void constructorAndRun() throws Exception {

        LoadStrategy mls = new MockLoadStrategy(1);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);

        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner(
                "TEST", new MockService(), mls, s, cb, new AtomicBoolean(false), -1L);

        assertEquals("TEST", st.getName());

        // we simulate the running runner
        setRunning(st);

        s.start();

        st.run();

        assertEquals(0, cb.getNumberWaiting());
    }

    @Test
    public void insureThatKeyStoreIsClosedOnExit() throws Exception {

        MockKeyStore mks = new MockKeyStore();
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy(1);
        mockLoadStrategy.setKeyStore(mks);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);

        CyclicBarrier cb = new CyclicBarrier(1);

        MockService ms = new MockService();

        SingleThreadedRunner st =
                new SingleThreadedRunner("TEST", ms, mockLoadStrategy, s, cb, new AtomicBoolean(false), -1L);

        KeyStore ks = mockLoadStrategy.getKeyStore();
        ks.start();

        assertTrue(ks.isStarted());

        // we simulate the running runner
        setRunning(st);

        s.start();

        st.run();

        TestCase.assertFalse(ks.isStarted());
    }

    @Test
    public void insureSleepWorks() throws Exception {

        long sleepMs = 250L;

        MockService ms = new MockService();

        MockSampler mockSampler = new MockSampler();
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy(1);

        CyclicBarrier barrier = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner(
                "TEST", ms, mockLoadStrategy, mockSampler, barrier, new AtomicBoolean(false), sleepMs);

        setRunning(st);

        long t0 = System.currentTimeMillis();

        st.run();

        long t1 = System.currentTimeMillis();

        assertTrue(t1 - t0 >= sleepMs);
    }

    @Test
    public void run_RuntimeIsShuttingDown() throws Exception {

        //
        // we install a mock load strategy that "shuts the runtime down" after the first operation
        //

        final AtomicBoolean durationExpired = new AtomicBoolean(false);

        LoadStrategy ls = new LoadStrategy() {

            private boolean cleanupOperationIssued = false;

            @Override
            public String getName() {
                throw new RuntimeException("getName() NOT YET IMPLEMENTED");
            }

            @Override
            public void configure(Configuration configuration, List<String> arguments, int from) throws Exception {
                throw new RuntimeException("configure() NOT YET IMPLEMENTED");
            }

            @Override
            public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {

                if (runtimeShuttingDown) {

                    if (cleanupOperationIssued) {
                        return null;
                    }

                    //
                    // returns a "cleanup" mock operation and then null
                    //
                    cleanupOperationIssued = true;
                    return new MockCleanupOperation();
                }
                else {

                    //
                    // we trigger shutdown after the first invocation
                    //
                    durationExpired.set(true);
                    return new MockOperation();
                }
            }

            @Override
            public Set<Class<? extends Operation>> getOperationTypes() {
                throw new RuntimeException("getOperationTypes() NOT YET IMPLEMENTED");
            }

            @Override
            public KeyStore getKeyStore() {

                return null;
            }
        };

        final List<Operation> operations = new ArrayList<>();

        Service s = new Service() {

            @Override
            public void setConfiguration(Configuration c) {
                throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
            }

            @Override
            public void setTarget(List<Node> nodes) {
                throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
            }

            @Override
            public void configure(List<String> commandLineArguments) throws UserErrorException {
                throw new RuntimeException("configure() NOT YET IMPLEMENTED");
            }

            @Override
            public ContentType getContentType() {
                throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
            }

            @Override
            public void start() throws Exception {
                throw new RuntimeException("start() NOT YET IMPLEMENTED");
            }

            @Override
            public void stop() throws Exception {
                throw new RuntimeException("stop() NOT YET IMPLEMENTED");
            }

            @Override
            public boolean isStarted() {
                throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
            }

            @Override
            public void perform(Operation o) throws Exception {

                operations.add(o);
            }
        };

        MockSampler ms = new MockSampler();
        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner r = new SingleThreadedRunner("TEST", s, ls, ms, cb, durationExpired, -1L);

        //
        // we simulate the running runner without actually have to start the internal thread
        //
        setRunning(r);

        r.run();

        //
        // the service accumulates exactly two operations, of which the first is a MockOperation and the second
        // is a MockCleanupOperation
        //

        assertEquals(2, operations.size());

        Operation o = operations.get(0);
        assertTrue(o instanceof MockOperation);
        assertFalse(o instanceof MockCleanupOperation);

        Operation o2 = operations.get(1);
        assertTrue(o2 instanceof MockCleanupOperation);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
