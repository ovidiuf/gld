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

import java.util.HashSet;
import java.util.Set;

/**
 * An in-memory, thread-safe Set. Does not have memory protection, so it can potentially cause OutOfMemoryError.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class InMemoryStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String STORY_TYPE_LABEL = "in-memory";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private final Set store;

    // Constructors ----------------------------------------------------------------------------------------------------

    public InMemoryStore() {

        this.store = new HashSet<>();
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws KeyStoreException {
        throw new RuntimeException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() throws KeyStoreException {
        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {
        throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public void store(String key, byte[] ... value) throws KeyStoreException {

        synchronized (store) {
            throw new RuntimeException("store() NOT YET IMPLEMENTED");
        }
    }

    @Override
    public Value retrieve(String key) throws KeyStoreException {
        throw new RuntimeException("retrieve() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws KeyStoreException {
        throw new RuntimeException("getKeys() NOT YET IMPLEMENTED");
    }

    @Override
    public long getKeyCount() {

        synchronized (store) {

            return store.size();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
