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

package com.novaordis.gld;

import com.novaordis.gld.mock.MockCacheService;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.mock.MockKeyStore;
import com.novaordis.gld.mock.MockSampler;
import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.sampler.SamplerImpl;
import com.novaordis.gld.strategy.load.cache.MockLoadStrategy;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SingleThreadedRunnerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SingleThreadedRunnerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullConfig() throws Exception
    {
        try
        {
            new SingleThreadedRunner("TEST", null, new MockLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null config");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullSampler() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        try
        {
            new SingleThreadedRunner("TEST", mc, new MockLoadStrategy(), null, new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null sampler");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullBarrier() throws Exception
    {
        try
        {
            new SingleThreadedRunner(
                "TEST", new MockConfiguration(), new MockLoadStrategy(), new SamplerImpl(), null);
            fail("should fail with IllegalArgumentException, null barrier");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullService() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();

        assertNull(mc.getService());

        try
        {
            new SingleThreadedRunner(
                "TEST", mc, new MockLoadStrategy(), new SamplerImpl(), new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null service");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructorAndRun() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());

        LoadStrategy mls = new MockLoadStrategy(1);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);

        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, mls, s, cb);

        assertEquals("TEST", st.getName());

        // we simulate the running runner
        st.running = true;

        s.start();

        st.run();

        assertEquals(0, cb.getNumberWaiting());
    }

    @Test
    public void insureThatKeyStoreIsClosedOnExit() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());

        MockKeyStore mks = new MockKeyStore();
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy(1);
        mockLoadStrategy.setKeyStore(mks);

        Sampler s = new SamplerImpl(0L, 1000L);
        s.registerOperation(MockOperation.class);

        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, mockLoadStrategy, s, cb);

        KeyStore ks = mockLoadStrategy.getKeyStore();
        ks.start();

        assertTrue(ks.isStarted());

        // we simulate the running runner
        st.running = true;

        s.start();

        st.run();

        assertFalse(ks.isStarted());
    }

    @Test
    public void insureSleepWorks() throws Exception
    {
        long sleepMs = 250L;

        MockConfiguration mc = new MockConfiguration();
        mc.setSleepMs(sleepMs);
        mc.setService(new MockService());

        MockSampler mockSampler = new MockSampler();
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy(1);

        CyclicBarrier barrier = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, mockLoadStrategy, mockSampler, barrier);
        st.running = true;

        long t0 = System.currentTimeMillis();

        st.run();

        long t1 = System.currentTimeMillis();

        assertTrue(t1 - t0 >= sleepMs);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
