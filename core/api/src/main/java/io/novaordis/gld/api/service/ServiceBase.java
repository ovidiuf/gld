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

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.utilities.version.VersionUtilities;

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

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * No argument constructor to be used by reflection instantiation of sub-classes
     */
    public ServiceBase() {

        this.started = false;
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

        if (isStarted()) {

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

        if (!isStarted()) {

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

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
