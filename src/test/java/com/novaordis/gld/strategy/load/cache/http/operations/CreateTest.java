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

import com.novaordis.gld.service.cache.infinispan.MockInfinispanService;
import com.novaordis.gld.service.cache.infinispan.MockRemoteCache;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulationException;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class CreateTest extends HttpSessionOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CreateTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void create_SessionIdAlreadyExists() throws Exception {

        Create c = getOperationToTest("blah");

        MockInfinispanService mis = new MockInfinispanService();
        MockRemoteCache mrc = new MockRemoteCache();
        mis.setMockCache(mrc);

        mrc.put(c.getSessionId(), new Object());

        try {

            c.performInternal(mis);
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

        Create c = getOperationToTest(sessionId);

        MockInfinispanService mis = new MockInfinispanService();
        MockRemoteCache mrc = new MockRemoteCache();
        mis.setMockCache(mrc);

        Object o = mrc.get(sessionId);
        assertNull(o);

        c.performInternal(mis);

        //
        // the "remote cache" must contain the session representation
        //

        Object o2 = mrc.get(sessionId);
        assertNotNull(o2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Create getOperationToTest(String sessionId) throws Exception {

        return new Create(sessionId);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
