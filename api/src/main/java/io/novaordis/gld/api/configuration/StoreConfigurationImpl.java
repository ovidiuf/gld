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

import java.io.File;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class StoreConfigurationImpl implements StoreConfiguration, LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // the actual raw configuration map passed at construction
    private Map<String, Object> rawConfiguration;

    private LowLevelConfigurationAccess configurationAccess;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param map the map extracted from the YAML file from under the "load" section.
     *
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     */
    public StoreConfigurationImpl(Map<String, Object> map, File configurationDirectory) throws Exception {

        this.rawConfiguration = map;
        this.configurationAccess = new LowLevelConfigurationAccess(rawConfiguration, configurationDirectory);
    }

    // LowLevelConfiguration implementation ----------------------------------------------------------------------------

    @Override
    public <T> T get(Class<? extends T> type, String... path) {

        //
        // we delegate to a generic low-level configuration resolver
        //

        return configurationAccess.get(type, path);
    }

    @Override
    public File getFile(String... path) {

        //
        // we delegate to a generic low-level configuration resolver
        //

        return configurationAccess.getFile(path);
    }

    // StoreConfiguration implementation -------------------------------------------------------------------------------

    @Override
    public String getStoreType() throws UserErrorException {

        Object o = rawConfiguration.get(StoreConfiguration.STORE_TYPE_LABEL);

        if (o == null) {

            throw new UserErrorException("missing store type");
        }
        else {

            if (!(o instanceof String)) {
                throw new UserErrorException(
                        "'" + StoreConfiguration.STORE_TYPE_LABEL + "' not a string: \"" + o + "\"");
            }

            return ((String)o);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
