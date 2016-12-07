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
import io.novaordis.gld.api.LoadConfiguration;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.RandomContentGenerator;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceFactory;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import io.novaordis.gld.api.cache.local.LocalCacheKeyStore;

import java.util.Set;

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

    private RandomContentGenerator contentGenerator;

    private Service service;

    private KeyStore keyStore;

    private MultiThreadedRunner multiThreadedRunner;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param background - if true, the load driver assumes the JVM runs as a background process and does not interact
     *                   directly with stdin/stdout/stderr. If false, the load driver assumes the JVM runs in foreground
     *                   and can be controlled directly from the console.
     *
     */
    public LoadDriverImpl(boolean background) {

        this.background = background;

        this.contentGenerator = new RandomContentGenerator();
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

        LoadConfiguration lc = c.getLoadConfiguration();
        ServiceConfiguration sc = c.getServiceConfiguration();

        //
        // load strategy instantiation and installation
        //

        LoadStrategy ls = LoadStrategyFactory.build(sc, lc);


        //
        // service initialization and configuration
        //

        service = ServiceFactory.buildInstance(sc, ls, this);


        service.start();

        keyStore = new LocalCacheKeyStore(this);
        keyStore.start();

        //
        // load configuration
        //

        Sampler sampler = new SamplerImpl();

        //
        // register operations
        //

        Set<Class<? extends Operation>> operations = ls.getOperationTypes();
        operations.forEach(sampler::registerOperation);

        sampler.start();

        long singleThreadedRunnerSleepMs = -1L;

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
    public boolean background() {

        return background;
    }

    @Override
    public void error(Throwable t) {

        error(Util.formatErrorMessage(t));
    }

    @Override
    public void error(String msg) {

        System.err.println(Util.formatErrorMessage(msg));
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
