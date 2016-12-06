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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class ServiceConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

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

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract ServiceConfiguration getServiceConfigurationToTest(Map<String, Object> map) throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
