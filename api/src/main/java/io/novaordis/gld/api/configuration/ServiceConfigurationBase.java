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

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class ServiceConfigurationBase implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ServiceType type;
    private String implementation;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param map the map extracted from the YAML file from under the "service" section.
     */
    public ServiceConfigurationBase(Map map) throws Exception {

        load(map);
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return type;
    }

    @Override
    public String getImplementation() {

        return implementation;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void load(Map map) throws Exception {

        //
        // type
        //

        Object o = map.get(ServiceConfiguration.TYPE_LABEL);

        if (o == null) {

            throw new UserErrorException("missing service type");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the service type should be a string, but it is a " + o.getClass().getSimpleName());
        }

        try {

            type = ServiceType.valueOf((String) o);
        }
        catch(Exception e) {

            throw new UserErrorException("unknown service type '" + o + "'", e);
        }

        //
        // implementation
        //

        o = map.get(ServiceConfiguration.IMPLEMENTATION_LABEL);

        if (o == null) {

            throw new UserErrorException("missing implementation");
        }

        if (!(o instanceof String)) {
            throw new UserErrorException(
                    "the implementation should be a string, but it is a(n) " + o.getClass().getSimpleName());
        }

        implementation = (String)o;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
