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

import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionWrite;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class DefaultHttpSessionLoadStrategyLogicTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(DefaultHttpSessionLoadStrategyLogicTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_InvalidSessionCount() throws Exception {

        try {
            new DefaultHttpSessionLoadStrategyLogic(-1, 10, 1024);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_InvalidSessionCount2() throws Exception {

        try {
            new DefaultHttpSessionLoadStrategyLogic(0, 10, 1024);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_InvalidWritesPerSession() throws Exception {

        try {
            new DefaultHttpSessionLoadStrategyLogic(10, -1, 1024);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_InvalidWritesPerSession2() throws Exception {

        try {
            new DefaultHttpSessionLoadStrategyLogic(10, 0, 1024);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor() throws Exception {

        DefaultHttpSessionLoadStrategyLogic logic = new DefaultHttpSessionLoadStrategyLogic(10, 11, 1025);
        assertEquals(10, logic.getConfiguredSessionCount());
        assertEquals(11, logic.getWritesPerSession());
        assertEquals(1025, logic.getInitialSessionSize().intValue());
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void next_OneSession() throws Exception {

        int sessions = 1;
        int writesPerSession = 1;
        int initialSessionSize = 1027;

        DefaultHttpSessionLoadStrategyLogic logic =
                new DefaultHttpSessionLoadStrategyLogic(sessions, writesPerSession, initialSessionSize);

        HttpSessionCreate o = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s = o.getHttpSession();
        assertEquals(1, s.getRemainingWrites());
        assertEquals(1027, s.getInitialSessionSize());

        //
        // next invocation is a write
        //

        HttpSessionWrite w = (HttpSessionWrite)logic.next(false);
        HttpSessionSimulation s2 = w.getHttpSession();
        assertEquals(1027, s2.getInitialSessionSize());

        assertEquals(0, s2.getRemainingWrites());
        assertEquals(s, s2);

        //
        // next invocation is an invalidation
        //

        HttpSessionInvalidate i = (HttpSessionInvalidate)logic.next(false);
        HttpSessionSimulation s3 = i.getHttpSession();
        assertEquals(0, s3.getRemainingWrites());
        assertEquals(s, s3);

        //
        // next invocation is a new session
        //

        HttpSessionCreate c2 = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s4 = c2.getHttpSession();
        assertEquals(1, s4.getRemainingWrites());

        //
        // the sessions are different
        //
        assertFalse(s.equals(s4));

        //
        // next invocation is a write
        //
        HttpSessionWrite w2 = (HttpSessionWrite)logic.next(false);
        HttpSessionSimulation s5 = w2.getHttpSession();
        assertEquals(0, s5.getRemainingWrites());
        assertEquals(s5, s4);
        assertNotEquals(s5, s3);

        //
        // next invocation is an invalidation
        //

        HttpSessionInvalidate i2 = (HttpSessionInvalidate)logic.next(false);
        HttpSessionSimulation s6 = i2.getHttpSession();
        assertEquals(0, s6.getRemainingWrites());
        assertEquals(s6, s4);
        assertNotEquals(s6, s3);

        //
        // one more cycle
        //

        //
        // next invocation is a new session
        //

        HttpSessionCreate c3 = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s7 = c3.getHttpSession();
        assertEquals(1, s7.getRemainingWrites());
        assertNotEquals(s7, s);
        assertNotEquals(s7, s4);
    }

    @Test
    public void next() throws Exception {

        int sessions = 3;
        int writesPerSession = 1;
        int initialSessionSize = 1029;

        DefaultHttpSessionLoadStrategyLogic logic =
                new DefaultHttpSessionLoadStrategyLogic(sessions, writesPerSession, initialSessionSize);

        //
        // the first three operations are always "create"
        //

        HttpSessionCreate o = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s = o.getHttpSession();
        assertEquals(1, s.getRemainingWrites());
        assertEquals(1029, s.getInitialSessionSize());

        HttpSessionCreate o2 = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s2 = o2.getHttpSession();
        assertEquals(1, s2.getRemainingWrites());

        HttpSessionCreate o3 = (HttpSessionCreate)logic.next(false);
        HttpSessionSimulation s3 = o3.getHttpSession();
        assertEquals(1, s3.getRemainingWrites());

        //
        // next invocation is a write
        //

        HttpSessionWrite w = (HttpSessionWrite)logic.next(false);
        HttpSessionSimulation s4 = w.getHttpSession();
        assertEquals(0, s4.getRemainingWrites());
    }

    // next() Runtime Shutting Down ------------------------------------------------------------------------------------

    @Test
    public void next_RuntimeShuttingDown() throws Exception {

        int sessions = 3;
        int writesPerSession = 10;
        int initialSessionSize = 1024;

        DefaultHttpSessionLoadStrategyLogic logic =
                new DefaultHttpSessionLoadStrategyLogic(sessions, writesPerSession, initialSessionSize);

        assertEquals(0, logic.getActiveSessionCount());

        Set<HttpSessionSimulation> sessionSet = new HashSet<>();

        //
        // Fill up with sessions
        //

        HttpSessionOperation o;

        o = logic.next(false);
        assertTrue(o instanceof HttpSessionCreate);
        sessionSet.add(o.getHttpSession());
        assertEquals(1, sessionSet.size());
        assertEquals(1, logic.getActiveSessionCount());

        o = logic.next(false);
        assertTrue(o instanceof HttpSessionCreate);
        sessionSet.add(o.getHttpSession());
        assertEquals(2, sessionSet.size());
        assertEquals(2, logic.getActiveSessionCount());

        o = logic.next(false);
        assertTrue(o instanceof HttpSessionCreate);
        sessionSet.add(o.getHttpSession());
        assertEquals(3, sessionSet.size());
        assertEquals(3, logic.getActiveSessionCount());

        o = logic.next(false);
        assertTrue(o instanceof HttpSessionWrite);
        sessionSet.add(o.getHttpSession());
        assertEquals(3, sessionSet.size());
        assertEquals(3, logic.getActiveSessionCount());

        //
        // start winding down
        //

        o = logic.next(true);
        assertTrue(o instanceof HttpSessionInvalidate);
        HttpSessionSimulation s = o.getHttpSession();
        assertNotNull(sessionSet.remove(s));
        assertEquals(2, sessionSet.size());
        assertEquals(2, logic.getActiveSessionCount());

        o = logic.next(true);
        assertTrue(o instanceof HttpSessionInvalidate);
        s = o.getHttpSession();
        assertNotNull(sessionSet.remove(s));
        assertEquals(1, sessionSet.size());
        assertEquals(1, logic.getActiveSessionCount());

        o = logic.next(true);
        assertTrue(o instanceof HttpSessionInvalidate);
        s = o.getHttpSession();
        assertNotNull(sessionSet.remove(s));
        assertEquals(0, sessionSet.size());
        assertEquals(0, logic.getActiveSessionCount());

        o = logic.next(true);
        assertNull(o);

        o = logic.next(true);
        assertNull(o);

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}
