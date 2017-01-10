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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class MockLoadStrategy implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockLoadStrategy.class);

    // Static ----------------------------------------------------------------------------------------------------------

    private static String classLevelRequiredConfigurationElementName;

    /**
     * Resets static configuration.
     */
    public static void reset() {

        classLevelRequiredConfigurationElementName = null;
    }

    public static void setRequiredConfigurationElement(String elementName) {

        classLevelRequiredConfigurationElementName = elementName;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Service service;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    @Override
    public Service getService() {

        return service;
    }

    @Override
    public void setService(Service s) throws IllegalArgumentException {

        //
        // we don't perform any validation, we're a mock. The real implementations should, though ...
        //
        this.service = s;
    }

    @Override
    public String getName() {

        return "mock";
    }

    @Override
    public void init(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        //
        // by default, we don't require any specific configuration
        //

        if (classLevelRequiredConfigurationElementName != null) {

            //
            // there is a required configuration element name, attempt to read it from the configuration
            //

            CacheServiceConfiguration csc = (CacheServiceConfiguration)sc;

            Map<String, Object> rawConfig = csc.get("load-strategy");

            String requiredValue = (String)rawConfig.get(classLevelRequiredConfigurationElementName);

            if (requiredValue == null) {

                throw new UserErrorException(
                        "required configuration element '" + classLevelRequiredConfigurationElementName + "' not found");
            }
        }

        log.info(this + ".init()");
    }

    @Override
    public void start() throws Exception {
        throw new RuntimeException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {
        throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() {
        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {
        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {
        throw new RuntimeException("getOperationTypes() NOT YET IMPLEMENTED");
    }

    @Override
    public KeyProvider getKeyProvider() {
        throw new RuntimeException("getKeyProvider() NOT YET IMPLEMENTED");
    }

    @Override
    public void setKeyProvider(KeyProvider keyProvider) {
        throw new RuntimeException("setKeyProvider() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "api cache MockLoadStrategy";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
