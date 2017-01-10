/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.gld.api.extension;

import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.extensions.mock.MockService;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class ExtensionInfrastructureTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // initializeExtensionService() ------------------------------------------------------------------------------------

    @Test
    public void instantiateAndConfigureExtensionService() throws Exception {

        MockServiceConfiguration mc = new MockServiceConfiguration();
        mc.setImplementationConfigurationMap(new HashMap<>());
        mc.set("mock",
                ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL,
                ImplementationConfiguration.EXTENSION_NAME_LABEL);

        Service s = ExtensionInfrastructure.initializeExtensionService(mc);

        assertFalse(s.isStarted());
        assertNull(s.getLoadStrategy());
        assertNull(s.getLoadDriver());

        assertEquals(mc, ((MockService) s).getConfigurationInstanceWeWereConfiguredWith());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
