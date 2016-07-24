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

import com.novaordis.gld.strategy.load.cache.http.DistributableSessionMetadataSimulation;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulationException;
import org.infinispan.client.hotrod.RemoteCache;

import java.util.HashMap;
import java.util.Map;

/**
 * The GLD operation that simulates a HTTP session creation.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionCreate extends HttpSessionOperation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionCreate(HttpSessionSimulation httpSession) {
        super(httpSession);
    }

    // HttpSessionOperation overrides ----------------------------------------------------------------------------------

    /**
     * This method simulates HTTP session creation.
     */
    @Override
    public void performInternal(RemoteCache<String, Object> cache) throws Exception {

        //
        // make a read for our session ID - if content with the given session ID exists, throw an exception, because
        // this is supposed to be a new session
        //

        String ourSessionId = getSessionId();

        Object value = cache.get(ourSessionId);

        if (value != null) {
            throw new HttpSessionSimulationException(
                    "session with ID \"" + ourSessionId + "\" already found in cache: " + value);
        }

        Map<Object, Object> sessionValue = new HashMap<>();
        sessionValue.put((byte)0, 0); // Integer
        sessionValue.put((byte)1, 0L); // Long
        sessionValue.put((byte)3, new DistributableSessionMetadataSimulation());

        cache.put(ourSessionId, sessionValue);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "CREATE:" + getSessionId();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
