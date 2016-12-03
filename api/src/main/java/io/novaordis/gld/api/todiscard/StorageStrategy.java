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

package io.novaordis.gld.api.todiscard;

import io.novaordis.utilities.UserErrorException;

import java.util.List;
import java.util.Set;

/**
 * Implementations *must* provide a no-argument constructor.
 */
public interface StorageStrategy {

    /**
     * Allows the strategy instance to pull and remove configuration arguments that pertains to it from an argument
     * list leaving the uninteresting arguments in the list for further processing by the upper level.
     *
     * @param arguments - the method removes the interesting arguments from the list, and does not touch the rest. Must
     *        not be a null argument, otherwise IllegalArgumentException will be thrown.
     *
     * @param from - position in list where to start looking. If negative, the method will throw an
     *        ArrayIndexOutOfBoundsException
     *
     * @throws UserErrorException on configuration errors.
     */
    void configure(Configuration configuration, List<String> arguments, int from) throws Exception;
    boolean isConfigured();

    /**
     * @throws IllegalArgumentException if not configured prior to starting.
     */
    void start() throws Exception;
    void stop() throws Exception;
    boolean isStarted();

    /**
     * @throws IllegalStateException if the strategy does not allow writing.
     *
     * @see StorageStrategy#isWrite()
     */
    void store(String key, String value) throws Exception;

    /**
     * @throws IllegalStateException if the strategy does not allow reading.
     *
     * @see StorageStrategy#isRead()
     *
     * @return null if the key is not in store.
     */
    String retrieve(String key) throws Exception;

    Set<String> getKeys() throws Exception;

    /**
     * @return whether the storage strategy allows reading. If the storage does not allow reading, retrieve() will
     *         throw an IllegalStateException.
     */
    boolean isRead();

    /**
     * @return whether the storage strategy allows writing. If the storage does not allow writing, store() will
     *         throw an IllegalStateException.
     */
    boolean isWrite();

}