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
import static org.junit.Assert.assertNotEquals;
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

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.cache, c.getType());
    }

    @Test
    public void type_jms() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "jms");

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.jms, c.getType());
    }

    @Test
    public void type_http() throws Exception {

        Map<String, String> m = new HashMap<>();
        m.put(ServiceConfiguration.TYPE_LABEL, "http");

        ServiceConfigurationBase c = new ServiceConfigurationBase(m);
        assertEquals(ServiceType.http, c.getType());
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
