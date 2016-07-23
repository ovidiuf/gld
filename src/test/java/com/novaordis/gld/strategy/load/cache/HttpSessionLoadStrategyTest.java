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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.Operation;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    @Test
    public void unconfiguredStrategyFailsUponFirstUsage() throws Exception {

        //
        // noop, we're find with unconfigured instances
        //
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void cleanup() {

        HttpSessionSimulation.destroyInstance();
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void next_firstInvocation() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();

        HttpSessionSimulation s = HttpSessionSimulation.getCurrentInstance();
        assertNull(s);

        Operation o = ls.next(null, null, false);

        HttpSessionSimulation s2 = HttpSessionSimulation.getCurrentInstance();
        assertNotNull(s2);

        assertTrue(o instanceof HttpSessionCreate);
    }

    @Test
    public void next_RuntimeShuttingDown_SessionAssociatedWithThread() throws Exception {

        HttpSessionLoadStrategy hsls = new HttpSessionLoadStrategy();

        HttpSessionSimulation s = HttpSessionSimulation.initializeInstance();
        assertNotNull(s);

        String sessionId = s.getSessionId();

        //
        // should generate an "invalidate" operation
        //
        HttpSessionInvalidate i = (HttpSessionInvalidate)hsls.next(null, null, true);

        assertEquals(sessionId, i.getSessionId());

        assertNull(HttpSessionSimulation.getCurrentInstance());
    }

    @Test
    public void next_RuntimeShuttingDown_SessionNotAssociatedWithThread() throws Exception {

        HttpSessionLoadStrategy hsls = new HttpSessionLoadStrategy();

        assertNull(HttpSessionSimulation.getCurrentInstance());

        //
        // no session associated with the thread, no session id, don't send anything
        //
        assertNull(hsls.next(null, null, true));
    }

    // configuration ---------------------------------------------------------------------------------------------------

    @Test
    public void configuration_writeCount_Default() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();

        assertEquals(HttpSessionSimulation.DEFAULT_WRITE_COUNT, ls.getWriteCount());

        HttpSessionCreate c = (HttpSessionCreate)ls.next(null, null, false);

        HttpSessionSimulation s = c.getHttpSession();

        assertEquals(HttpSessionSimulation.DEFAULT_WRITE_COUNT, s.getWriteCount());
    }

    @Test
    public void configuration_writeCount() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();

        MockConfiguration mc = new MockConfiguration();

        List<String> arguments = new ArrayList<>(Arrays.asList("--write-count", "7"));

        ls.configure(mc, arguments, 0);

        assertEquals(7, ls.getWriteCount());

        HttpSessionCreate c = (HttpSessionCreate)ls.next(null, null, false);

        HttpSessionSimulation s = c.getHttpSession();

        assertEquals(7, s.getWriteCount());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected LoadStrategy getLoadStrategyToTest(Configuration config, List<String> arguments, int from)
            throws Exception {

        return new HttpSessionLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
