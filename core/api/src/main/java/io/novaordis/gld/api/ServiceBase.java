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

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.version.VersionUtilities;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class ServiceBase implements Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LoadDriver loadDriver;
    private LoadStrategy loadStrategy;

    private static boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * No argument constructor to be used by reflection instantiation of sub-classes
     */
    public ServiceBase() {
    }

    // Service implementation ------------------------------------------------------------------------------------------

    // topology --------------------------------------------------------------------------------------------------------

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

    // lifecycle -------------------------------------------------------------------------------------------------------

    /**
     * @throws IllegalStateException on inconsistent state.
     */
    @Override
    public void start() throws Exception {

        if (started) {

            return;
        }

        if (loadStrategy == null) {

            throw new IllegalStateException("incompletely configured service instance: load strategy not installed");
        }

        //
        // start dependencies
        //

        loadStrategy.start();

        started = true;
    }

    @Override
    public void stop() {

        if (!started) {

            return;
        }

        //
        // stop dependencies
        //

        loadStrategy.stop();

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    /**
     * Uses the VersionUtilities versioning mechanism.
     *
     * @{linktourl https://kb.novaordis.com/index.php/Nova_Ordis_Utilities_Version_Metadata_Handling#Build_Infrastructure_Configuration}
     */
    @Override
    public String getVersion() {

        return VersionUtilities.getVersion();
    }

    // execution -------------------------------------------------------------------------------------------------------

    // to deplete ------------------------------------------------------------------------------------------------------

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

    // topology --------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
