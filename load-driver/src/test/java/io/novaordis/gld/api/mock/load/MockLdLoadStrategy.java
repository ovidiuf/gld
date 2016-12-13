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

package io.novaordis.gld.api.mock.load;

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.mock.MockOperation;
import io.novaordis.gld.api.mock.configuration.MockLoadConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;

/**
 * Named MockLdLoadStrategy instead of MockLoadStrategy to avoid non-obviously clashes with the API MockLoadStrategy.
 * Ran into problems at times when debugging the modules together.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockLdLoadStrategy implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockLdLoadStrategy.class);

    public static final int SERVICE_STATE_INDEX = 0;
    public static final int SAMPLER_STATE_INDEX = 1;
    public static final int KEY_STORE_STATE_INDEX = 2;
    public static final int LOAD_STRATEGY_STATE_INDEX = 3;
    public static final int KEY_PROVIDER_STATE_INDEX = 4;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean verbose;
    private volatile boolean started;
    private volatile boolean initialized;
    private AtomicInteger remainingOperations;

    private volatile boolean recordLifecycleComponentState;

    // Service, Sampler, KeyStore, LoadStrategy, KeyProvider
    private boolean[] componentStarted = new boolean[5];

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Will generate an unlimited number of operations.
     */
    public MockLdLoadStrategy() {
        this(-1);
    }

    /**
     * @param operationCount the number of operations to generate.
     */
    public MockLdLoadStrategy(int operationCount) {

        if (operationCount >= 0) {
            remainingOperations = new AtomicInteger(operationCount);
        }
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {

        return ServiceType.mock;
    }

    @Override
    public String getName() {
        throw new RuntimeException("getName() NOT YET IMPLEMENTED");
    }

    @Override
    public void init(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        //
        // because init() is usually NOT idempotent, fail this if we call it twice
        //

        if (initialized) {

            fail(this + ".init(...) called twice");
        }

        MockLoadConfiguration mlc = (MockLoadConfiguration)lc;
        Long operations = mlc.getOperations();
        if (operations != null) {
            remainingOperations = new AtomicInteger(operations.intValue());
        }

        this.initialized = true;

        log.info(this + ".init()");
    }

    @Override
    public void start() throws Exception {

        started = true;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public void stop() {

        started = false;
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {


        if (recordLifecycleComponentState) {

            //
            // record the state on the first operation
            //

            recordLifecycleComponentState = false;

            componentStarted[LOAD_STRATEGY_STATE_INDEX] = isStarted();
            KeyProvider provider = getKeyProvider();
            componentStarted[KEY_PROVIDER_STATE_INDEX] = provider.isStarted();
        }

        //
        // if the runtime is shutting down, comply and return null; tests rely on this behavior
        //
        if (runtimeShuttingDown) {

            log.info(this + " has been notified that the runtime is shutting down, stopping building operations ...");
            return null;
        }


        if (remainingOperations != null) {

            if (remainingOperations.getAndDecrement() <= 0) {
                return null;
            }
        }

        MockOperation mo = new MockOperation();

        if (verbose) {
            mo.setVerbose(true);
        }

        return mo;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        HashSet<Class<? extends Operation>> result = new HashSet<>();
        result.add(MockOperation.class);
        return result;
    }

    @Override
    public KeyProvider getKeyProvider() {
        throw new RuntimeException("getKeyProvider() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getRemainingOperations()
    {
        int i = remainingOperations.get();

        // the counter is decremented under 0, and that has "0" semantics

        if (i < 0)
        {
            i = 0;
        }

        return i;
    }

    @Override
    public String toString() {

        return "load-driver MockLoadStrategy";
    }

    /**
     * We need to explicitly set the instance as verbose in order to next log.info(), otherwise the high concurrency
     * tests are too noisy. If this load strategy is verbose, then the MockOperations it builds will be verbose.
     */
    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    public void recordLifecycleComponentState() {

        this.recordLifecycleComponentState = true;
    }

    public boolean[] getComponentStarted() {

        return componentStarted;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
