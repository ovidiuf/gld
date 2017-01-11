/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.MockLoadDriver;
import io.novaordis.gld.api.mock.load.MockLoadStrategy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // topology --------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        Service s = getServiceToTest();

        LoadStrategy ls = s.getLoadStrategy();
        assertNull(ls);

        MockLoadStrategy ms = new MockLoadStrategy();
        s.setLoadStrategy(ms);
        ls = s.getLoadStrategy();
        assertEquals(ms, ls);

        LoadDriver ld = s.getLoadDriver();
        assertNull(ld);

        MockLoadDriver md = new MockLoadDriver();
        s.setLoadDriver(md);
        ld = s.getLoadDriver();
        assertEquals(md, ld);
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void start_MissingLoadStrategy() throws Exception {

        Service s = getServiceToTest();

        assertNull(s.getLoadStrategy());

        try {

            s.start();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);

            assertEquals("incompletely configured service instance: load strategy not installed", msg);
        }

    }

    @Test
    public void lifeCycle() throws Exception {

        Service s = getServiceToTest();

        MockLoadStrategy mls = new MockLoadStrategy();
        s.setLoadStrategy(mls);

        assertFalse(s.isStarted());

        s.start();

        assertTrue(s.isStarted());

        // idempotence: starting an already started service instance, it should be a noop

        s.start();

        assertTrue(s.isStarted());

        assertTrue(mls.isStarted());

        s.stop();

        assertFalse(s.isStarted());

        // idempotence:  stopping an already started stopped instance should be a noop

        s.stop();

        assertFalse(s.isStarted());
    }

    @Test
    public void loadStrategyLifecycleIsCorrelatedToTheServiceLifecycle() throws Exception {

        Service s = getServiceToTest();

        MockLoadStrategy ms = new MockLoadStrategy();
        MockLoadDriver md = new MockLoadDriver();

        s.setLoadStrategy(ms);
        s.setLoadDriver(md);

        s.start();

        assertTrue(s.isStarted());

        assertEquals(ms, s.getLoadStrategy());

        assertTrue(s.getLoadStrategy().isStarted());

        s.stop();

        assertFalse(s.getLoadStrategy().isStarted());
        assertFalse(s.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Service getServiceToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
