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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfigurationTest;
import io.novaordis.gld.api.service.ServiceType;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class JmsServiceConfigurationTest extends ServiceConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void defaultMessageSize() throws Exception {

        JmsServiceConfiguration c = getConfigurationToTest(new HashMap<>(), new File("."));
        assertEquals(ServiceConfiguration.DEFAULT_VALUE_SIZE, c.getMessageSize());
    }

    @Test
    public void nonDefaultMessageSize() throws Exception {

        JmsServiceConfigurationImpl c = getConfigurationToTest(new HashMap<>(), new File("."));
        c.set(123, JmsServiceConfiguration.MESSAGE_SIZE_LABEL);
        assertEquals(123, c.getMessageSize());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JmsServiceConfigurationImpl getConfigurationToTest(Map<String, Object> map, File cd) throws Exception {

        return new JmsServiceConfigurationImpl(map, cd);
    }

    @Override
    protected String getServiceTypeToTest() {

        return ServiceType.jms.name();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
