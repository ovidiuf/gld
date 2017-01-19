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

import io.novaordis.gld.api.Runner;
import io.novaordis.gld.api.configuration.Configuration;
import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.OutputConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.extension.ExtensionInfrastructure;
import io.novaordis.gld.api.sampler.SamplerConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.configuration.StoreConfiguration;
import io.novaordis.gld.api.store.KeyStoreFactory;
import io.novaordis.gld.api.sampler.Sampler;
import io.novaordis.gld.api.sampler.SamplerImpl;
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

    private MultiThreadedRunner runner;

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
    public KeyStore getKeyStore() {

        return keyStore;
    }

    @Override
    public Sampler getSampler() {

        return sampler;
    }

    @Override
    public Runner getRunner() {

        return runner;
    }

    @Override
    public String getID() {

        return id;
    }

    @Override
    public void init(Configuration c) throws Exception {

        log.debug("initializing load driver " + this);

        ServiceConfiguration serviceConfiguration = c.getServiceConfiguration();
        LoadConfiguration loadConfiguration = c.getLoadConfiguration();
        StoreConfiguration storeConfiguration = c.getStoreConfiguration();
        OutputConfiguration outputConfiguration = c.getOutputConfiguration();

        //
        // load strategy instantiation and installation; the load strategy usually initializes the key provider,
        // which is accessible with LoadStrategy.getKeyProvider()
        //

        LoadStrategy loadStrategy = LoadStrategyFactory.build(serviceConfiguration, loadConfiguration);

        //
        // service initialization and configuration; it also establishes service - load strategy bidirectional
        // relationship
        //

        this.service = ExtensionInfrastructure.initializeExtensionService(serviceConfiguration);

        //
        // at this point the service is instantiated and configured, initialize the reference relationships
        //

        service.setLoadStrategy(loadStrategy);
        service.setLoadDriver(this);
        loadStrategy.setService(service);

        //
        // initialize the sampler and configure statistics output, if any.
        //

        SamplerConfiguration sc =
                outputConfiguration == null ? null : outputConfiguration.getSamplerConfiguration();

        if (sc != null) {

            //
            // there is configuration, it means we want statistics
            //

            this.sampler = new SamplerImpl();

            //
            // configure the sampler
            //

            this.sampler.configure(sc);

            //
            // register operations to be sampled
            //

            Set<Class<? extends Operation>> operations = loadStrategy.getOperationTypes();
            operations.forEach(sampler::registerOperation);
        }

        long singleThreadedRunnerSleepMs = -1L;

        if (storeConfiguration != null) {

            this.keyStore = KeyStoreFactory.build(storeConfiguration);
        }

        this.runner =
                new MultiThreadedRunnerImpl(
                        service,
                        sampler,
                        keyStore,
                        loadConfiguration.getThreadCount(),
                        background,
                        singleThreadedRunnerSleepMs);
    }

    @Override
    public void run() throws Exception {

        try {

            //
            // start the dependent lifecycle instances the start the runner, then enter the main control loop
            //

            startLifeCycleInstances();

            runner.run();

            //
            // the main control loop
            //
        }
        finally {

            //
            // execute the init() operations in reverse order and stops lifecycle-enabled components. The sequence must
            // be invoked in a finally block, to leave the driver and associated components in a clean state,
            // irrespective of whether the driver completed the run cleanly, or existed because of an exception. If it
            // cannot complete, must log and exit. Failure to stop one component must not prevent other components to
            // stop.
            //

            if (keyStore != null) {

                try {

                    keyStore.stop();

                } catch (Throwable e) {

                    log.warn("failed to stop the key store: " + e.getMessage());
                }
            }

            if (sampler != null) {

                try {

                    sampler.stop();

                } catch (Throwable e) {

                    log.warn("failed to stop the sampler: " + e.getMessage());
                }
            }

            if (service != null) {

                try {

                    service.stop(); // this will stop internal lifecycle components, recursively.

                }
                catch(Throwable e) {

                    log.warn("failed to stop service: " + e.getMessage());
                }
            }
        }
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

    void startLifeCycleInstances() throws Exception {

        service.start(); // this will start internal lifecycle components, recursively.

        if (sampler != null) {

            sampler.start();
        }

        if (keyStore != null) {

            keyStore.start();
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
