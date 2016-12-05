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
import io.novaordis.gld.api.Service;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import io.novaordis.gld.api.cache.local.LocalCacheKeyStore;
import io.novaordis.gld.api.cache.local.LocalCacheService;
import io.novaordis.utilities.UserErrorException;

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

        this.keyStore = new LocalCacheKeyStore(this);
        this.service = new LocalCacheService(this);
        this.sampler = new SamplerImpl();

        keyStore.start();
        service.start();
        sampler.start();

        //
        // configure
        //

        int threadCount = 2;

        long singleThreadedRunnerSleepMs = -1L;

        this.background = false;


        multiThreadedRunner =
                new MultiThreadedRunnerImpl(service, threadCount, sampler, background, singleThreadedRunnerSleepMs);
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