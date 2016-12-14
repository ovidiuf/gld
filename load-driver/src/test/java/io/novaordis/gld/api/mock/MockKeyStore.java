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

package io.novaordis.gld.api.mock;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.store.KeyStoreException;
import io.novaordis.gld.api.store.StoredValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class MockKeyStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockKeyStore.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws KeyStoreException {

        this.started = true;

        log.info(this + " started");
    }

    @Override
    public void stop() throws KeyStoreException {
        this.started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void store(String key, byte[]... value) throws IllegalArgumentException, KeyStoreException {
        throw new RuntimeException("store() NOT YET IMPLEMENTED");
    }

    @Override
    public StoredValue retrieve(String key) throws KeyStoreException {
        throw new RuntimeException("retrieve() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws KeyStoreException {
        throw new RuntimeException("getKeys() NOT YET IMPLEMENTED");
    }

    @Override
    public long getKeyCount() {
        throw new RuntimeException("getKeyCount() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "MockKeyStore[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
