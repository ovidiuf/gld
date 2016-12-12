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
import io.novaordis.gld.api.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.driver.MockOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockLoadStrategy implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockLoadStrategy.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean verbose;
    private volatile boolean started;
    private volatile boolean initialized;
    private AtomicInteger remainingOperations;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Will generate an unlimited number of operations.
     */
    public MockLoadStrategy() {
        this(-1);
    }

    /**
     * @param operationCount the number of operations to generate.
     */
    public MockLoadStrategy(int operationCount) {

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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
