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

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.load.WriteThenReadLoadStrategy;
import io.novaordis.gld.api.cache.operation.Write;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.mock.MockCacheService;
import io.novaordis.gld.api.mock.MockCleanupOperation;
import io.novaordis.gld.api.mock.MockKeyStore;
import io.novaordis.gld.api.mock.MockOperation;
import io.novaordis.gld.api.mock.MockService;
import io.novaordis.gld.api.mock.configuration.MockCacheServiceConfiguration;
import io.novaordis.gld.api.mock.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.mock.load.MockLdLoadStrategy;
import io.novaordis.gld.api.sampler.Sampler;
import io.novaordis.gld.api.sampler.SamplerImpl;
import io.novaordis.gld.api.store.InMemoryStore;
import io.novaordis.gld.api.store.StoredValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
                    "TEST", null, new MockLdLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1),
                    new AtomicBoolean(false), -1L, new MockKeyStore());

            fail("should fail with IllegalArgumentException, null config");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullSampler() throws Exception {

        final Sampler sampler = null;

        //
        // null sampler is OK
        //
        SingleThreadedRunner r = new SingleThreadedRunner(
                "TEST", new MockService(), new MockLdLoadStrategy(), sampler, new CyclicBarrier(1),
                new AtomicBoolean(false), -1L, new MockKeyStore());

        assertNull(r.getSampler());
    }

    @Test
    public void nullBarrier() throws Exception {

        try {

            new SingleThreadedRunner(
                    "TEST", new MockService(), new MockLdLoadStrategy(), new SamplerImpl(), null,
                    new AtomicBoolean(false), -1L, new MockKeyStore());

            fail("should fail with IllegalArgumentException, null barrier");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

//    @Test
//    public void nullService() throws Exception {
//
//        try {
//
//            new SingleThreadedRunner(
//                "TEST", new MockService(), new MockLdLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1),
//                    new AtomicBoolean(false), -1L);
//
//            fail("should fail with IllegalArgumentException, null service");
//        }
//        catch(IllegalArgumentException e) {
//
//            log.info(e.getMessage());
//        }
//    }

    @Test
    public void nullDurationExpiredBoolean() throws Exception {

        try {

            new SingleThreadedRunner(
                    "TEST", new MockService(), new MockLdLoadStrategy(), new SamplerImpl(),
                    new CyclicBarrier(1), null, -1L, new MockKeyStore());

            fail("should fail with IllegalArgumentException, null duration expired");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void constructorAndRun() throws Exception {

        LoadStrategy mls = new MockLdLoadStrategy(1);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);

        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner(
                "TEST", new MockService(), mls, s, cb, new AtomicBoolean(false), -1L, new MockKeyStore());

        assertEquals("TEST", st.getName());

        // we simulate the running runner
        setRunning(st);

        s.start();

        st.run();

        assertEquals(0, cb.getNumberWaiting());
    }

//    @Test
//    public void insureThatKeyStoreIsClosedOnExit() throws Exception {
//
//        MockKeyStore mks = new MockKeyStore();
//        MockLdLoadStrategy mockLoadStrategy = new MockLdLoadStrategy(1);
//        mockLoadStrategy.getKeyProvider(mks);
//
//        Sampler s = new SamplerImpl(0L, 1000L);
//        s.registerOperation(MockOperation.class);
//
//        CyclicBarrier cb = new CyclicBarrier(1);
//
//        MockService ms = new MockService();
//
//        SingleThreadedRunner st =
//                new SingleThreadedRunner("TEST", ms, mockLoadStrategy, s, cb, new AtomicBoolean(false), -1L);
//
//        KeyStore ks = mockLoadStrategy.getKeyProvider();
//        ks.start();
//
//        assertTrue(ks.isStarted());
//
//        // we simulate the running runner
//        setRunning(st);
//
//        s.start();
//
//        st.run();
//
//        TestCase.assertFalse(ks.isStarted());
//    }

    @Test
    public void insureSleepWorks() throws Exception {

        long sleepMs = 250L;

        MockService ms = new MockService();

        MockSampler mockSampler = new MockSampler();
        MockLdLoadStrategy mockLoadStrategy = new MockLdLoadStrategy(1);

        CyclicBarrier barrier = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner(
                "TEST", ms, mockLoadStrategy, mockSampler, barrier, new AtomicBoolean(false),
                sleepMs, new MockKeyStore());

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
            public ServiceType getServiceType() {
                throw new RuntimeException("getServiceType() NOT YET IMPLEMENTED");
            }

            @Override
            public Service getService() {
                throw new RuntimeException("getService() NOT YET IMPLEMENTED");
            }

            @Override
            public void setService(Service s) throws IllegalArgumentException {
                throw new RuntimeException("setService() NOT YET IMPLEMENTED");
            }

            @Override
            public String getName() {
                throw new RuntimeException("getName() NOT YET IMPLEMENTED");
            }

            @Override
            public void init(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {
                throw new RuntimeException("init() NOT YET IMPLEMENTED");
            }

            @Override
            public void start() throws Exception {
                throw new RuntimeException("start() NOT YET IMPLEMENTED");
            }

            @Override
            public boolean isStarted() {
                throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
            }

            @Override
            public void stop() {
                throw new RuntimeException("stop() NOT YET IMPLEMENTED");
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
            public KeyProvider getKeyProvider() {
                throw new RuntimeException("getKeyProvider() NOT YET IMPLEMENTED");
            }

            @Override
            public void setKeyProvider(KeyProvider keyProvider) {
                throw new RuntimeException("setKeyProvider() NOT YET IMPLEMENTED");
            }

            @Override
            public Long getRemainingOperations() {
                throw new RuntimeException("getRemainingOperations() NOT YET IMPLEMENTED");
            }

            @Override
            public String getReusedValue() {
                throw new RuntimeException("getReusedValue() NOT YET IMPLEMENTED");
            }

            @Override
            public boolean isReuseValue() {
                throw new RuntimeException("isReuseValue() NOT YET IMPLEMENTED");
            }

            @Override
            public String computeValue() {
                throw new RuntimeException("computeValue() NOT YET IMPLEMENTED");
            }
        };

        MockService ms = new MockService();

        MockSampler msp = new MockSampler();
        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner r = new SingleThreadedRunner(
                "TEST", ms, ls, msp, cb, durationExpired, -1L, new MockKeyStore());

        //
        // we simulate the running runner without actually have to start the internal thread
        //
        setRunning(r);

        r.run();

        //
        // the service accumulates exactly two operations, of which the first is a MockOperation and the second
        // is a MockCleanupOperation
        //

        List<Operation> operations = ms.getExecutedOperations();

        assertEquals(2, operations.size());

        Operation o = operations.get(0);
        assertTrue(o instanceof MockOperation);
        assertFalse(o instanceof MockCleanupOperation);

        Operation o2 = operations.get(1);
        assertTrue(o2 instanceof MockCleanupOperation);
    }

    // WriteThenReadLoadStrategy - SingleThreadedRunner integration ----------------------------------------------------

    @Test
    public void integration_WriteThenRead_SingleThreadedRunner() throws Exception {

        MockSampler ms = new MockSampler();
        CyclicBarrier barrier = new CyclicBarrier(1);

        MockCacheService mcs = new MockCacheService();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();

        msc.set(WriteThenReadLoadStrategy.NAME,
                CacheServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.NAME_LABEL);

        //
        // only writes
        //

        msc.set(0,
                CacheServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL);

        //
        // use the same value
        //

        msc.set(true,
                CacheServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                WriteThenReadLoadStrategy.REUSE_VALUE_LABEL);


        MockLoadConfiguration mlc = new MockLoadConfiguration(mcs.getType());
        mlc.setOperations(2);
        mlc.setKeySize(3);
        mlc.setValueSize(7);

        //
        // because we're building the instance directly and not with the factory, clean configuration elements
        // that otherwise would have been removed by the factory
        //

        msc.remove(CacheServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.FACTORY_CLASS_LABEL);

        WriteThenReadLoadStrategy wtr = new WriteThenReadLoadStrategy();

        wtr.init(msc, mlc);

        InMemoryStore ks = new InMemoryStore();
        ks.start();

        SingleThreadedRunner st =
                new SingleThreadedRunner("TEST", mcs, wtr, ms, barrier, new AtomicBoolean(false), 0L, ks);

        SingleThreadedRunnerTest.setRunning(st);

        st.run();

        List<OperationThrowablePair> recorded = ms.getRecorded();
        assertEquals(2, recorded.size());

        Write w = (Write)recorded.get(0).operation;
        assertNull(recorded.get(0).throwable);

        String key = w.getKey();
        String value = w.getValue();

        assertNotNull(key);
        assertNotNull(value);

        Write w2 = (Write)recorded.get(1).operation;
        assertNull(recorded.get(1).throwable);

        String key2 = w2.getKey();
        String value2 = w2.getValue();

        assertNotNull(key2);
        assertNotNull(value2);

        // make sure the key is in

        // 1. cache

        assertEquals(value, mcs.get(key));
        assertEquals(value2, mcs.get(key2));

        // 2. key storeFile

        //
        // we only stored keys, not values
        //

        StoredValue v = ks.retrieve(key);
        assertNotNull(v);
        assertTrue(v.notStored());

        StoredValue v2 = ks.retrieve(key2);
        assertNotNull(v2);
        assertTrue(v2.notStored());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
