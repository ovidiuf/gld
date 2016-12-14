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

import com.novaordis.gld.strategy.load.cache.HttpSessionLoadStrategy;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionPerThreadTest extends HttpSessionSimulationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HttpSessionPerThreadTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {

        HttpSessionPerThread.destroyInstance();
    }

    // threadLocal lifecycle -------------------------------------------------------------------------------------------

    @Test
    public void threadLocalLifecycle() throws Exception {

        HttpSessionPerThread s = HttpSessionPerThread.getCurrentInstance();
        assertNull(s);

        HttpSessionPerThread s2 = HttpSessionPerThread.initializeInstance();
        assertNotNull(s2);

        HttpSessionPerThread s3 = HttpSessionPerThread.getCurrentInstance();
        assertEquals(s2, s3);

        HttpSessionPerThread s4 = HttpSessionPerThread.destroyInstance();
        assertEquals(s3, s4);

        HttpSessionPerThread s5 = HttpSessionPerThread.getCurrentInstance();
        assertNull(s5);
    }

    @Test
    public void initializeInstance_AlreadyInitialized() throws Exception {

        HttpSessionPerThread s = HttpSessionPerThread.initializeInstance();
        assertNotNull(s);

        try {

            HttpSessionPerThread.initializeInstance();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            log.info(e.getMessage());
        }

        HttpSessionPerThread.destroyInstance();
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void next_firstInvocation() throws Exception {

        HttpSessionPerThread s = HttpSessionPerThread.getCurrentInstance();
        assertNull(s);

        HttpSessionLoadStrategy strategy = new HttpSessionLoadStrategy();
        strategy.setWriteCount(77);

        HttpSessionOperation o = HttpSessionPerThread.next(strategy, false);

        assertNotNull(o);
        assertEquals(77, o.getHttpSession().getConfiguredWriteCount());

        HttpSessionPerThread s2 = HttpSessionPerThread.getCurrentInstance();
        assertNotNull(s2);

        assertTrue(o instanceof HttpSessionCreate);
    }

    @Test
    public void next_RuntimeShuttingDown_SessionAssociatedWithThread() throws Exception {

        HttpSessionPerThread s = HttpSessionPerThread.initializeInstance();
        assertNotNull(s);

        String sessionId = s.getSessionId();

        //
        // should generate an "invalidate" operation
        //
        HttpSessionInvalidate i = (HttpSessionInvalidate)HttpSessionPerThread.next(null, true);

        assertNotNull(i);
        assertEquals(sessionId, i.getSessionId());

        assertNull(HttpSessionPerThread.getCurrentInstance());
    }

    @Test
    public void next_RuntimeShuttingDown_SessionNotAssociatedWithThread() throws Exception {

        assertNull(HttpSessionPerThread.getCurrentInstance());

        //
        // no session associated with the thread, no session id, don't send anything
        //
        assertNull(HttpSessionPerThread.next(null, true));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HttpSessionPerThread getHttpSessionSimulationToTest() {

        return new HttpSessionPerThread();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
