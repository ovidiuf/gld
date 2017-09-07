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

import io.novaordis.gld.api.http.HttpServiceConfigurationImpl;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfigurationImpl;
import io.novaordis.gld.api.jms.JMSServiceConfigurationImpl;
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
    public static final String OUTPUT_SECTION_LABEL = "output";

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

        return outputConfiguration;
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

            Object content = yaml.load(is);

            if (content == null) {

                throw new UserErrorException("empty configuration file " + f);
            }
            else if (!(content instanceof Map)) {

                throw new UserErrorException("invalid configuration file " + f);
            }

            Map topLevelConfigurationMap = (Map)content;

            Map<String, Object> serviceConfigurationMap = null;
            Map<String, Object> loadConfigurationMap = null;
            Map<String, Object> storeConfigurationMap = null;
            Map<String, Object> outputConfigurationMap = null;

            //noinspection ConstantConditions
            if (topLevelConfigurationMap != null) {

                //noinspection unchecked
                serviceConfigurationMap = (Map<String, Object>)topLevelConfigurationMap.get(SERVICE_SECTION_LABEL);

                //noinspection unchecked
                loadConfigurationMap = (Map<String, Object>)topLevelConfigurationMap.get(LOAD_SECTION_LABEL);

                //noinspection unchecked
                storeConfigurationMap = (Map<String, Object>)topLevelConfigurationMap.get(STORE_SECTION_LABEL);

                //noinspection unchecked
                outputConfigurationMap = (Map<String, Object>)topLevelConfigurationMap.get(OUTPUT_SECTION_LABEL);
            }

            if (serviceConfigurationMap == null) {

                throw new UserErrorException(
                        "'" + SERVICE_SECTION_LABEL + "' section empty or missing from configuration file " + file);
            }

            serviceConfiguration = buildServiceConfiguration(serviceConfigurationMap, configurationDirectory);

            ServiceType st = serviceConfiguration.getType();

            loadConfiguration = new LoadConfigurationImpl(st, loadConfigurationMap, configurationDirectory);
            storeConfiguration = new StoreConfigurationImpl(storeConfigurationMap, configurationDirectory);
            outputConfiguration = new OutputConfigurationImpl(outputConfigurationMap, configurationDirectory);

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

    private static ServiceType extractServiceType(Map<String, Object> serviceConfigurationMap)
            throws UserErrorException {

        String sts = (String)serviceConfigurationMap.get(ServiceConfiguration.TYPE_LABEL);

        if (sts == null) {

            throw new UserErrorException("required 'type' missing from the service section");
        }

        try {

            return ServiceType.fromString(sts);
        }
        catch(IllegalArgumentException e) {

            throw new UserErrorException(e.getMessage());
        }
    }

    private static ServiceConfiguration buildServiceConfiguration(Map<String, Object> serviceConfigurationMap,
                                                                  File configurationDirectory) throws Exception {

        ServiceType st = extractServiceType(serviceConfigurationMap);

        if (ServiceType.cache.equals(st)) {

            return new CacheServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
        }
        else if (ServiceType.jms.equals(st)) {

            return new JMSServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
        }
        else if (ServiceType.http.equals(st)) {

            return new HttpServiceConfigurationImpl(serviceConfigurationMap, configurationDirectory);
        }
        else {

            throw new RuntimeException(st + " service type NOT YET SUPPORTED");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
