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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class DeleteLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(DeleteLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

//    @Test
//    public void lifeCycle_DEFAULT_KEY_COUNT() throws Exception {
//
//        MockCacheService mockCacheService = new MockCacheService();
//        mockCacheService.start();
//
//        Map<String, String> backingMap = mockCacheService.getBackingMap();
//        backingMap.put("KEY1", "VALUE1");
//        backingMap.put("KEY2", "VALUE2");
//        backingMap.put("KEY3", "VALUE3");
//
//        Set<String> expected = new HashSet<>(backingMap.keySet());
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(mockCacheService);
//
//        DeleteLoadStrategy dk = new DeleteLoadStrategy();
//
//        dk.configure(mc, Collections.<String>emptyList(), 0);
//
//        assertEquals("Delete", dk.getName());
//
//        Configuration c = dk.getConfiguration();
//        assertEquals(mc, c);
//
//        SetKeyStore sks = (SetKeyStore)dk.getKeyStore();
//        assertEquals(DeleteLoadStrategy.DEFAULT_KEY_COUNT, sks.size());
//
//        Delete d = (Delete)dk.next(null, null, false);
//
//        String key = d.getKey();
//
//        assertTrue(expected.remove(key));
//
//        // no more operations
//
//        assertNull(dk.next(null, null, false));
//
//        log.debug(".");
//    }
//
//    @Test
//    public void lifeCycle_configuredKeyCount_SmallerThanTheTotalNumberOfKeys() throws Exception {
//
//        MockCacheService mockCacheService = new MockCacheService();
//        mockCacheService.start();
//
//        Map<String, String> backingMap = mockCacheService.getBackingMap();
//        backingMap.put("KEY1", "VALUE1");
//        backingMap.put("KEY2", "VALUE2");
//        backingMap.put("KEY3", "VALUE3");
//
//        Set<String> expected = new HashSet<>(backingMap.keySet());
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(mockCacheService);
//
//        // keys to delete - smaller than the number of keys in "cache".
//        int keysToDelete = 2;
//
//        DeleteLoadStrategy dk = new DeleteLoadStrategy(keysToDelete);
//
//        dk.configure(mc, Collections.<String>emptyList(), 0);
//
//        assertEquals(keysToDelete, dk.getKeyCount());
//
//        assertEquals("Delete", dk.getName());
//
//        Configuration c = dk.getConfiguration();
//        assertEquals(mc, c);
//
//        SetKeyStore sks = (SetKeyStore)dk.getKeyStore();
//        assertEquals(keysToDelete, sks.size());
//
//        // we expect 2 delete operations
//
//        Delete d = (Delete)dk.next(null, null, false);
//
//        String key = d.getKey();
//
//        assertTrue(expected.remove(key));
//
//        Delete d2 = (Delete)dk.next(null, null, false);
//
//        String key2 = d2.getKey();
//
//        assertTrue(expected.remove(key2));
//
//        // no more operations
//
//        assertNull(dk.next(null, null, false));
//    }
//
//    @Test
//    public void lifeCycle_configuredKeyCount_LargerThanTheTotalNumberOfKeys() throws Exception {
//
//        MockCacheService mockCacheService = new MockCacheService();
//        mockCacheService.start();
//
//        Map<String, String> backingMap = mockCacheService.getBackingMap();
//        backingMap.put("KEY1", "VALUE1");
//        backingMap.put("KEY2", "VALUE2");
//        backingMap.put("KEY3", "VALUE3");
//
//        Set<String> expected = new HashSet<>(backingMap.keySet());
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(mockCacheService);
//
//        // keys to delete - larger thant the number of keys in "cache"
//        int keysToDelete = 7;
//
//        DeleteLoadStrategy dk = new DeleteLoadStrategy(keysToDelete);
//
//        dk.configure(mc, Collections.<String>emptyList(), 0);
//
//        assertEquals(keysToDelete, dk.getKeyCount());
//
//        assertEquals("Delete", dk.getName());
//
//        Configuration c = dk.getConfiguration();
//        assertEquals(mc, c);
//
//        SetKeyStore sks = (SetKeyStore)dk.getKeyStore();
//        assertEquals(backingMap.size(), sks.size());
//
//        // we expect 3 delete operations
//
//        Delete d = (Delete)dk.next(null, null, false);
//
//        String key = d.getKey();
//
//        assertTrue(expected.remove(key));
//
//        Delete d2 = (Delete)dk.next(null, null, false);
//
//        String key2 = d2.getKey();
//
//        assertTrue(expected.remove(key2));
//
//        Delete d3 = (Delete)dk.next(null, null, false);
//
//        String key3 = d3.getKey();
//
//        assertTrue(expected.remove(key3));
//
//        // no more operations
//
//        assertNull(dk.next(null, null, false));
//    }

    // factory ---------------------------------------------------------------------------------------------------------

    @Test
    public void factory() throws Exception {

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        msc.set(new HashMap<>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(DeleteLoadStrategy.NAME,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.NAME_LABEL);


        DeleteLoadStrategy s = (DeleteLoadStrategy) LoadStrategyFactory.build(msc, mlc);
        assertEquals(DeleteLoadStrategy.NAME, s.getName());

        log.debug(".");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected DeleteLoadStrategy getLoadStrategyToTest() throws Exception {
        return new DeleteLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
