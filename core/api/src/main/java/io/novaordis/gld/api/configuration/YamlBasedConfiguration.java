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
import io.novaordis.gld.api.cache.CacheServiceConfigurationImpl;
import io.novaordis.gld.api.jms.JmsServiceConfigurationImpl;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class YamlBasedConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String SERVICE_SECTION_LABEL = "service";
    public static final String LOAD_SECTION_LABEL = "load";
    public static final String STORE_SECTION_LABEL = "store";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private File file;
    private File configurationDirectory;
    private ServiceConfiguration serviceConfiguration;
    private LoadConfiguration loadConfiguration;
    private StoreConfiguration storeConfiguration;
    private OutputConfiguration outputConfiguration;

    // Constructors ----------------------------------------------------------------------------------------------------

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

        throw new NotYetImplementedException("getOutputConfiguration() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Loads the configuration from the specified file.
     */
    public void load(File f) throws Exception {

        this.file = f;
        this.configurationDirectory = f.getParentFile();
        if (configurationDirectory == null) {
            configurationDirectory = new File(".");
        }



        InputStream is = null;

        try {

            is = new BufferedInputStream(new FileInputStream(file));

            Yaml yaml = new Yaml();

            Map topLevelConfigurationMap = (Map)yaml.load(is);

            Map<String, Object> serviceConfigurationMap = null;
            Map<String, Object> loadConfigurationMap = null;
            Map<String, Object> storeConfigurationMap = null;

            if (topLevelConfigurationMap != null) {

                //noinspection unchecked
                serviceConfigurationMap = (Map<String, Object>)topLevelConfigurationMap.get(SERVICE_SECTION_LABEL);
                //noinspection unchecked
                loadConfigurationMap = (Map<String, Object>) topLevelConfigurationMap.get(LOAD_SECTION_LABEL);
                //noinspection unchecked
                storeConfigurationMap = (Map<String, Object>) topLevelConfigurationMap.get(STORE_SECTION_LABEL);
            }

            if (serviceConfigurationMap == null) {

                throw new UserErrorException(
                        "'" + SERVICE_SECTION_LABEL + "' section empty or missing from configuration file " + file);
            }

            String serviceType = (String)serviceConfigurationMap.get(ServiceConfiguration.TYPE_LABEL);

            if (ServiceType.cache.name().equals(serviceType)) {

                serviceConfiguration =
                        new CacheServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
            }
            else if (ServiceType.jms.name().equals(serviceType)) {

                serviceConfiguration =
                        new JmsServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
            }
            else if (ServiceType.http.name().equals(serviceType)) {

                serviceConfiguration =
                        new JmsServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
            }
            else {

                throw new UserErrorException(
                        "unknown service type '" + serviceType + "' in configuration file " + file);
            }

            if (loadConfigurationMap == null) {

                throw new UserErrorException(
                        "'" + LOAD_SECTION_LABEL + "' section empty or missing from configuration file " + file);
            }

            loadConfiguration = new LoadConfigurationImpl(loadConfigurationMap, configurationDirectory);

            if (storeConfigurationMap != null) {

                storeConfiguration = new StoreConfigurationImpl(storeConfigurationMap, configurationDirectory);
            }
        }
        finally {

            if (is != null) {

                is.close();
            }
        }
    }

    @Override
    public String toString() {

        return file == null ? "null" : file.getAbsolutePath();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Never return null.
     */
    File getConfigurationDirectory() {

        return configurationDirectory;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
