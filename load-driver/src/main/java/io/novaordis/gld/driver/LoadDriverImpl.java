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

import io.novaordis.gld.api.Configuration;
import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadDriverConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceFactory;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import io.novaordis.gld.api.cache.local.LocalCacheKeyStore;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LoadDriverImpl implements LoadDriver {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Package protected static ----------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean background;

    private Service service;

    private KeyStore keyStore;

    private MultiThreadedRunner multiThreadedRunner;

    private Sampler sampler;

    // Constructors ----------------------------------------------------------------------------------------------------

    public LoadDriverImpl() {

        //
        // noop constructor, all initialization takes place in init()
        //
    }

    // LoadDriver implementation ---------------------------------------------------------------------------------------

    @Override
    public KeyStore getKeyStore() {

        return keyStore;
    }

    @Override
    public Service getService() {

        return service;
    }

    @Override
    public void init(Configuration c) throws Exception {

        //
        // service initialization and configuration
        //

        ServiceConfiguration sc = c.getServiceConfiguration();
        service = ServiceFactory.buildInstance(sc.getType(), sc.getImplementation(), this);

        //
        // load strategy instantiation and installation
        //

        Map<String, Object> loadStrategyConfiguration = sc.getMap(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        LoadStrategy ls = LoadStrategyFactory.buildInstance(sc.getType(), loadStrategyConfiguration);

        service.start();

        keyStore = new LocalCacheKeyStore(this);
        keyStore.start();

        //
        // load configuration
        //

        LoadDriverConfiguration lc = c.getLoadDriverConfiguration();

        sampler = new SamplerImpl();

        //
        // register operations
        //

        for(Class<? extends Operation> ot : service.getLoadStrategy().getOperationTypes()) {

            sampler.registerOperation(ot);
        }

        sampler.start();

        long singleThreadedRunnerSleepMs = -1L;

        this.background = false;

        multiThreadedRunner = new MultiThreadedRunnerImpl(
                service, lc.getThreadCount(), sampler, background, singleThreadedRunnerSleepMs);
    }

    @Override
    public void run() {

        //
        // start the load and then enter the main control loop
        //

        try {

            multiThreadedRunner.run();
        }
        catch(Exception e) {

            throw new RuntimeException("NOT YET IMPLEMENTED " + e);
        }

        //
        // the main control loop
        //
    }

    @Override
    public boolean isBackground() {

        return background;
    }

    @Override
    public void error(Throwable t) {

        if (t instanceof UserErrorException) {

            String msg = t.getMessage();
            error(msg);

        }
        else {

            String msg = "internal error: " + t.getClass().getSimpleName();

            String m = t.getMessage();

            if (m != null) {

                msg += " (" + m + "), consult the log for more details";
            }

            error(msg);
        }
    }

    @Override
    public void error(String msg) {

        System.err.println("[error]: " + msg);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
