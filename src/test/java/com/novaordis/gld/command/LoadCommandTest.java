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

package com.novaordis.gld.command;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockCacheService;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.strategy.load.cache.MockLoadStrategy;
import com.novaordis.gld.strategy.storage.MockStorageStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;


public class LoadCommandTest extends CommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LoadCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // initialize()/load strategy --------------------------------------------------------------------------------------

    @Test
    public void noStrategySpecified_UseDefault() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        assertNull(load.getLoadStrategy());

        load.initialize();

        LoadStrategy s = load.getLoadStrategy();

        assertEquals(Load.DEFAULT_CACHE_LOAD_STRATEGY, s);

        // make sure the load strategy is also installed in the configuration
        LoadStrategy s2 = mc.getLoadStrategy();

        assertEquals(s, s2);
    }

    @Test
    public void missingStrategyName() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--strategy");

        assertNull(load.getLoadStrategy());

        try
        {
            load.initialize();
            fail("should fail with UserErrorException because nothing follows --strategy");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nonExistentStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--strategy");
        load.addArgument("IAmQuiteSureThisStrategyDoesNotExist");

        assertNull(load.getLoadStrategy());

        try
        {
            load.initialize();
            fail("should fail with UserErrorException because the strategy does not exist");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
            ClassNotFoundException cnfe = (ClassNotFoundException)e.getCause();
            log.info(cnfe.getMessage());
        }
    }

    @Test
    public void strategyInstantiationFailure() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--strategy");
        load.addArgument("FailureToInstantiate");

        assertNull(load.getLoadStrategy());

        try
        {
            load.initialize();
            fail("should fail with UserErrorException because the strategy failed to instantiate");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            InstantiationException ie = (InstantiationException)e.getCause();
            log.info(ie.getMessage());
        }
    }

    @Test
    public void notALoadStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--strategy");
        load.addArgument("NotA");

        assertNull(load.getLoadStrategy());

        try
        {
            load.initialize();
            fail("should fail with UserErrorException because the strategy does not implement LoadStrategy");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            ClassCastException cce = (ClassCastException)t;
            log.info(cce.getMessage());
        }
    }

    @Test
    public void validLoadStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--strategy");
        load.addArgument("Mock");

        assertNull(load.getLoadStrategy());

        load.initialize();

        LoadStrategy ls = load.getLoadStrategy();

        assertTrue(ls instanceof MockLoadStrategy);

        assertTrue(load.isInitialized());
    }

    // initialize()/load strategy and storage strategy -----------------------------------------------------------------

    @Test
    public void defaultLoadStrategyWithCustomStorageStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--storage-strategy");
        load.addArgument("Mock");
        load.addArgument("--mock-argument");
        load.addArgument("blah");

        assertNull(load.getLoadStrategy());
        assertNull(mc.getStorageStrategy());

        load.initialize();

        assertTrue(load.isInitialized());

        LoadStrategy ls = load.getLoadStrategy();
        assertEquals(ls, Load.DEFAULT_CACHE_LOAD_STRATEGY);

        MockStorageStrategy mss = (MockStorageStrategy)mc.getStorageStrategy();
        assertEquals("blah", mss.getMockArgument());
        assertTrue(mss.isStarted());
    }

    @Test
    public void customLoadStrategyWithCustomStorageStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--load-strategy");
        load.addArgument("Mock");
        load.addArgument("--mock-load-argument");
        load.addArgument("load-blah");

        load.addArgument("--storage-strategy");
        load.addArgument("Mock");
        load.addArgument("--mock-storage-argument");
        load.addArgument("storage-blah");

        assertNull(load.getLoadStrategy());
        assertNull(mc.getStorageStrategy());

        load.initialize();

        assertTrue(load.isInitialized());

        MockLoadStrategy mls = (MockLoadStrategy)load.getLoadStrategy();
        assertEquals("load-blah", mls.getMockLoadArgument());

        MockStorageStrategy mss = (MockStorageStrategy)mc.getStorageStrategy();
        assertEquals("storage-blah", mss.getMockStorageArgument());
        assertTrue(mss.isStarted());
    }

    // read-to-write ratio ---------------------------------------------------------------------------------------------

    @Test
    public void missingReadToWriteValue() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--read-to-write");

        try
        {
            load.initialize();
            fail("should fail with UserErrorException, no actual read-to-write value following");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void missingWriteToReadValue() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--write-to-read");

        try
        {
            load.initialize();
            fail("should fail with UserErrorException, no actual write-to-read value following");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void misspelledRatio() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setCacheService(new MockCacheService());

        Load load = new Load(mc);

        load.addArgument("--write-toread");
        load.addArgument("66");

        try
        {
            load.initialize();
            fail("should fail with UserErrorException, misspelled '--write-to-read'");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Load getCommandToTest(Configuration c)
    {
        return new Load(c);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
