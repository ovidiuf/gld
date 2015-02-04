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

package com.novaordis.cld;

import com.novaordis.cld.mock.MockCacheService;
import com.novaordis.cld.mock.MockConfiguration;
import com.novaordis.cld.mock.MockKeyStore;
import com.novaordis.cld.mock.MockStatistics;
import com.novaordis.cld.strategy.load.MockLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SingleThreadedRunnerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SingleThreadedRunnerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void setRunning(SingleThreadedRunner r)
    {
        r.running = true;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullConfig() throws Exception
    {
        try
        {
            new SingleThreadedRunner(
                "TEST", null, new MockLoadStrategy(), new CollectorBasedStatistics(null), new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null config");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullStatistics() throws Exception
    {
        try
        {
            new SingleThreadedRunner(
                "TEST", new MockConfiguration(), new MockLoadStrategy(), null, new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null statistics");
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
                "TEST", new MockConfiguration(), new MockLoadStrategy(), new MockStatistics(), null);
            fail("should fail with IllegalArgumentException, null barrier");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullCacheService() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();

        assertNull(mc.getCacheService());

        try
        {
            new SingleThreadedRunner(
                "TEST", mc, new MockLoadStrategy(), new MockStatistics(), new CyclicBarrier(1));
            fail("should fail with IllegalArgumentException, null barrier");
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
        mc.setCacheService(new MockCacheService());
        MockStatistics ms = new MockStatistics();
        LoadStrategy mls = new MockLoadStrategy();
        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, mls, ms, cb);

        assertEquals("TEST", st.getName());

        // we simulate the running runner
        st.running = true;

        st.run();

        assertEquals(0, cb.getNumberWaiting());
    }

    @Test
    public void insureThatKeyStoreIsClosedOnExit() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());
        MockStatistics ms = new MockStatistics();

        MockKeyStore mks = new MockKeyStore();
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy();
        mockLoadStrategy.setKeyStore(mks);
        CyclicBarrier cb = new CyclicBarrier(1);

        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, mockLoadStrategy, ms, cb);

        KeyStore ks = mockLoadStrategy.getKeyStore();
        ks.start();
        assertTrue(ks.isStarted());

        // we simulate the running runner
        st.running = true;

        st.run();

        assertFalse(ks.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
