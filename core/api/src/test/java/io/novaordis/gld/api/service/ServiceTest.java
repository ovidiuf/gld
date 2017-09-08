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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

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

        LoadStrategy ls2 = getMatchingLoadStrategyToTest(s);
        s.setLoadStrategy(ls2);
        ls = s.getLoadStrategy();
        assertEquals(ls2, ls);

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
            assertTrue(msg.contains("load strategy not installed"));
        }
    }

    @Test
    public void lifeCycle() throws Exception {

        Service s = getServiceToTest();

        LoadStrategy mls = getMatchingLoadStrategyToTest(s);
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

        LoadStrategy ms = getMatchingLoadStrategyToTest(s);
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

    /**
     * @return a Service instance fully configured so it can be successfully started, provided that the associated
     * LoadStrategy instance, returned by getMatchingLoadStrategyToTest(), is applied with setLoadStrategy(). The
     * method is also responsible with configuring the context with the elements it introduces, to allow a successful
     * lifecycle.
     *
     * @see ServiceTest#getMatchingLoadStrategyToTest(Service)
     */
    protected abstract Service getServiceToTest() throws Exception;

    /**
     * The method is also responsible with configuring the context with the elements it introduces, to allow a
     * successful lifecycle.
     *
     * The method MUST NOT associate the service with the produced load strategy, it must use the Service reference
     * in read-only mode. If an association needs to be made, that is the upper layer's responsibility.
     */
    protected abstract LoadStrategy getMatchingLoadStrategyToTest(Service s) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
