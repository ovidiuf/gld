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
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceFactory;
import io.novaordis.gld.api.StoreConfiguration;
import io.novaordis.gld.api.store.KeyStoreFactory;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LoadDriverImpl implements LoadDriver {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadDriverImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Package protected static ----------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String id;

    private volatile boolean background;

    //
    // lifecycle enabled components
    //

    private Service service;

    private Sampler sampler;

    private KeyStore keyStore;

    private MultiThreadedRunner multiThreadedRunner;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param id each member of a load driver cluster must have an unique ID.
     *
     * @param background - if true, the load driver assumes the JVM runs as a background process and does not interact
     *                   directly with stdin/stdout/stderr. If false, the load driver assumes the JVM runs in foreground
     *                   and can be controlled directly from the console.
     *
     */
    public LoadDriverImpl(String id, boolean background) {

        this.id = id;
        this.background = background;
    }

    // LoadDriver implementation ---------------------------------------------------------------------------------------

    @Override
    public Service getService() {

        return service;
    }

    @Override
    public String getID() {

        return id;
    }

    @Override
    public void init(Configuration c) throws Exception {

        ServiceConfiguration svc = c.getServiceConfiguration();
        LoadConfiguration ldc = c.getLoadConfiguration();
        StoreConfiguration stc = c.getStoreConfiguration();

        //
        // load strategy instantiation and installation; the load strategy usually initializes the key provider,
        // which is accessible with LoadStrategy.getKeyProvider()
        //

        LoadStrategy ls = LoadStrategyFactory.build(svc, ldc);

        //
        // service initialization and configuration
        //

        service = ServiceFactory.buildInstance(svc, ls, this);

        service.start();

        //
        // load configuration
        //

        sampler = new SamplerImpl();

        //
        // register operations
        //

        Set<Class<? extends Operation>> operations = ls.getOperationTypes();
        operations.forEach(sampler::registerOperation);

        sampler.start();

        long singleThreadedRunnerSleepMs = -1L;

        if (stc != null) {

            keyStore = KeyStoreFactory.build(stc);
            keyStore.start();
        }

        multiThreadedRunner = new MultiThreadedRunnerImpl(
                service, ldc.getThreadCount(), sampler, background, singleThreadedRunnerSleepMs, keyStore);
    }

    @Override
    public void turnOff() {

        //
        // execute the init() operations in reverse order
        //

        if (multiThreadedRunner != null) {

            try {

                multiThreadedRunner.stop();

            } catch (Exception e) {

                log.warn("failed to stop the runner: " + e.getMessage());
            }
        }

        if (keyStore != null) {

            try {

                keyStore.stop();

            } catch (Exception e) {

                log.warn("failed to stop the key store: " + e.getMessage());
            }
        }

        if (sampler != null) {

            try {

                sampler.stop();

            } catch (Exception e) {

                log.warn("failed to stop the sampler: " + e.getMessage());
            }
        }

        if (service != null) {

            try {

                // this will also stop the associated load strategy
                service.stop();

            }
            catch(Exception e) {

                log.warn("failed to stop service: " + e.getMessage());
            }
        }
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

        //
        // turnOff() will be invoked in a finally block by the upper layer
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

    @Override
    public String toString() {

        return getID() + (background ? " (background)" : " (foreground)");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    Sampler getSampler() {

        return sampler;
    }

    KeyStore getKeyStore() {

        return keyStore;
    }

    MultiThreadedRunner getRunner() {

        return multiThreadedRunner;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
