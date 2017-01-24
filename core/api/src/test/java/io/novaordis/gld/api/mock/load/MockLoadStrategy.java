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
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.MockOperation;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;

/**
 * Named
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockLoadStrategy implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockLoadStrategy.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean initialized;
    private volatile boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {

        return ServiceType.mock;
    }

    @Override
    public Service getService() {
        throw new RuntimeException("getService() NOT YET IMPLEMENTED");
    }

    @Override
    public void setService(Service s) throws IllegalArgumentException {
        throw new RuntimeException("setService() NOT YET IMPLEMENTED");
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
        // we use this mock load strategy in the testing process, to report on the state of
        //
        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        Set<Class<? extends Operation>> result = new HashSet<>();
        result.add(MockOperation.class);
        return result;
    }

    @Override
    public KeyProvider getKeyProvider() {
        throw new RuntimeException("getKeyProvider() NOT YET IMPLEMENTED");
    }

    @Override
    public void setKeyProvider(KeyProvider keyProvider) {
        throw new RuntimeException("setKeyProvider() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getOperations() {
        throw new RuntimeException("getOperations() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getRemainingOperations() {
        throw new RuntimeException("getRemainingOperations() NOT YET IMPLEMENTED");
    }

    @Override
    public int getValueSize() {
        throw new RuntimeException("getValueSize() NOT YET IMPLEMENTED");
    }

    @Override
    public String getReusedValue() {
        throw new RuntimeException("getReusedValue() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isReuseValue() {
        throw new RuntimeException("isReuseValue() NOT YET IMPLEMENTED");
    }

    @Override
    public String computeValue() {
        throw new RuntimeException("computeValue() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "api (main) MockLoadStrategy";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
