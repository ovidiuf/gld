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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceConfigurationTest;
import io.novaordis.gld.api.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class ServiceConfigurationBaseTest extends ServiceConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceConfigurationBaseTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // type ------------------------------------------------------------------------------------------------------------

    @Test
    public void missingType() throws Exception {

        Map<String, String> m = new HashMap<>();

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("missing service type"));
        }
    }

    @Test
    public void unknownType() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "no-such-type");

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("unknown service type 'no-such-type'"));
            Throwable t = e.getCause();
            assertNotNull(t);
        }
    }

    @Test
    public void type_cache() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "cache");
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.cache, c.getType());
    }

    @Test
    public void type_jms() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "jms");
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.jms, c.getType());
    }

    @Test
    public void type_http() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "http");
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.http, c.getType());
    }

    // implementation --------------------------------------------------------------------------------------------------

    @Test
    public void missingImplementation() throws Exception {

        Map<String, String> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("missing implementation"));
        }
    }

    @Test
    public void wrongImplementationType() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, 1);

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("the implementation should be a string, but it is a\\(n\\) .*"));
        }
    }

    @Test
    public void implementation() throws Exception {

        Map<String, String> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfigurationBase scb = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.cache, scb.getType());
        assertEquals("local", scb.getImplementation());
    }

    // load strategy name ----------------------------------------------------------------------------------------------

    @Test
    public void missingLoadStrategyName() throws Exception {

        Map<String, String> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("missing load strategy name"));
        }
    }

    @Test
    public void wrongLoadStrategyType() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, 1);

        try {

            new ServiceConfigurationBase(m);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("the load strategy name should be a string, but it is a\\(n\\) .*"));
        }
    }

    @Test
    public void loadStrategyName() throws Exception {

        Map<String, String> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfigurationBase scb = new ServiceConfigurationBase(m);
        assertEquals("test", scb.getLoadStrategyName());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected ServiceConfigurationBase getServiceConfigurationToTest(Map map) throws Exception {

        return new ServiceConfigurationBase(map);
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
