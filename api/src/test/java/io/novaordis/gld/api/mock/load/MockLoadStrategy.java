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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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

    private boolean initialized;

    // Constructors ----------------------------------------------------------------------------------------------------

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
        throw new RuntimeException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {
        throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() {
        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {
        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {
        throw new RuntimeException("getOperationTypes() NOT YET IMPLEMENTED");
    }

    @Override
    public KeyProvider getKeyProvider() {
        throw new RuntimeException("getKeyProvider() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
