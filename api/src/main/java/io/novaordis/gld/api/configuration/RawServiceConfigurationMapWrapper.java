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
import io.novaordis.gld.api.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.util.Collections;
import java.util.Map;

/**
 * Wraps around the raw map and provides typed access to it.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class RawServiceConfigurationMapWrapper implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // the actual raw configuration map passed at construction
    private Map<String, Object> rawConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param map the map extracted from the YAML file from under the "service" section.
     */
    public RawServiceConfigurationMapWrapper(Map<String, Object> map) {

        this.rawConfiguration = map;
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() throws UserErrorException {

        Object o = rawConfiguration.get(ServiceConfiguration.TYPE_LABEL);

        if (o == null) {

            throw new UserErrorException("missing service type");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the service type should be a string, but it is a " + o.getClass().getSimpleName());
        }

        try {

            return ServiceType.valueOf((String) o);
        }
        catch(Exception e) {

            throw new UserErrorException("unknown service type '" + o + "'", e);
        }
    }

    @Override
    public String getImplementation() throws UserErrorException {

        Object o = rawConfiguration.get(ServiceConfiguration.IMPLEMENTATION_LABEL);

        if (o == null) {

            throw new UserErrorException("missing implementation");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the implementation should be a string, but it is a(n) " + o.getClass().getSimpleName());
        }

        return (String)o;
    }

    @Override
    public String getLoadStrategyName() throws UserErrorException {

        Object o = rawConfiguration.get(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        if (o == null) {

            throw new UserErrorException("missing load strategy configuration");
        }

        if (!(o instanceof Map)) {
            throw new UserErrorException(
                    "the load strategy configuration should be a map, but it is a(n) " + o.getClass().getSimpleName());
        }

        //noinspection unchecked
        Map<String, Object> loadStrategyConfig = (Map<String, Object>)o;

        o = loadStrategyConfig.get(LOAD_STRATEGY_NAME_LABEL);

        if (o == null) {

            throw new UserErrorException("missing load strategy name");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the load strategy name should be a string, but it is a(n) " + o.getClass().getSimpleName());
        }

        return (String)o;
    }

    @Override
    public Map<String, Object> getMap(String... path) {

        Map<String, Object> crtMap = rawConfiguration;

        for(String pathElement: path) {

            Object o = crtMap.get(pathElement);

            if (o == null) {
                return Collections.emptyMap();
            }

            if (o instanceof Map) {

                //noinspection unchecked
                crtMap = (Map<String, Object>)o;
                continue;
            }

            throw new RuntimeException("NOT YET IMPLEMENTED: don't know how to handle this");
        }

        return crtMap;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected Map<String, Object> getRawConfigurationMap() {

        return rawConfiguration;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
