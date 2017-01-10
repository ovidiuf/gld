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

package io.novaordis.gld.api.cache;

import io.novaordis.gld.api.MockService;
import io.novaordis.gld.api.service.ServiceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class MockCacheService extends MockService implements CacheService {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> entries;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockCacheService() {

        this.entries = new HashMap<>();
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return ServiceType.cache;
    }

    @Override
    public String get(String key) throws Exception {

        return entries.get(key);
    }

    @Override
    public void put(String key, String value) throws Exception {

        entries.put(key, value);
    }

    @Override
    public void remove(String key) throws Exception {

        entries.remove(key);
    }

    @Override
    public Set<String> keys() throws Exception {

        return entries.keySet();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
