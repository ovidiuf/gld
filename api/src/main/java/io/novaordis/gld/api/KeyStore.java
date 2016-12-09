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

/**
 * A key store - a repository to store keys that were sent into the service, in case we want to retrieve them later.
 *
 * Implementations must be thread-safe.
 */
public interface KeyStore {

    // lifecycle -------------------------------------------------------------------------------------------------------

    void start() throws Exception;

    void stop() throws Exception;

    boolean isStarted();

    /**
     * Implementations are encouraged to store asynchronously, if at all possible, in order to interfere with the
     * writing thread as little as possible.
     *
     * @exception java.lang.IllegalStateException if we're a read-only key store.
     */
    void store(String key) throws Exception;

    long getKeyCount();

}
