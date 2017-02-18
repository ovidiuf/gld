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
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class ServiceConfigurationImpl extends LowLevelConfigurationBase
        implements ServiceConfiguration, LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------


    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param rawMap the raw map as extracted from the YAML file from the section corresponding to this type of
     *            configuration.
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     */
    public ServiceConfigurationImpl(Map<String, Object> rawMap, File configurationDirectory) throws Exception {

        super(rawMap, configurationDirectory);
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() throws UserErrorException {

        String s;

        try {

            s = get(String.class, ServiceConfiguration.TYPE_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException("'" + ServiceConfiguration.TYPE_LABEL + "' not a string", e);
        }

        if (s == null) {

            throw new UserErrorException("missing service type");
        }

        try {

            return ServiceType.valueOf(s);
        }
        catch(Exception e) {

            throw new UserErrorException("unknown service type '" + s + "'", e);
        }
    }

    @Override
    public ImplementationConfiguration getImplementationConfiguration() throws UserErrorException {

        Map<String, Object> m;

        try {

            m = get(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException(
                    "'" + ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL + "' not a map", e);
        }

        if (m.isEmpty()) {

            throw new UserErrorException("missing implementation configuration");
        }

        return new ImplementationConfigurationImpl(m, getConfigurationDirectory());
    }

    @Override
    public String getLoadStrategyName() throws UserErrorException {

        Map<String, Object> loadStrategyConfig;

        try {

            //noinspection unchecked
            loadStrategyConfig = get(Map.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException(
                    "'" + ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL + "' not a map", e);
        }

        if (loadStrategyConfig == null) {

            throw new UserErrorException("missing load strategy configuration");
        }

        Object o = loadStrategyConfig.get(LoadStrategy.NAME_LABEL);

        if (o == null) {

            throw new UserErrorException("missing load strategy name");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the load strategy name should be a string, but it is a(n) " + o.getClass().getSimpleName());
        }

        return (String)o;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
