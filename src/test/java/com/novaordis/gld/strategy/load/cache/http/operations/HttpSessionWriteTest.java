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
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionWriteTest extends HttpSessionOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HttpSessionWriteTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void write() throws Exception {

        String sessionId = "n723hf";

        HttpSessionWrite w = getOperationToTest(new HttpSessionSimulation(sessionId));

        MockRemoteCache mrc = new MockRemoteCache();

        Object o = mrc.get(sessionId);
        assertNull(o);

        //noinspection unchecked
        w.performInternal(mrc);

        //
        // the "remote cache" must contain the session representation
        //

        Object o2 = mrc.get(sessionId);
        assertNotNull(o2);

        log.debug(".");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HttpSessionWrite getOperationToTest(HttpSessionSimulation s) throws Exception {

        return new HttpSessionWrite(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
