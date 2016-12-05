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

import io.novaordis.gld.api.LoadDriverConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class LoadDriverConfigurationImpl implements LoadDriverConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int threadCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param map the map extracted from the YAML file from under the "load" section.
     */
    public LoadDriverConfigurationImpl(Map map) throws Exception {

        load(map);
    }

    // LoadDriverConfiguration implementation --------------------------------------------------------------------------

    @Override
    public int getThreadCount() {

        return threadCount;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void load(Map map) throws Exception {

        Object o = map.get(LoadDriverConfiguration.THREAD_COUNT_LABEL);

        if (o == null) {

            threadCount = LoadDriverConfiguration.DEFAULT_THREAD_COUNT;
        }
        else {

            if (!(o instanceof Integer)) {
                throw new UserErrorException(
                        "'" + LoadDriverConfiguration.THREAD_COUNT_LABEL + "' not an integer: \"" + o + "\"");
            }

            threadCount = ((Integer)o);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
