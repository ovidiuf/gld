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

package io.novaordis.gld.api;

import io.novaordis.gld.api.store.KeyStoreException;
import io.novaordis.gld.api.store.Value;

import java.util.Set;

/**
 * A key store is a repository to store keys (and optionally, their associated values) that were sent into the service,
 * in case we want to retrieve them later.
 *
 * Implementations must be thread-safe.
 */
public interface KeyStore {

    // lifecycle -------------------------------------------------------------------------------------------------------

    void start() throws KeyStoreException;

    void stop() throws KeyStoreException;

    boolean isStarted();

    /**
     * Must be thread-save. Implementations are encouraged to store asynchronously, if at all possible, in order to
     * interfere with the writing thread as little as possible.
     *
     * @param value is optional, if not provided, the store will retain the information that the value was not provided
     *              (and not that it was null). null values are also stored, and they are semantically different from
     *              "not provided". No more than one byte[] array should be provided, if more than one is provided,
     *              the method will throw IllegalArgumentException.
     *
     * @exception IllegalArgumentException if more than one value is provided.
     */
    void store(String key, byte[] ... value) throws IllegalArgumentException, KeyStoreException;

    Value retrieve(String key) throws KeyStoreException;

    /**
     * This operation is potentially expensive (in processing time and memory), if the underlying storage contains
     * a large number of keys and/or it is distributed.
     */
    Set<String> getKeys() throws KeyStoreException;

    long getKeyCount();

}
