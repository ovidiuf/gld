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

import io.novaordis.gld.api.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class ServiceConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // untyped access --------------------------------------------------------------------------------------------------

    @Test
    public void untypedAccess_AllRawConfiguration() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        Map<String, Object> lsc = new HashMap<>();
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getServiceConfigurationToTest(m);

        Map<String, Object> uam = c.getMap();

        assertEquals(3, uam.size());
        assertEquals(ServiceType.cache.toString(), uam.get(ServiceConfiguration.TYPE_LABEL));
        assertEquals("local", uam.get(ServiceConfiguration.IMPLEMENTATION_LABEL));
        //noinspection unchecked
        Map<String, Object> lsc2 = (Map<String, Object>)uam.get(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        assertNotNull(lsc2);
        assertEquals(1, lsc2.size());
        assertEquals("test", lsc2.get(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL));
    }

    @Test
    public void untypedAccess_NoSuchPath() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        Map<String, Object> lsc = new HashMap<>();
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getServiceConfigurationToTest(m);

        Map<String, Object> uam = c.getMap("no-such-key");
        assertTrue(uam.isEmpty());
    }

    // service type processing -----------------------------------------------------------------------------------------

    @Test
    public void missingType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getType();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("missing service type"));
        }
    }

    @Test
    public void getType_unknownType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "no-such-type");
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getType();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("unknown service type 'no-such-type'"));
            Throwable t = e.getCause();
            assertNotNull(t);
        }
    }

    @Test
    public void getType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfiguration c = getServiceConfigurationToTest(m);
        ServiceType type = c.getType();
        assertEquals(type.name(), getServiceTypeToTest());
    }

    // implementation --------------------------------------------------------------------------------------------------

    @Test
    public void getImplementation_MissingKey() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getImplementation();
            fail("should throw exception");
        }
        catch(UserErrorException e) {
            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("missing implementation"));
        }
    }

    @Test
    public void getImplementation_wrongType() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, 1);
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getImplementation();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("the implementation should be a string, but it is a\\(n\\) .*"));
        }
    }

    @Test
    public void getImplementation() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfiguration c = getServiceConfigurationToTest(m);
        Assert.assertEquals(ServiceType.valueOf(getServiceTypeToTest()), c.getType());
        Assert.assertEquals("local", c.getImplementation());
    }

    // load strategy configuration -------------------------------------------------------------------------------------

    @Test
    public void getLoadStrategyName_missingLoadStrategyConfig() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getLoadStrategyName();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("missing load strategy configuration"));
        }
    }

    @Test
    public void getLoadStrategyName_wrongLoadStrategyConfigurationType() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, "something");
        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getLoadStrategyName();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("the load strategy configuration should be a map, but it is a\\(n\\) .*"));
        }
    }

    @Test
    public void getLoadStrategyName_NoStrategyConfigurationName() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");

        Map<String, Object> lsc = Collections.emptyMap();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getLoadStrategyName();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("missing load strategy name"));
        }
    }

    @Test
    public void getLoadStrategyName_WrongType() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");

        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, 1);

        ServiceConfiguration c = getServiceConfigurationToTest(m);

        try {

            c.getLoadStrategyName();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("the load strategy name should be a string, but it is a\\(n\\) .*"));
        }
    }

    @Test
    public void getLoadStrategyName_NoExtraConfigurationExceptName() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_LABEL, "local");

        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "test");

        ServiceConfiguration c = getServiceConfigurationToTest(m);

        Assert.assertEquals("test", c.getLoadStrategyName());

        Map<String, Object> rawConfig = c.getMap(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        Assert.assertEquals(1, rawConfig.size());
        Assert.assertEquals("test", rawConfig.get(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @param rawConfigurationMap the recursive configuration map, corresponding to the "service" key, as loaded from
     *                            the YAML representation.
     */
    protected abstract ServiceConfiguration getServiceConfigurationToTest(Map<String, Object> rawConfigurationMap)
            throws Exception;

    protected abstract String getServiceTypeToTest();

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
