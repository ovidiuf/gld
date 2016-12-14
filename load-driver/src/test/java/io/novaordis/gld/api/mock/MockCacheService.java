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

package io.novaordis.gld.api.mock;

import io.novaordis.gld.api.cache.CacheService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class MockCacheService extends MockService implements CacheService {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> storage;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockCacheService() {

        this.storage = new ConcurrentHashMap<>();
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public String get(String key) throws Exception {

        return storage.get(key);
    }

    @Override
    public void put(String key, String value) throws Exception {

        storage.put(key, value);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Map<String, String> getStorage() {

        return storage;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
