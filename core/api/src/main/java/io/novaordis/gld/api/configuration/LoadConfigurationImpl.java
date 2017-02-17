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

import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class LoadConfigurationImpl extends LowLevelConfigurationBase
        implements LoadConfiguration, LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ServiceType serviceType;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param serviceType the service type.
     *
     * @param rawMap the raw map as extracted from the YAML file from the section corresponding to this type of
     *            configuration. Null is acceptable, and in this case the instance will return default values.
     *
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     */
    public LoadConfigurationImpl(ServiceType serviceType, Map<String, Object> rawMap, File configurationDirectory)
            throws Exception {

        super(rawMap, configurationDirectory);

        if (serviceType == null) {

            throw new IllegalArgumentException("null service type");
        }

        if (rawMap == null) {

            rawMap = new HashMap<>();
            rawMap.put(THREAD_COUNT_LABEL, DEFAULT_THREAD_COUNT);

            set(rawMap);
        }

        this.serviceType = serviceType;
    }

    // LoadConfiguration implementation --------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {

        return serviceType;
    }

    @Override
    public void setServiceType(ServiceType t) {

        this.serviceType = t;
    }

    @Override
    public int getThreadCount() throws UserErrorException {

        Integer i;

        try {

            i = get(Integer.class, LoadConfiguration.THREAD_COUNT_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException("'" + LoadConfiguration.THREAD_COUNT_LABEL + "' not an integer", e);
        }

        if (i == null) {

            return LoadConfiguration.DEFAULT_THREAD_COUNT;
        }

        return i;
    }

    @Override
    public Long getOperations() throws UserErrorException {

        String label = LoadConfiguration.OPERATION_COUNT_LABEL;

        Integer i;

        try {

            i = get(Integer.class, label);

            if (i == null) {

                label = LoadConfiguration.REQUEST_COUNT_LABEL;
                i = get(Integer.class, label);

                if (i == null) {

                    label = LoadConfiguration.MESSAGE_COUNT_LABEL;
                    i = get(Integer.class, label);

                    if (i == null) {

                        // default value is "unlimited"
                        return null;
                    }
                }
            }
        }
        catch(IllegalStateException e) {

            throw new UserErrorException("'" + label + "' not a long", e);
        }

        return (long)i;
    }

    @Override
    public Long getRequests() throws UserErrorException {

        return getOperations();
    }

    @Override
    public Long getMessages() throws UserErrorException {

        return getOperations();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
