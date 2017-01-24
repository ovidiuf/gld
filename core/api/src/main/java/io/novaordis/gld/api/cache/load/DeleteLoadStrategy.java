/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Delete;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.provider.SetKeyProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A load strategy that generate delete operations based on keys read from an external storage.
 */
public class DeleteLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    /**
     * Just one key to limit the damage in case this load strategy is used improperly.
     */
    public static final int DEFAULT_KEY_COUNT = 1;

    public static final String NAME = "delete";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int keyCount;
    private volatile boolean initialized;

    // Constructors ----------------------------------------------------------------------------------------------------

    public DeleteLoadStrategy(int keyCount) {

        super();
        this.keyCount = keyCount;
    }

    public DeleteLoadStrategy() {

        this(DEFAULT_KEY_COUNT);
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return NAME;
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        throw new RuntimeException("getOperationTypes() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getKeyCount()
    {
        return keyCount;
    }

    @Override
    public String toString()
    {
        return getName() + " (count=" + keyCount + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected void init(ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc)
            throws Exception {

        if (!(sc instanceof CacheServiceConfiguration)) {

            throw new IllegalArgumentException(sc + " not a CacheServiceConfiguration");
        }

        Set<String> keysToDelete = new HashSet<>();

//        CacheServiceConfiguration cc = (CacheServiceConfiguration)sc;
//
//        //
//        // TODO this is a potentially costly operation
//        //
//
//        CacheService cacheService = ...
//
//        Set<String> keys = cacheService.keys(null);
//
//        Set<String> keysToDelete = new HashSet<>();
//        int counter = 0;
//
//        for(String k: keys) {
//
//            keysToDelete.add(k);
//
//            if (++counter == keyCount) {
//                break;
//            }
//        }

        KeyProvider keyProvider = new SetKeyProvider(keysToDelete);

        //
        // install the provider ...
        //
        setKeyProvider(keyProvider);

        //
        // ... and start it
        //

        keyProvider.start();

        initialized = true;
    }

    @Override
    protected Operation nextInternal(Operation last, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception {

        if (!initialized) {

            throw new IllegalStateException(this + " was not initialized");
        }

        KeyProvider kp = getKeyProvider();

        if (kp == null) {

            throw new IllegalStateException(this + " not configured, missing key provider");
        }

        String key = kp.next();

        if (key == null) {

            return null;
        }

        return new Delete(key);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
