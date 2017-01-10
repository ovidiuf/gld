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

package io.novaordis.gld.api.cache.embedded;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simple cache service collocated with the load driver.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class EmbeddedCacheService extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EmbeddedCacheService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ConcurrentHashMap<String, String> cache;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Required for no-argument reflection instantiation.
     */
    @SuppressWarnings("unused")
    public EmbeddedCacheService() {
        this.cache = new ConcurrentHashMap<>();
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        log.info(this + " was mock configured");
    }

    @Override
    public void start() throws Exception {

        super.start();
    }

    @Override
    public void stop() {

        if (!isStarted()) {

            return;
        }

        super.stop();

        cache.clear();
    }

    @Override
    public ServiceType getType() {

        return ServiceType.cache;
    }

    // execution -------------------------------------------------------------------------------------------------------

    @Override
    public String get(String key) throws Exception {

        return cache.get(key);
    }

    @Override
    public void put(String key, String value) throws Exception {

        cache.put(key, value);
    }

    @Override
    public void remove(String key) throws Exception {

        cache.remove(key);
    }

    @Override
    public Set<String> keys() throws Exception {

        return cache.keySet();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
