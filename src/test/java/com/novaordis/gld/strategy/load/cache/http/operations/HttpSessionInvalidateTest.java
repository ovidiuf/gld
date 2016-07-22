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
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionInvalidateTest extends HttpSessionOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void invalidate() throws Exception {

        String sessionId = "5f3tw3";

        HttpSessionInvalidate c = getOperationToTest(new HttpSessionSimulation(sessionId));

        MockRemoteCache mrc = new MockRemoteCache();

        //
        // "populate" the cache in advance
        //

        mrc.put(sessionId, "something");

        Object o = mrc.get(sessionId);
        assertNotNull(o);

        //noinspection unchecked
        c.performInternal(mrc);

        //
        // the "remote cache" must not contain the session representation anymore
        //

        Object o2 = mrc.get(sessionId);
        assertNull(o2);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HttpSessionInvalidate getOperationToTest(HttpSessionSimulation s) throws Exception {

        return new HttpSessionInvalidate(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
