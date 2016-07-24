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
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionPerThread;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HttpSessionLoadStrategyTest.class);

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

        HttpSessionPerThread.destroyInstance();
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    // mode ------------------------------------------------------------------------------------------------------------

    @Test
    public void mode_default() throws Exception {

        HttpSessionLoadStrategy strategy = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();

        // the default mode must have a valid --session count otherwise it will fail
        List<String> args = new ArrayList<>(Arrays.asList("something", "--sessions", "1", "somethingelse"));

        strategy.configure(mc, args, 0);
        assertEquals(HttpSessionLoadStrategy.DEFAULT_MODE, strategy.getMode());
        assertEquals(2, args.size());

        assertEquals(1, strategy.getSessionCount().intValue());
    }

    @Test
    public void mode_default_NoSessionCount() throws Exception {

        HttpSessionLoadStrategy s = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();

        List<String> args = new ArrayList<>();

        try {
            s.configure(mc, args, 0);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("--session count required"));
        }
    }

    @Test
    public void mode_explicitDefault() throws Exception {

        HttpSessionLoadStrategy s = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();

        // the default mode must have a valid --session count otherwise it will fail

        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "--http-session-mode", "default", "--sessions", "7", "somethingelse"));

        s.configure(mc, args, 0);

        assertEquals(HttpSessionLoadStrategy.DEFAULT_MODE, s.getMode());
        assertEquals(2, args.size());
        assertEquals("something", args.get(0));
        assertEquals("somethingelse", args.get(1));

        assertEquals(7, s.getSessionCount().intValue());
    }

    @Test
    public void mode_explicitDefault_NoSessionCount() throws Exception {

        HttpSessionLoadStrategy s = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();

        // the default mode must have a valid --session count otherwise it will fail

        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "--http-session-mode", "default", "somethingelse"));

        try {
            s.configure(mc, args, 0);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("--session count required"));
        }
    }

    @Test
    public void mode_sessionPerThread() throws Exception {

        HttpSessionLoadStrategy s = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();
        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "--http-session-mode", "session-per-thread", "somethingelse"));
        s.configure(mc, args, 0);
        assertEquals(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE, s.getMode());
        assertEquals(2, args.size());
        assertEquals("something", args.get(0));
        assertEquals("somethingelse", args.get(1));

        assertNull(s.getSessionCount());
    }

    @Test
    public void mode_invalid() throws Exception {

        HttpSessionLoadStrategy s = new HttpSessionLoadStrategy();

        MockConfiguration mc = new MockConfiguration();

        try {
            s.configure(mc, new ArrayList<>(Arrays.asList(
                    "something", "--http-session-mode", "surely-there-is-no-such-mode", "somethingelse")), 0);

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            log.info(e.getMessage());
        }
    }

    // configuration ---------------------------------------------------------------------------------------------------

    @Test
    public void configuration_writeCount_Default() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        ls.setMode(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE);

        assertEquals(HttpSessionSimulation.DEFAULT_WRITE_COUNT, ls.getWriteCount());

        HttpSessionCreate c = (HttpSessionCreate)ls.next(null, null, false);

        HttpSessionSimulation s = c.getHttpSession();

        assertEquals(HttpSessionSimulation.DEFAULT_WRITE_COUNT, s.getConfiguredWriteCount());
    }

    @Test
    public void configuration_writeCount() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        ls.setMode(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE);

        MockConfiguration mc = new MockConfiguration();

        List<String> arguments = new ArrayList<>(Arrays.asList("--write-count", "7"));

        ls.configure(mc, arguments, 0);

        assertEquals(7, ls.getWriteCount());

        HttpSessionCreate c = (HttpSessionCreate)ls.next(null, null, false);

        HttpSessionSimulation s = c.getHttpSession();

        assertEquals(7, s.getConfiguredWriteCount());
    }

    @Test
    public void configuration_SessionCount() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        MockConfiguration mc = new MockConfiguration();

        List<String> arguments = new ArrayList<>(Arrays.asList("--sessions", "8"));

        ls.configure(mc, arguments, 0);

        assertEquals(8, ls.getSessionCount().intValue());
    }

    // next() session-per-thread mode ----------------------------------------------------------------------------------

    @Test
    public void next_sessionPerThreadMode_firstInvocation() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        ls.setMode(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE);
        assertNull(HttpSessionPerThread.getCurrentInstance());

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof HttpSessionCreate);

        assertNotNull(HttpSessionPerThread.getCurrentInstance());
    }

    @Test
    public void next_sessionPerThreadMode_RuntimeShuttingDown_SessionAssociatedWithThread() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        ls.setMode(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE);

        HttpSessionPerThread s = HttpSessionPerThread.initializeInstance();
        assertNotNull(s);

        String sessionId = s.getSessionId();

        //
        // should generate an "invalidate" operation
        //
        HttpSessionInvalidate i = (HttpSessionInvalidate) ls.next(null, null, true);

        assertEquals(sessionId, i.getSessionId());

        assertNull(HttpSessionPerThread.getCurrentInstance());
    }

    @Test
    public void next_sessionPerThreadMode_RuntimeShuttingDown_SessionNotAssociatedWithThread() throws Exception {

        HttpSessionLoadStrategy ls = new HttpSessionLoadStrategy();
        ls.setMode(HttpSessionLoadStrategy.SESSION_PER_THREAD_MODE);

        assertNull(HttpSessionPerThread.getCurrentInstance());

        //
        // no session associated with the thread, no session id, don't send anything
        //
        assertNull(ls.next(null, null, true));
    }

    // next() default mode ---------------------------------------------------------------------------------------------

    //
    // @see DefaultHttpSessionLoadStrategyLogicTest
    //

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
