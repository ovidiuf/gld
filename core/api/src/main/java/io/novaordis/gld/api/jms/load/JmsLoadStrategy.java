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

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.jms.Destination;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public interface JmsLoadStrategy extends LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    Destination getDestination();

    /**
     * @return the connection policy employed by this load strategy. The default is CONNECTION_PER_RUN, which means
     * that one connection is created at the beginning of the load run and that connection is used throughout the test.
     */
    ConnectionPolicy getConnectionPolicy();

    /**
     * @return the session policy employed by this load strategy. The default is SESSION_PER_OPERATION, which means
     * that one session is created for each operation, and then discarded.
     */
    SessionPolicy getSessionPolicy();


}
