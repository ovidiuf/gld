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

package com.novaordis.gld.strategy.load.cache.http;

import com.novaordis.gld.strategy.load.cache.http.operations.Create;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionSimulationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HttpSessionSimulationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {

        HttpSessionSimulation.destroyInstance();
    }

    // threadLocal lifecycle -------------------------------------------------------------------------------------------

    @Test
    public void threadLocalLifecycle() throws Exception {

        HttpSessionSimulation s = HttpSessionSimulation.getCurrentInstance();
        assertNull(s);

        HttpSessionSimulation s2 = HttpSessionSimulation.initializeInstance();
        assertNotNull(s2);

        HttpSessionSimulation s3 = HttpSessionSimulation.getCurrentInstance();
        assertEquals(s2, s3);

        HttpSessionSimulation s4 = HttpSessionSimulation.destroyInstance();
        assertEquals(s3, s4);

        HttpSessionSimulation s5 = HttpSessionSimulation.getCurrentInstance();
        assertNull(s5);
    }

    @Test
    public void initializeInstance_AlreadyInitialized() throws Exception {

        HttpSessionSimulation s = HttpSessionSimulation.initializeInstance();
        assertNotNull(s);

        try {

            HttpSessionSimulation.initializeInstance();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            log.info(e.getMessage());
        }

        HttpSessionSimulation.destroyInstance();
    }

    // generateSessionId() ---------------------------------------------------------------------------------------------

    @Test
    public void generateSessionId() throws Exception {

        Random r = new Random();

        String s = HttpSessionSimulation.generateSessionId(r);

        log.info(s);

        assertNotNull(s);

        assertEquals(((HttpSessionSimulation.DEFAULT_SESSION_ID_LENGTH + 2) / 3) * 4, s.length());
    }

    // instance lifecycle ----------------------------------------------------------------------------------------------

    @Test
    public void firstOperationIsCreate() throws Exception {

        HttpSessionSimulation s = new HttpSessionSimulation();

        HttpSessionOperation o = s.next();

        assertTrue(o instanceof Create);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
