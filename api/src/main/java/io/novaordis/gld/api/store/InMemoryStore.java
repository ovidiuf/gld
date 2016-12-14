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

package io.novaordis.gld.api.store;

import io.novaordis.gld.api.KeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A store that stores each key/value pair in memory. It is multi-thread safe.
 *
 * It does not have memory overflow protection.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class InMemoryStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String STORY_TYPE_LABEL = "in-memory";

    private static final Logger log = LoggerFactory.getLogger(InMemoryStore.class);

    private static final Object NOT_STORED = new Object();
    private static final Object NULL = new Object();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    protected volatile boolean started;

    private Map<String, Object> storage;

    // Constructors ----------------------------------------------------------------------------------------------------

    public InMemoryStore() {

        this.storage = new ConcurrentHashMap<>();
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws KeyStoreException {

        if (started) {

            return;
        }

        started = true;

        log.debug(this + " started");
    }

    @Override
    public void stop() throws KeyStoreException {

        if (!started) {

            return;
        }

        storage.clear();

        started = false;

        log.debug(this + " stopped");
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public void store(String key, byte[]... v) throws KeyStoreException {

        Object storedValue;

        if (v == null) {

            storedValue = NULL;
        }
        else if (v.length > 1) {

            throw new IllegalArgumentException("invalid multiple arguments");
        }
        else if (v.length == 1) {

            storedValue = v[0];
        }
        else {

            storedValue = NOT_STORED;
        }

        storage.put(key, storedValue);
    }

    @Override
    public StoredValue retrieve(String key) throws KeyStoreException {

        Object value = storage.get(key);

        if (value == null || value == NULL) {

            return Null.INSTANCE;
        }
        else if (value == NOT_STORED) {

            return NotStored.INSTANCE;
        }
        else if (value instanceof byte[]) {

            return StoredValue.getInstance((byte[])value);
        }
        else {

            throw new IllegalStateException("invalid type found in storage: " + value.getClass());
        }
    }

    @Override
    public Set<String> getKeys() throws KeyStoreException {

        return storage.keySet();
    }

    @Override
    public long getKeyCount() throws KeyStoreException {

        return storage.size();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return STORY_TYPE_LABEL  + " store (" + storage.size() + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
