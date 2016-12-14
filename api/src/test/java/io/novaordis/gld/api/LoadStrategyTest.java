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
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

    // setService() ----------------------------------------------------------------------------------------------------

    @Test
    public void setService() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        assertNull(ls.getService());

        MockService ms = new MockService();
        ls.setService(ms);

        Service s2 = ls.getService();
        assertEquals(ms, s2);
    }

    @Test
    public void setService_InvalidService() throws Exception {

        LoadStrategy ls = getLoadStrategyToTest();

        assertNull(ls.getService());

        MockService ms = new MockService();

        try {
            ls.setService(ms);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("return here", msg);
        }
    }

    // constructors ----------------------------------------------------------------------------------------------------

//    @Test
//    public void nullArguments() throws Exception {
//
//        LoadStrategy s = getLoadStrategyToTest(null, null, -1);
//
//        Configuration c = getConfigurationToTestWith();
//
//        s.configure(c, null, -1);
//
//        // we should be fine, null means no more arguments to look at
//    }
//
//    @Test
//    public void fromOutOfBounds_InferiorLimit() throws Exception {
//
//        LoadStrategy s = getLoadStrategyToTest(null, null, -1);
//
//        List<String> args = Arrays.asList("blah", "blah", "blah");
//
//        Configuration c = getConfigurationToTestWith();
//
//        try {
//
//            s.configure(c, args, -1);
//            fail("should fail with ArrayIndexOutOfBoundsException because from is lower than acceptable");
//        }
//        catch(ArrayIndexOutOfBoundsException e) {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void nullConfiguration() throws Exception {
//
//        LoadStrategy s = getLoadStrategyToTest(null, null, -1);
//
//        List<String> args = Arrays.asList("blah", "blah", "blah");
//
//        try {
//
//            s.configure(null, args, 1);
//            fail("should fail with IllegalArgumentException on account of null configuration");
//        }
//        catch(IllegalArgumentException e) {
//            log.info(e.getMessage());
//        }
//    }
//
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadStrategy getLoadStrategyToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
