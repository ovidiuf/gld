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

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class LowLevelConfigurationAccess implements LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, Object> rawConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public LowLevelConfigurationAccess(Map<String, Object> raw) {

        this.rawConfiguration = raw;
    }

    // LowLevelConfiguration implementation ----------------------------------------------------------------------------

    @Override
    public <T> T get(Class<? extends T> type, String... path) {

        String pathAsString = "";

        Map<String, Object> current = rawConfiguration;

        for(int i = 0; i < path.length; i ++) {

            String element = path[i];

            pathAsString = pathAsString.isEmpty() ? element : pathAsString + "." + element;

            Object o = current.get(element);

            if (o == null) {

                return null;
            }

            if (i < path.length - 1) {

                //
                // we expect a map, if we get anything else, it means the path did not match, return null
                //

                if (!(o instanceof Map)) {

                    return null;
                }

                //noinspection unchecked
                current = (Map<String, Object>)o;
                continue;
            }

            //
            // at the end of the path
            //

            if (type.isAssignableFrom(o.getClass())) {

                //noinspection unchecked
                return (T) o;
            }

            throw new IllegalStateException(
                    "expected " + pathAsString + " to be a " + type.getSimpleName() + " but it is a(n) " +
                            o.getClass().getSimpleName());
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
