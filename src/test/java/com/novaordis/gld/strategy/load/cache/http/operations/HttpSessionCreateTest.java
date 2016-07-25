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

import com.novaordis.gld.service.cache.infinispan.MockRemoteCache;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulationException;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionCreateTest extends HttpSessionOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HttpSessionCreateTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void create_SessionIdAlreadyExists() throws Exception {

        HttpSessionCreate c = getOperationToTest(new HttpSessionSimulation("blah"));

        MockRemoteCache mrc = new MockRemoteCache();

        mrc.put(c.getSessionId(), new Object());

        try {

            //noinspection unchecked
            c.performInternal(mrc);
        }
        catch(HttpSessionSimulationException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("session with ID \"blah\" already found in cache"));
        }
    }

    @Test
    public void create() throws Exception {

        String sessionId = "m3gys5";

        HttpSessionCreate c = getOperationToTest(new HttpSessionSimulation(sessionId));

        MockRemoteCache mrc = new MockRemoteCache();

        Object o = mrc.get(sessionId);
        assertNull(o);

        //noinspection unchecked
        c.performInternal(mrc);

        //
        // the "remote cache" must contain the session representation
        //

        Object o2 = mrc.get(sessionId);
        assertNotNull(o2);
    }

    @Test
    public void create_initialSessionSize() throws Exception {

        String sessionId = "m3gys6";

        HttpSessionCreate c = getOperationToTest(new HttpSessionSimulation(sessionId));

        MockRemoteCache mrc = new MockRemoteCache();

        Object o = mrc.get(sessionId);
        assertNull(o);

        //noinspection unchecked
        c.performInternal(mrc);

        //
        // the "remote cache" must contain the session representation
        //

        Object o2 = mrc.get(sessionId);
        assertNotNull(o2);

        //
        // initial session
        //

        Map m = (Map)o2;
        byte[] initialFootprint = (byte[])m.get("INITIAL-FOOTPRINT");
        assertEquals(HttpSessionSimulation.DEFAULT_SESSION_SIZE_BYTES, initialFootprint.length);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HttpSessionCreate getOperationToTest(HttpSessionSimulation s) throws Exception {

        return new HttpSessionCreate(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
