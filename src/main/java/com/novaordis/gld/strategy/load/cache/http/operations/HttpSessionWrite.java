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

import com.novaordis.gld.strategy.load.cache.http.DistributedSessionMetadataSimulation;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import org.infinispan.client.hotrod.RemoteCache;

import java.util.HashMap;
import java.util.Map;

/**
 * The GLD operation that simulates a HTTP session write.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionWrite extends HttpSessionOperation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionWrite(HttpSessionSimulation httpSession) {
        super(httpSession);
    }

    // HttpSessionOperation overrides ----------------------------------------------------------------------------------

    /**
     * This method simulates a HTTP session write.
     */
    @Override
    public void performInternal(RemoteCache<String, Object> cache) throws Exception {

        String ourSessionId = getSessionId();

        //
        // we don't do a read, we simply write
        //

        Map<Object, Object> sessionValue = new HashMap<>();
        sessionValue.put((byte)0, 0); // Integer
        sessionValue.put((byte)1, 0L); // Long
        sessionValue.put((byte)3, new DistributedSessionMetadataSimulation());
        sessionValue.put("TEST-KEY", "TEST-VALUE");

        cache.put(ourSessionId, sessionValue);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
