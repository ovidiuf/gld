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

import io.novaordis.gld.api.todiscard.*;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.utilities.UserErrorException;

import java.util.List;

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

    // Constructors ----------------------------------------------------------------------------------------------------

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

        return ServiceType.mock;
    }

    @Override
    public void setConfiguration(Configuration c) {
        throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes) {
        throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException {
        throw new RuntimeException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public ContentType getContentType() {
        throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
