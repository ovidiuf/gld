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
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.Runner;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.mock.MockKeyStore;
import io.novaordis.gld.api.mock.MockService;
import io.novaordis.gld.api.mock.configuration.MockConfiguration;
import io.novaordis.gld.api.mock.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.mock.load.MockLdLoadStrategy;
import io.novaordis.gld.api.sampler.Sampler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public abstract class LoadDriverTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadDriverTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void init_run_sequence_SuccessfulRun() throws Exception {

        LoadDriver ld = getLoadDriverToTest();

        MockConfiguration mc = new MockConfiguration();

        //
        // We're installing a LoadStrategy that produces zero operations and gets depleted immediately - this will
        // make the runner finnish immediately, and successfully
        //
        ((MockLoadConfiguration)mc.getLoadConfiguration()).setOperations(0);

        ld.init(mc);

        Service service = ld.getService();
        Sampler sampler = ld.getSampler();
        KeyStore keyStore = ld.getKeyStore();
        Runner runner = ld.getRunner();

        //
        // instrument the Mock load strategy to report on the state of the lifecycle services
        //

        MockLdLoadStrategy mls = (MockLdLoadStrategy)service.getLoadStrategy();
        mls.setVerbose(false);
        mls.recordLifecycleComponentState();

        //
        // init() does not start anything
        //

        assertFalse(service.isStarted());
        assertFalse(service.getLoadStrategy().isStarted());
        assertFalse(service.getLoadStrategy().getKeyProvider().isStarted());
        assertFalse(sampler.isStarted());
        assertFalse(keyStore.isStarted());
        assertFalse(runner.isRunning());
        assertEquals(service, service.getLoadStrategy().getService());

        //
        // successful run - services will get started and then stopped cleanly upon each run
        //

        //
        // the load strategy is instrumented to insure all lifecycle components are started
        //

        ld.run();

        log.info("run completed");

        //
        // verify lifecycle-enabled component state when the first load strategy next() was called
        //
        boolean[] componentStarted = mls.getComponentStarted();
        assertTrue(componentStarted[MockLdLoadStrategy.SERVICE_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.LOAD_STRATEGY_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.KEY_PROVIDER_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.SAMPLER_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.KEY_STORE_STATE_INDEX]);

        //
        // make sure all lifecycle-enabled components are off
        //

        assertFalse(runner.isRunning());
        assertFalse(keyStore.isStarted());
        assertFalse(sampler.isStarted());
        assertFalse(service.isStarted());
        assertFalse(service.getLoadStrategy().isStarted());
        assertFalse(service.getLoadStrategy().getKeyProvider().isStarted());
    }

    @Test
    public void init_run_sequence_UnsuccessfulRun() throws Exception {

        LoadDriver ld = getLoadDriverToTest();

        MockConfiguration mc = new MockConfiguration();

        ld.init(mc);

        Service service = ld.getService();
        Sampler sampler = ld.getSampler();
        KeyStore keyStore = ld.getKeyStore();
        Runner runner = ld.getRunner();

        //
        // instrument the Mock load strategy to report on the state of the lifecycle services and then block
        // indefinitely before producing the next operation
        //

        MockLdLoadStrategy mls = (MockLdLoadStrategy)service.getLoadStrategy();
        mls.recordLifecycleComponentState();
        mls.blockIndefinitely();

        //
        // init() does not start anything
        //

        assertFalse(service.isStarted());
        assertFalse(service.getLoadStrategy().isStarted());
        assertFalse(service.getLoadStrategy().getKeyProvider().isStarted());
        assertFalse(sampler.isStarted());
        assertFalse(keyStore.isStarted());
        assertFalse(runner.isRunning());
        assertEquals(service, service.getLoadStrategy().getService());

        //
        // trigger an unsuccessful run by interrupting the main thread - services will get started and then stopped
        // cleanly upon run
        //

        Thread.currentThread().interrupt();

        try {

            ld.run();
            fail("should have thrown exception");
        }
        catch(InterruptedException e) {

            String msg = e.getMessage();
            log.info(msg);
        }

        //
        // verify lifecycle-enabled component state when the first load strategy next() was called
        //
        boolean[] componentStarted = mls.getComponentStarted();
        assertTrue(componentStarted[MockLdLoadStrategy.SERVICE_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.LOAD_STRATEGY_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.KEY_PROVIDER_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.SAMPLER_STATE_INDEX]);
        assertTrue(componentStarted[MockLdLoadStrategy.KEY_STORE_STATE_INDEX]);

        //
        // make sure all lifecycle-enabled components are off
        //

        assertFalse(runner.isRunning());
        assertFalse(keyStore.isStarted());
        assertFalse(sampler.isStarted());
        assertFalse(service.isStarted());
        assertFalse(service.getLoadStrategy().isStarted());
        assertFalse(service.getLoadStrategy().getKeyProvider().isStarted());
    }

    @Test
    public void insureAllComponentsAreStoppedOnGracefulExit() throws Exception {

        //
        // make sure the components such as the Service, Sampler and KeyStore are stopped on graceful exit
        //

        Service ms = new MockService();
        Sampler msp = new MockSampler();
        KeyStore mks = new MockKeyStore();

        ms.start();
        assertTrue(ms.isStarted());

        msp.start();
        assertTrue(msp.isStarted());

        mks.start();
        assertTrue(mks.isStarted());

        LoadDriver ld = getLoadDriverToTest();

        fail("return here: " + ld);

        assertFalse(mks.isStarted());
        assertFalse(msp.isStarted());
        assertFalse(ms.isStarted());
    }

    @Test
    public void insureAllComponentsAreStoppedOnFailure() throws Exception {

        //
        // make sure the components such as the Service, Sampler and KeyStore are stopped on failure
        //

        Service ms = new MockService();
        Sampler msp = new MockSampler();
        KeyStore mks = new MockKeyStore();

        ms.start();
        assertTrue(ms.isStarted());

        msp.start();
        assertTrue(msp.isStarted());

        mks.start();
        assertTrue(mks.isStarted());


        fail("add here logic that would simulate components stopped on failure");


        assertFalse(mks.isStarted());
        assertFalse(msp.isStarted());
        assertFalse(ms.isStarted());
    }

    @Test
    public void ifAComponentFailsToStopOthersAreStoppedCorrectly() throws Exception {

        fail("add here logic ");
    }


    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadDriver getLoadDriverToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
