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

package io.novaordis.gld.api.cache;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfigurationTest;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class CacheServiceConfigurationTest extends ServiceConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getKeySize_MissingKeySize_DefaultShouldBeUsed() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        int s = c.getKeySize();
        assertEquals(ServiceConfiguration.DEFAULT_KEY_SIZE, s);
    }

    @Test
    public void getKeySize_WrongType() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        map.put(CacheServiceConfiguration.KEY_SIZE_LABEL, "1024");

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getKeySize();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(e.getMessage());
            assertTrue(msg.matches("'key-size' is not an integer.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("\"1024\""));
        }
    }

    @Test
    public void getKeySize() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        map.put(CacheServiceConfiguration.KEY_SIZE_LABEL, 1024);

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));
        assertEquals(1024, c.getKeySize());
    }

    @Test
    public void getValueSize_MissingKeySize_DefaultShouldBeUsed() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        int s = c.getValueSize();
        assertEquals(CacheServiceConfiguration.DEFAULT_VALUE_SIZE, s);
    }

    @Test
    public void getValueSize_WrongType() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        map.put(CacheServiceConfiguration.VALUE_SIZE_LABEL, "1025");

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getValueSize();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(e.getMessage());
            assertTrue(msg.matches("'value-size' is not an integer.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("\"1025\""));
        }
    }

    @Test
    public void getValueSize() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.name());
        map.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> loadStrategy = new HashMap<>();
        map.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, loadStrategy);
        loadStrategy.put(LoadStrategy.NAME_LABEL, "test");

        map.put(CacheServiceConfiguration.VALUE_SIZE_LABEL, 1025);

        CacheServiceConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        assertEquals(1025, c.getValueSize());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CacheServiceConfiguration getConfigurationToTest(Map<String, Object> map, File cd) throws Exception {

        return new CacheServiceConfigurationImpl(map, cd);
    }

    @Override
    protected String getServiceTypeToTest() {

        return ServiceType.cache.name();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
