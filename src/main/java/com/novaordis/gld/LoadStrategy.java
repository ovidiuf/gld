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

package com.novaordis.gld;

import java.util.List;

/**
 * Implementations *must* provide a no-argument constructor.
 */
public interface LoadStrategy
{
    String getName();

    /**
     * A strategy must be generally configured before the first use. If the strategy requires configuration and it
     * was not properly configured, the first next() invocation will throw IllegalStateException;
     *
     * @param arguments - command line arguments. Relevant arguments will be used and removed from the list. null is
     *                  fine, will be ignored/
     *
     * @exception IllegalArgumentException on null configuration.
     *
     */
    void configure(Configuration configuration, List<String> arguments, int from) throws Exception;

    /**
     * @return the next operation to be sent into the cache, factoring in the last operation sent into the cache, or
     *         null if there are no more operations.
     *
     * @param last may be null (which has the semantics "no operation was previously sent into the cache"
     *
     * @param lastWrittenKey  last successfully written key - the method should be prepared for the situation the key
     *        is null.
     *
     * @exception java.lang.IllegalStateException if the strategy was not properly configured before use
     */
    Operation next(Operation last, String lastWrittenKey) throws Exception;

    /**
     * @return the key store used by this load strategy. May return null.
     */
    KeyStore getKeyStore();
}
