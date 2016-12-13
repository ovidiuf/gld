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

import io.novaordis.gld.api.configuration.Configuration;
import io.novaordis.gld.api.sampler.Sampler;

/**
 * @{linktourl https://kb.novaordis.com/index.php/Gld_Concepts#Load_Driver_Instance}
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public interface LoadDriver {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // identity --------------------------------------------------------------------------------------------------------

    /**
     * Each load driver member of a cluster must have an unique ID.
     */
    String getID();

    // lifecycle -------------------------------------------------------------------------------------------------------

    /**
     * Initialize the load driver and dependencies (the service and auxiliary components), based on the Configuration
     * passed to it.
     */
    void init(Configuration c) throws Exception;

    /**
     * @return true if the load driver is configured to run as a background process and does not interact directly
     * with stdin/stdout/stderr. False if the load driver is configured to run in foreground and can be controlled
     * directly from the console. The default behavior is to run in background.
     */
    boolean background();

    /**
     * The main LoadDriver instance loop.
     *
     * The implementation should start all installed lifecycle components, which were previously initialized by init()
     * and then it should start sending load into the target service.
     *
     * It is run()'s responsibility to stop() (or at least attempt to stop all dependent services) at the end of the
     * run, irregardless of whether the run was successful or not.
     *
     * @exception Exception if anything goes wrong with the dependent lifecycle components or with the run itself.
     * By the moment the Exception emerges from run(), the dependent services should be stopped already.
     */
    void run() throws Exception;

    //
    // topology --------------------------------------------------------------------------------------------------------
    //

    /**
     * A LoadDriver instance can only be associated with a service at a time.
     */
    Service getService();

    /**
     * @return the KeyStore instance. May be null if the keys are not stored.
     */
    KeyStore getKeyStore();

    Runner getRunner();

    Sampler getSampler();

    //
    // logging ---------------------------------------------------------------------------------------------------------
    //

    /**
     * Logs the exception, in a human readable format, to the error stream.
     * The details debug logging must be done by the calling layer.
     */
    void error(Throwable t);

    /**
     * Logs the message (which should be in a human readable format), to the error stream.
     * The details debug logging must be done by the calling layer.
     */
    void error(String msg);
}
