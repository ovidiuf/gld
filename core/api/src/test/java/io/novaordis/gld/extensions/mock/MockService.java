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

package io.novaordis.gld.extensions.mock;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class MockService implements Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;
    private ServiceConfiguration serviceConfiguration;
    private LoadStrategy loadStrategy;
    private LoadDriver loadDriver;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockService() {

        this.started = false;
        this.serviceConfiguration = null;
        this.loadStrategy = null;
        this.loadDriver = null;
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public LoadDriver getLoadDriver() {

        return loadDriver;
    }

    @Override
    public void setLoadDriver(LoadDriver d) {

        throw new NotYetImplementedException("setLoadDriver() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadStrategy getLoadStrategy() {

        return loadStrategy;
    }

    @Override
    public void setLoadStrategy(LoadStrategy s) {
        throw new NotYetImplementedException("setLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public void start() throws Exception {

        this.started = true;
    }

    @Override
    public void stop() throws Exception {
        throw new NotYetImplementedException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public ServiceType getType() {

        return ServiceType.mock;
    }

    @Override
    public String getVersion() {
        throw new NotYetImplementedException("getVersion() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null if we weren't configured yet.
     */
    public ServiceConfiguration getConfigurationInstanceWeWereConfiguredWith() {

        return serviceConfiguration;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
