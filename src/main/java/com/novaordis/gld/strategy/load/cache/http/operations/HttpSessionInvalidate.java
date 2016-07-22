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

package com.novaordis.gld.strategy.load.cache.http.operations;

import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import org.infinispan.client.hotrod.RemoteCache;

/**
 * The GLD operation that simulates a HTTP session invalidation.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionInvalidate extends HttpSessionOperation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionInvalidate(HttpSessionSimulation httpSession) {
        super(httpSession);
    }

    // HttpSessionOperation overrides ----------------------------------------------------------------------------------

    @Override
    public void performInternal(RemoteCache<String, Object> cache) throws Exception {

        String ourSessionId = getSessionId();
        cache.remove(ourSessionId);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
