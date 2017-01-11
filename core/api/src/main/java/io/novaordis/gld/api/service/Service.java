/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * The abstract representation of a target gld can send load to. A service is characterized by its type (cache,  jms,
 * http, etc).
 *
 * @see ServiceType
 */
public interface Service {

    // topology --------------------------------------------------------------------------------------------------------

    LoadDriver getLoadDriver();

    /**
     * Needed to configure instances created via the no-argument constructor.
     */
    void setLoadDriver(LoadDriver d);

    LoadStrategy getLoadStrategy();

    /**
     * Needed to configure instances created via the no-argument constructor.
     */
    void setLoadStrategy(LoadStrategy s);

    // lifecycle -------------------------------------------------------------------------------------------------------

    /**
     * Initialize internal state based on the external service configuration.
     */
    void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException;

    /**
     * If the internal lifecycle components are installed, the will be started recursively.
     *
     * Starting an already started service should be a noop.
     *
     * @throws IllegalStateException on attempt to start an incompletely configured service (for example, a service
     * without a load strategy) or a service with an inconsistent state.
     *
     * @throws UserErrorException contains a human readable message. Thrown on incomplete command line configuration,
     * or any error condition that can be addressed with user intervention.
     */
    void start() throws Exception;

    /**
     * It stops the service and the internal lifecycle components (load strategy and its components), etc.
     *
     * Stopping an already stopped service instance should be a noop.
     */
    void stop() throws Exception;

    boolean isStarted();

    // accessors -------------------------------------------------------------------------------------------------------

    ServiceType getType();

    String getVersion();

}
