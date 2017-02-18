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

package io.novaordis.gld.api.mock.configuration;

import io.novaordis.gld.api.configuration.Configuration;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.OutputConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.configuration.StoreConfiguration;
import io.novaordis.gld.api.service.ServiceType;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private MockServiceConfiguration serviceConfiguration;
    private MockLoadConfiguration loadConfiguration;
    private MockStoreConfiguration storeConfiguration;
    private MockOutputConfiguration outputConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockConfiguration() throws Exception {

        serviceConfiguration = new MockServiceConfiguration();
        loadConfiguration = new MockLoadConfiguration(ServiceType.mock);
        storeConfiguration = new MockStoreConfiguration();
        outputConfiguration = new MockOutputConfiguration();
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public ServiceConfiguration getServiceConfiguration() {

        return serviceConfiguration;
    }

    @Override
    public LoadConfiguration getLoadConfiguration() {

        return loadConfiguration;
    }

    @Override
    public StoreConfiguration getStoreConfiguration() {

        return storeConfiguration;
    }

    @Override
    public OutputConfiguration getOutputConfiguration() {

        return outputConfiguration;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setOutputConfiguration(MockOutputConfiguration oc) {

        this.outputConfiguration = oc;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
