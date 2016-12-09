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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.driver.sampler.Sampler;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LoadDriverImplTest extends LoadDriverTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void init_turnOff_cycle() throws Exception {

        LoadDriverImpl ld = new LoadDriverImpl("test", true);

        MockConfiguration mc = new MockConfiguration();

        ld.init(mc);

        Service service = ld.getService();
        Sampler sampler = ld.getSampler();
        KeyStore keyStore = ld.getKeyStore();
        MultiThreadedRunner runner = ld.getRunner();

        assertTrue(service.isStarted());
        assertTrue(sampler.isStarted());
        assertTrue(keyStore.isStarted());

        //
        // we did not start the runner
        //

        assertFalse(runner.isRunning());

        ld.turnOff();

        //
        // make sure all lifecycle-enabled components are off
        //

        assertFalse(runner.isRunning());

        assertFalse(keyStore.isStarted());
        assertFalse(sampler.isStarted());
        assertFalse(service.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected LoadDriverImpl getLoadDriverToTest() throws Exception {

        return new LoadDriverImpl("test", true);
    }
    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
