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

package io.novaordis.gld.api;

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
    public void nullLoadStrategy() throws Exception {

        MockLoadDriver md = new MockLoadDriver();

        try {

            getServiceToTest(null, md);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null load strategy", msg);
        }
    }

    @Test
    public void identity() throws Exception {

        MockLoadStrategy ms = new MockLoadStrategy();
        MockLoadDriver md = new MockLoadDriver();

        Service s = getServiceToTest(ms, md);

        LoadStrategy ls = s.getLoadStrategy();
        assertEquals(ms, ls);

        LoadDriver ld = s.getLoadDriver();
        assertEquals(md, ld);
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void start_MissingLoadStrategy() throws Exception {

        MockLoadStrategy ms = new MockLoadStrategy();
        MockLoadDriver md = new MockLoadDriver();

        Service s = getServiceToTest(ms, md);
        ((ServiceBase)s).setLoadStrategy(null);

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

//    @Test
//    public void lifeCycle() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        assertFalse(s.isStarted());
//
//        s.start();
//
//        assertTrue(s.isStarted());
//
//        // starting an already started service instance should throw IllegalStateException
//
//        try {
//
//            s.start();
//            fail("should fail with IllegalStateException");
//        }
//        catch(IllegalStateException e) {
//            log.info(e.getMessage());
//        }
//
//        assertTrue(s.isStarted());
//
//        s.stop();
//
//        assertFalse(s.isStarted());
//
//        // stopping an already started stopped instance should be a noop
//
//        s.stop();
//
//        assertFalse(s.isStarted());
//    }

//    @Test
//    public void cannotPerformIfNotStarted() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        assertFalse(s.isStarted());
//
//        try {
//
//            s.perform(new MockOperation());
//            fail("should fail with IllegalStateException because the service is not started");
//        }
//        catch(IllegalStateException e) {
//            log.info(e.getMessage());
//        }
//    }

//    @Test
//    public void configureIgnoresArgumentsThatDoNotBelongToService() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        List<String> arguments = Arrays.asList(
//            "--this-argument-surely-is-not-interesting-to-the-service",
//            "apples",
//            "--this-argument-is-also-not-interesting-to-the-service",
//            "oranges"
//            );
//
//        s.configure(arguments);
//
//        // make sure no arguments were removed from list
//        assertEquals(4, arguments.size());
//    }

    @Test
    public void loadStrategyLifecycleIsCorrelatedToTheServiceLifecycle() throws Exception {

        MockLoadStrategy ms = new MockLoadStrategy();
        MockLoadDriver md = new MockLoadDriver();

        Service s = getServiceToTest(ms, md);

        s.start();

        assertTrue(s.isStarted());

        assertEquals(ms, s.getLoadStrategy());

        assertTrue(ms.isStarted());

        s.stop();

        assertFalse(ms.isStarted());
        assertFalse(s.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Service getServiceToTest(LoadStrategy s, LoadDriver d) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
