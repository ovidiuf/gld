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

package io.novaordis.gld.api.provider;

import io.novaordis.gld.api.KeyProvider;

import java.util.Iterator;
import java.util.Set;

/**
 * A KeyProvider backed by a memory set.
 */
public class SetKeyProvider implements KeyProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private final Set<String> storage;
    private volatile Iterator<String> iterator;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SetKeyProvider(Set<String> keys) {

        this.storage = keys;
    }

    // KeyProvider implementation --------------------------------------------------------------------------------------

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        if (iterator != null) {

            return;
        }

        iterator = storage.iterator();
    }

    @Override
    public void stop() {

        if (iterator == null) {

            return;
        }

        iterator = null;
    }

    @Override
    public boolean isStarted() {

        return iterator != null;
    }

    @Override
    public Long getRemainingKeyCount() {

        synchronized (storage) {

            return (long)storage.size();
        }
    }

    @Override
    public String next() {

        if (iterator == null) {

            throw new IllegalStateException(this + " not started");
        }

        synchronized (storage) {

            if (iterator == null) {

                iterator = storage.iterator();
            }

            if (!iterator.hasNext()) {

                return null;
            }


            String key = iterator.next();
            iterator.remove();
            return key;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
