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

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/10/16
 */
public class MockKeyStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws KeyStoreException {

        started = true;
    }

    @Override
    public void stop() throws KeyStoreException {

        started = false;
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
    public long getKeyCount() throws KeyStoreException {
        throw new RuntimeException("getKeyCount() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "api MockKeyStore";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
