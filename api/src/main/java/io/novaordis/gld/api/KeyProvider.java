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

package io.novaordis.gld.api;

/**
 * A key provider. Implementations must be thread-safe as it will accessed concurrently from multiple single-threaded
 * runners and possibly other threads.
 */
public interface KeyProvider {

    // lifecycle -------------------------------------------------------------------------------------------------------

    /**
     * All configuration must be applied to the instance before it starts. If there is a re-configuration attempt after
     * the instance has started, the corresponding method may throw IllegalStateException.
     * <p>
     * The implementations must be idempotent.
     */
    void start() throws Exception;

    /**
     * The implementations must be idempotent.
     */
    void stop() throws Exception;

    boolean isStarted();

    /**
     * @return the next key from the store, or null if there are no keys.
     */
    String next();

    /**
     * @return the number of keys still to be generated. May return null, which means unlimited keys remaining. May also
     * return zero or a negative value, which means no keys remaining.
     */
    Long getRemainingKeyCount();

}