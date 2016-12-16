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

import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.cache.CacheService;
import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.utilities.UserErrorException;

import java.util.List;
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
    public void setConfiguration(Configuration c) {
        throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes) {
        throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException {
        throw new RuntimeException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public ContentType getContentType() {
        throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
    }

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
