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

package io.novaordis.gld.api;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class MockService implements Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LoadStrategy loadStrategy;
    private LoadDriver loadDriver;

    private ServiceType type;

    // Constructors ----------------------------------------------------------------------------------------------------

    //
    // we're mock by default but we can change that
    //
    public MockService() {

        this(ServiceType.mock);
    }

    public MockService(ServiceType st) {

        this.type = st;
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public LoadDriver getLoadDriver() {

        return loadDriver;
    }

    @Override
    public void setLoadDriver(LoadDriver d) {

        this.loadDriver = d;
    }

    @Override
    public LoadStrategy getLoadStrategy() {

        return loadStrategy;
    }

    @Override
    public void setLoadStrategy(LoadStrategy s) {

        this.loadStrategy = s;
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {
        throw new NotYetImplementedException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception {
        throw new RuntimeException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() throws Exception {
        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted() {
        throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public ServiceType getType() {

        return type;
    }

    @Override
    public String getVersion() {
        throw new NotYetImplementedException("getVersion() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Simulate a mock service with a different type
     */
    public void setServiceType(ServiceType t) {

        this.type = t;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
