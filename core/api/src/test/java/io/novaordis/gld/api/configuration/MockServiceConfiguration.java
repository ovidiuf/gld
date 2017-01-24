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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class MockServiceConfiguration extends LowLevelConfigurationBase implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String loadStrategyName;
    private ServiceType serviceType;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockServiceConfiguration() {

        this(new HashMap<>());
    }

    public MockServiceConfiguration(Map<String, Object> raw)  {

        super(raw, new File(System.getProperty("basedir")));
        this.serviceType = ServiceType.mock;
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return serviceType;
    }

    @Override
    public ImplementationConfiguration getImplementationConfiguration() throws UserErrorException {

        return new ImplementationConfigurationImpl(
                get(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL), new File("."));
    }

    @Override
    public String getLoadStrategyName() {

        if (loadStrategyName != null) {

            return loadStrategyName;
        }

        //
        // if not explicitely set, defer to the underlying map
        //
        return get(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.NAME_LABEL);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setLoadStrategyName(String s) {

        this.loadStrategyName = s;
    }

    public void setImplementationConfigurationMap(Map<String, Object> m) {

        set(m, ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL);
    }

    public void setServiceType(ServiceType t) {

        this.serviceType = t;
    }

    //
    // give public access to set() for testing purposes
    //

    public void set(Object instance, String ... path) {

        super.set(instance, path);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
