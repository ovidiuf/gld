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

package io.novaordis.gld.api;

import io.novaordis.gld.api.cache.MockCacheService;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public abstract class LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // init() ----------------------------------------------------------------------------------------------------------

    @Test
    public void implementationsAreRequiredToFailIfTheyEncounterUnknownConfigurationOptions() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();

        Map<String, Object> mockRawConfig = new HashMap<>();
        mockRawConfig.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        mockRawConfig.put("unknown-configuration-element", "some-value");
        msc.set(mockRawConfig, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        try {

            s.init(msc, mlc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("unknown " + s.getName() + " load strategy configuration option(s): "));
        }
    }

    @Test
    public void configurationContainsInconsistentName() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockServiceConfiguration msc = new MockServiceConfiguration();

        Map<String, Object> mockRawConfig = new HashMap<>();
        mockRawConfig.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "wrong-name");
        msc.set(mockRawConfig, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        try {

            s.init(msc, mlc);
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("inconsistent load strategy name, expected "));
        }
    }

    @Test
    public void init() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();

        Map<String, Object> mockRawConfig = new HashMap<>();
        mockRawConfig.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        msc.set(mockRawConfig, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        s.init(msc, mlc);

        //
        // we did not linked to the service yet
        //
        assertNull(s.getService());

        //
        // link and test
        //
        MockCacheService ms = new MockCacheService();
        s.setService(ms);

        Service s2 = s.getService();
        assertEquals(ms, s2);

        LoadStrategyBase lsb = (LoadStrategyBase)s;

        //
        // make sure the key provider is installed and started
        //

        KeyProvider p = lsb.getKeyProvider();
        assertNotNull(p);
        assertTrue(p.isStarted());
    }

    // identity and defaults -------------------------------------------------------------------------------------------

    @Test
    public void identityAndDefaults() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        // unlimited
        assertNull(ls.getOperations());

        // unlimited
        assertNull(ls.getRemainingOperations());
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        assertFalse(s.isStarted());

        assertNull(s.getKeyProvider());

        MockKeyProvider mkp = new MockKeyProvider();
        s.setKeyProvider(mkp);

        assertEquals(mkp, s.getKeyProvider());

        s.start();

        assertTrue(s.isStarted());
        assertTrue(s.getKeyProvider().isStarted());

        //
        // idempotence
        //
        s.start();

        assertTrue(s.isStarted());
        assertTrue(s.getKeyProvider().isStarted());

        s.stop();

        assertFalse(s.isStarted());
        assertFalse(s.getKeyProvider().isStarted());

        //
        // idempotence
        //
        s.stop();

        assertFalse(s.isStarted());
        assertFalse(s.getKeyProvider().isStarted());
    }

    // setService() ----------------------------------------------------------------------------------------------------

    @Test
    public void setService() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        assertNull(ls.getService());

        //
        // the load strategy is not initialized, so we can associate it with any service
        //

        ServiceType type = ls.getServiceType();

        //
        // build a MockService that has the type we want
        //

        MockService ms = new MockService();
        ms.setServiceType(type);

        ls.setService(ms);

        Service s2 = ls.getService();
        assertEquals(ms, s2);

        //
        // sever the relationship
        //

        ls.setService(null);
        assertNull(ls.getService());
    }

    /**
     * Make sure setService() cannot be used to set a wrong service.
     */
    @Test
    public void setInvalidServiceBeforeInitialization() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        ServiceType type = ls.getServiceType();
        assertNotNull(type);

        MockService ms = new MockService();
        assertNotEquals(type, ms.getType());

        try {

            ls.setService(ms);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("cannot associate a mock service with a " + type.name() + " load strategy", msg);
        }
    }

    @Test
    public void setInvalidServiceAfterInitialization() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        MockServiceConfiguration msc = new MockServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        ServiceType type = s.getServiceType();

        assertNotNull(type);

        MockService ms = new MockService();
        assertNotEquals(type, ms.getType());

        try {

            s.setService(ms);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("cannot associate a mock service with a " + type + " load strategy", msg);
        }
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void uninitializedStrategyShouldFailUponFirstUse() throws Exception {

        LoadStrategy s = getLoadStrategyToTest();

        try {

            s.next(null, null, false);
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("was not initialized"));
        }
    }

    // maxOperations()/remainingOperations() ---------------------------------------------------------------------------

    @Test
    public void maxOperations_remainingOperations() throws Exception {

        LoadStrategyBase lsb = (LoadStrategyBase)getLoadStrategyToTest();

        assertNull(lsb.getOperations());
        assertNull(lsb.getRemainingOperations());

        lsb.setOperations(1L);
        assertEquals(1L, lsb.getOperations().longValue());
        assertEquals(1L, lsb.getRemainingOperations().longValue());

        //
        // produce the operation
        //

        Operation o = lsb.next(null, null, false);

        assertNotNull(o);

        assertEquals(1L, lsb.getOperations().longValue());
        assertEquals(0L, lsb.getRemainingOperations().longValue());

        //
        // must not produce operations anymore
        //

        Operation o2 = lsb.next(null, null, false);

        assertNull(o2);

        assertEquals(1L, lsb.getOperations().longValue());
        assertEquals(0L, lsb.getRemainingOperations().longValue());
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void next_Unlimited() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        assertNull(ls.getOperations());

        Operation o = ls.next(null, null, false);
        assertNotNull(o);

        Operation o2 = ls.next(o, null, false);
        assertNotNull(o2);

        Operation o3 = ls.next(o2, null, false);
        assertNotNull(o3);
    }

    @Test
    public void next_Limited() throws Exception {

        LoadStrategyBase lsb = (LoadStrategyBase)getLoadStrategyToTest();

        lsb.setOperations(2L);

        Operation o = lsb.next(null, null, false);
        assertNotNull(o);

        Operation o2 = lsb.next(o, null, false);
        assertNotNull(o2);

        Operation o3 = lsb.next(o2, null, false);
        assertNull(o3);

        Operation o4 = lsb.next(o2, null, false);
        assertNull(o4);
    }

    @Test
    public void next_Multithreaded_ByDefaultProducesAnUnlimitedNumberOfOperations() throws Exception {

        final LoadStrategy ls = getLoadStrategyToTest();

        assertNull(ls.getOperations());

        int threadCount = 1000;
        int operationsRequestedPerThread = 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicInteger producedOperationCount = new AtomicInteger(0);
        final Exception[] exceptions = new Exception[threadCount];

        for(int i = 0; i < threadCount; i ++) {

            new Thread(new TestThreadContext(
                    i, operationsRequestedPerThread, ls, exceptions, producedOperationCount, latch),
                    "test thread " + i).start();
        }

        latch.await();

        //
        // make sure there were no exceptions
        //
        for(int i = 0; i < threadCount; i ++) {

            if (exceptions[i] != null) {

                fail("thread " + i + " generated exception: " + exceptions[i]);
            }
        }

        int expected = threadCount * operationsRequestedPerThread;
        int actual = producedOperationCount.get();
        log.info("expected: " + expected + ", actual: " + actual);
        assertEquals(expected, actual);
    }

    @Test
    public void next_Multithreaded_LimitedNumberOfOperations() throws Exception {

        final LoadStrategyBase ls = (LoadStrategyBase)getLoadStrategyToTest();

        long operationsPerStrategy = 1000;
        ls.setOperations(operationsPerStrategy);

        int threadCount = 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final AtomicInteger producedOperationCount = new AtomicInteger(0);
        final Exception[] exceptions = new Exception[threadCount];

        for(int i = 0; i < threadCount; i ++) {

            new Thread(new TestThreadContext(
                    i, -1, ls, exceptions, producedOperationCount, latch),
                    "test thread " + i).start();
        }

        latch.await();

        //
        // make sure there were no exceptions
        //
        for(int i = 0; i < threadCount; i ++) {

            if (exceptions[i] != null) {

                fail("thread " + i + " generated exception: " + exceptions[i]);
            }
        }

        int actual = producedOperationCount.get();
        log.info("expected: " + operationsPerStrategy + ", actual: " + actual);
        assertEquals(operationsPerStrategy, actual);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadStrategy getLoadStrategyToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

    private static class TestThreadContext implements Runnable {

        private int id;
        private long operationsRequestedPerThread;
        private Exception[] exceptions;
        private LoadStrategy loadStrategy;
        private AtomicInteger producedOperationCount;
        private CountDownLatch latch;

        /**
         * @param operationsRequestedPerThread if -1, it means we keep requesting operations until the load strategy
         *                                     runs out.
         */
        public TestThreadContext(int id, long operationsRequestedPerThread, LoadStrategy ls,
                                 Exception[] exceptions, AtomicInteger producedOperationCount, CountDownLatch latch) {

            this.id = id;
            this.operationsRequestedPerThread = operationsRequestedPerThread;
            this.loadStrategy = ls;
            this.exceptions = exceptions;
            this.producedOperationCount = producedOperationCount;
            this.latch = latch;
        }

        @Override
        public void run() {

            try {

                Operation last = null;

                if (operationsRequestedPerThread == -1) {

                    operationsRequestedPerThread = Long.MAX_VALUE;
                }

                for (long j = 0; j < operationsRequestedPerThread; j++) {

                    Operation current = loadStrategy.next(last, null, false);

                    if (current == null) {

                        //
                        // the strategy ran out of operations, exit
                        //

                        return;
                    }

                    last = current;
                    producedOperationCount.incrementAndGet();
                }
            }
            catch(Exception e) {

                exceptions[id] = e;
            }
            finally {


                //
                // exit after the set number of operations have been requested
                //

                latch.countDown();
            }
        }
    }

}
