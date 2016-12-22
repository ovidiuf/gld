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

package io.novaordis.gld.api;

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.utilities.UserErrorException;

import java.util.List;

/**
 *
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
     * If the internal lifecycle components are installed, the will be started recursively.
     *
     * Starting an already started service should throw IllegalStateException.
     *
     * @throws IllegalStateException on attempt to start an already started service instance.
     * @throws IllegalStateException on attempt to start an incompletely configured service (for example, a service
     * without a load strategy) or a service with an inconsistent state.
     * @throws UserErrorException contains a human readable message. Thrown on incomplete command line configuration.
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

    // to deplete ------------------------------------------------------------------------------------------------------

    void setConfiguration(Configuration c);

    void setTarget(List<Node> nodes);

    /**
     * @param commandLineArguments command line arguments (whatever is left after the upper layer processes upper level
     *                             arguments) that may contain configuration relevant to the service. The service is
     *                             supposed to recognize them, extract them configure itself with them and remove them
     *                             from the list. Arguments relevant to the service will be removed from the list when
     *                             the method is done.
     *
     * @exception UserErrorException on invalid data specified by user. Contains a human readable message.
     */
    void configure(List<String> commandLineArguments) throws UserErrorException;

    ContentType getContentType();

}
