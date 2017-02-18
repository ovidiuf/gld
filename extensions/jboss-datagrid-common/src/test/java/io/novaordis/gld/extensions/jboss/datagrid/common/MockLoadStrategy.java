/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.NotYetImplementedException;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class MockLoadStrategy implements LoadStrategy {


    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockLoadStrategy() {

        this.started = false;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void init(ServiceConfiguration serviceConfiguration, LoadConfiguration loadConfiguration) throws Exception {
        throw new NotYetImplementedException("init() NOT YET IMPLEMENTED");
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
    public String getName() {
        throw new NotYetImplementedException("getName() NOT YET IMPLEMENTED");
    }

    @Override
    public ServiceType getServiceType() {
        throw new NotYetImplementedException("getServiceType() NOT YET IMPLEMENTED");
    }

    @Override
    public Service getService() {
        throw new NotYetImplementedException("getService() NOT YET IMPLEMENTED");
    }

    @Override
    public void setService(Service s) throws IllegalArgumentException {
        throw new NotYetImplementedException("setService() NOT YET IMPLEMENTED");
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean shuttingDown) throws Exception {
        throw new NotYetImplementedException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {
        throw new NotYetImplementedException("getOperationTypes() NOT YET IMPLEMENTED");
    }

    @Override
    public KeyProvider getKeyProvider() {
        throw new NotYetImplementedException("getKeyProvider() NOT YET IMPLEMENTED");
    }

    @Override
    public void setKeyProvider(KeyProvider keyProvider) {
        throw new NotYetImplementedException("setKeyProvider() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getRemainingOperations() {
        throw new RuntimeException("getRemainingOperations() NOT YET IMPLEMENTED");
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
