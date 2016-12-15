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

package io.novaordis.gld.driver.todeplete.command;

import io.novaordis.gld.api.todiscard.Configuration;
import org.slf4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class DeleteCommandTest extends CommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(DeleteCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void initialize_nullCacheService() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        Delete d = getCommandToTest(mc);
//
//        try
//        {
//            d.initialize();
//            fail("Should have failed with IllegalStateException, no cache service");
//        }
//        catch(IllegalStateException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void initialize() throws Exception
//    {
//        MockCacheService mockCacheService = new MockCacheService();
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(mockCacheService);
//
//        mockCacheService.stop();
//        assertFalse(mockCacheService.isStarted());
//
//        // "load" some keys in the cache, even if the service is not started
//        Map<String, String> backingMap = mockCacheService.getBackingMap();
//        backingMap.put("KEY1", "VALUE1");
//        backingMap.put("KEY2", "VALUE2");
//        backingMap.put("KEY3", "VALUE3");
//
//        Delete d = getCommandToTest(mc);
//
//        d.initialize();
//
//        // make sure the cache is started
//        assertTrue(mockCacheService.isStarted());
//
//        // make sure the DeleteKeys loadStrategy is installed
//        LoadStrategy ls = d.getLoadStrategy();
//
//        // make sure the load strategy is also installed in the configuration
//        LoadStrategy ls2 = mc.getLoadStrategy();
//
//        assertEquals(ls, ls2);
//
//        DeleteLoadStrategy dk = (DeleteLoadStrategy)ls;
//
//        // make sure we installed a SetKeyStore
//        SetKeyStore sks = (SetKeyStore)dk.getKeyProvider();
//
//        // make sure the key store is loaded with keys based on the given criteria - by default
//        // DeleteKeys.DEFAULT_KEY_COUNT
//
//        assertTrue(sks.isReadOnly());
//        assertTrue(sks.isStarted());
//
//        // it only contains *one* key
//
//        assertEquals(1, sks.size());
//
//        String key = sks.next();
//
//        assertNull(sks.next());
//
//        assertEquals(0, sks.size());
//
//        assertTrue("KEY1".equals(key) || "KEY2".equals(key) || "KEY3".equals(key));
//    }

    // additional arguments --------------------------------------------------------------------------------------------

//    @Test
//    public void unknownArgument() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        Delete d = new Delete(mc);
//        d.addArgument("blah");
//
//        try
//        {
//            d.initialize();
//            fail("should throw UserErrorException - unknown argument");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void keyCount_MissingNumber() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        Delete d = new Delete(mc);
//        d.addArgument("--key-count");
//
//        try
//        {
//            d.initialize();
//            fail("should throw UserErrorException because we're missing the key count");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void keyCount_NotANumber() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        Delete d = new Delete(mc);
//        d.addArgument("--key-count");
//        d.addArgument("blah");
//
//        try
//        {
//            d.initialize();
//            fail("should throw UserErrorException because wrong key");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//
//            Throwable t = e.getCause();
//
//            assertTrue(t instanceof NumberFormatException);
//        }
//    }
//
//    @Test
//    public void keyCount() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//        Delete d = new Delete(mc);
//        d.addArgument("--key-count");
//        d.addArgument("3");
//
//        d.initialize();
//
//        assertEquals(3, d.getKeysToDelete());
//
//        DeleteLoadStrategy dk = (DeleteLoadStrategy)d.getLoadStrategy();
//
//        assertEquals(3, dk.getKeyCount());
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Delete getCommandToTest(Configuration c)
    {
        return new Delete(c);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
