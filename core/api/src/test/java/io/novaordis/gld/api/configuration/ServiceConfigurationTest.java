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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
public abstract class ServiceConfigurationTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // Untyped Access --------------------------------------------------------------------------------------------------

    @Test
    public void untypedAccess_AllRawConfiguration() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> lsc = new HashMap<>();
        lsc.put(LoadStrategy.NAME_LABEL, "test");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        Map<String, Object> uam = c.get();

        assertEquals(3, uam.size());
        assertEquals(ServiceType.cache.toString(), uam.get(ServiceConfiguration.TYPE_LABEL));
        assertEquals("embedded", uam.get(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL));
        //noinspection unchecked
        Map<String, Object> lsc2 = (Map<String, Object>)uam.get(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        assertNotNull(lsc2);
        assertEquals(1, lsc2.size());
        assertEquals("test", lsc2.get(LoadStrategy.NAME_LABEL));
    }

    @Test
    public void untypedAccess_NoSuchPath() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, ServiceType.cache.toString());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> lsc = new HashMap<>();
        lsc.put(LoadStrategy.NAME_LABEL, "test");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        Map<String, Object> uam = c.get("no-such-key");
        assertTrue(uam.isEmpty());
    }

    // service type processing -----------------------------------------------------------------------------------------

    @Test
    public void missingType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

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
        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        try {

            c.getType();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("unknown service type 'no-such-type'.*"));
            Throwable t = e.getCause();
            assertNotNull(t);
        }
    }

    @Test
    public void getType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(LoadStrategy.NAME_LABEL, "test");

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));
        ServiceType type = c.getType();
        assertEquals(type.name(), getServiceTypeToTest());
    }

    // implementation --------------------------------------------------------------------------------------------------

    @Test
    public void getImplementationConfiguration_MissingKey() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        try {

            c.getImplementationConfiguration();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("missing implementation configuration"));
        }
    }

    @Test
    public void getImplementationConfiguration_NotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "something");

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        try {

            c.getImplementationConfiguration();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL + "' not a map.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            assertNotNull(e2);
            log.info(e2.getMessage());
        }
    }

    @Test
    public void getImplementationConfiguration() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());

        Map<String, Object> icm = new HashMap<>();
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, icm);
        icm.put(ImplementationConfiguration.EXTENSION_NAME_LABEL, "embedded");

        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(LoadStrategy.NAME_LABEL, "test");

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));
        Assert.assertEquals(ServiceType.valueOf(getServiceTypeToTest()), c.getType());

        ImplementationConfiguration ic = c.getImplementationConfiguration();
        assertNotNull(ic);
    }

    // load strategy configuration -------------------------------------------------------------------------------------

    @Test
    public void getLoadStrategyName_missingLoadStrategyConfig() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

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
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, "something");
        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        try {

            c.getLoadStrategyName();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            Assert.assertTrue(msg.matches("'load-strategy' not a map.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            Assert.assertTrue(msg2.contains("\"something\""));
        }
    }

    @Test
    public void getLoadStrategyName_NoStrategyConfigurationName() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put(ServiceConfiguration.TYPE_LABEL, getServiceTypeToTest());
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");

        Map<String, Object> lsc = Collections.emptyMap();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

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
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");

        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(LoadStrategy.NAME_LABEL, 1);

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

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
        m.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, "embedded");

        Map<String, Object> lsc = new HashMap<>();
        m.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, lsc);
        lsc.put(LoadStrategy.NAME_LABEL, "test");

        ServiceConfiguration c = getConfigurationToTest(m, new File(System.getProperty("basedir")));

        Assert.assertEquals("test", c.getLoadStrategyName());

        Map<String, Object> rawConfig = c.get(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        Assert.assertEquals(1, rawConfig.size());
        Assert.assertEquals("test", rawConfig.get(LoadStrategy.NAME_LABEL));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @param rawConfigurationMap the recursive configuration map, corresponding to the "service" key, as loaded from
     *                            the YAML representation.
     */
    protected abstract ServiceConfiguration getConfigurationToTest(
            Map<String, Object> rawConfigurationMap, File configurationDirectory) throws Exception;

    protected abstract String getServiceTypeToTest();

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
