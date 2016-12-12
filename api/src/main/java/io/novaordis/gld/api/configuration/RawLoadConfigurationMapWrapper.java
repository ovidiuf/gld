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

import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
@Deprecated
public class RawLoadConfigurationMapWrapper implements LoadConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // the actual raw configuration map passed at construction
    private Map<String, Object> rawConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param map the map extracted from the YAML file from under the "load" section.
     */
    public RawLoadConfigurationMapWrapper(Map<String, Object> map) throws Exception {

        this.rawConfiguration = map;
    }

    // LoadConfiguration implementation --------------------------------------------------------------------------

    @Override
    public int getThreadCount() throws UserErrorException {

        Object o = rawConfiguration.get(LoadConfiguration.THREAD_COUNT_LABEL);

        if (o == null) {

            return LoadConfiguration.DEFAULT_THREAD_COUNT;
        }
        else {

            if (!(o instanceof Integer)) {
                throw new UserErrorException(
                        "'" + LoadConfiguration.THREAD_COUNT_LABEL + "' not an integer: \"" + o + "\"");
            }

            return ((Integer)o);
        }
    }

    @Override
    public Long getOperations() throws UserErrorException {

        String label = LoadConfiguration.OPERATION_COUNT_LABEL;
        Object o = rawConfiguration.get(label);

        if (o == null) {

            label = LoadConfiguration.REQUEST_COUNT_LABEL;
            o = rawConfiguration.get(label);

            if (o == null) {

                label = LoadConfiguration.MESSAGE_COUNT_LABEL;
                o = rawConfiguration.get(label);

                if (o == null) {

                    // default value is "unlimited"
                    return null;
                }
            }
        }

        if (!(o instanceof Integer)) {

            throw new UserErrorException("'" + label + "' not a long: \"" + o + "\"");
        }

        return (long)(Integer)o;
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
