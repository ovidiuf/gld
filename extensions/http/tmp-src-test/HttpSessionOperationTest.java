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

import com.novaordis.gld.MockService;
import io.novaordis.gld.api.Service;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public abstract class HttpSessionOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HttpSessionOperationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void httpSession() throws Exception {

        HttpSessionSimulation s = new HttpSessionSimulation("blah");
        HttpSessionOperation o = getOperationToTest(s);
        assertEquals(s, o.getHttpSession());
    }


    @Test
    public void sessionId() throws Exception {

        HttpSessionOperation o = getOperationToTest(new HttpSessionSimulation("blah"));
        assertEquals("blah", o.getSessionId());
    }

    @Test
    public void perform_NotAnInfinispanService() throws Exception {

        HttpSessionOperation o = getOperationToTest(new HttpSessionSimulation("blah"));

        Service s = new MockService();

        try {
            o.perform(s);
            fail("should throw Exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract HttpSessionOperation getOperationToTest(HttpSessionSimulation session) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
