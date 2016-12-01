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

package com.novaordis.gld.service.cache.infinispan;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class MockRemoteCacheManager extends RemoteCacheManager {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, RemoteCache> caches;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockRemoteCacheManager() {

        this.caches = new HashMap<>();
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    public <K, V> RemoteCache<K, V> getCache(String cacheName) {

        //noinspection unchecked
        return caches.get(cacheName);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setCache(String name, RemoteCache cache) {
        caches.put(name, cache);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
