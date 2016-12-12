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

package io.novaordis.gld.api.configuration;

import io.novaordis.utilities.UserErrorException;

/**
 * Typed access to the configuration that specifies the load characteristics (number of threads, duration, number
 * of requests/operations/messages), etc.
 *
 * The implementations of this interface also allow low-level typed access (typed access to specific points into the
 * configuration structure) via LowLevelConfiguration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public interface LoadConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String THREAD_COUNT_LABEL = "threads";
    int DEFAULT_THREAD_COUNT = 1;

    String OPERATION_COUNT_LABEL = "operations";
    String REQUEST_COUNT_LABEL = "requests";
    String MESSAGE_COUNT_LABEL = "messages";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Typed Access ----------------------------------------------------------------------------------------------------

    int getThreadCount() throws UserErrorException;

    /**
     * @return the number of operations to be created and applied to the target service during the load run. null means
     * "unlimited". It is semantically equivalent with getRequests() and getMessages(), it can be used interchangeably,
     * all these methods will return the same value.
     *
     * @see LoadConfiguration#getOperations()
     * @see LoadConfiguration#getRequests()
     * @see LoadConfiguration#getMessages()
     */
    Long getOperations() throws UserErrorException;

    /**
     * @return the number of requests to be sent into the target service during the load run. null means "unlimited".
     * It is semantically equivalent with getOperations() and getMessages(), it can be used interchangeably,
     * all these methods will return the same value.
     *
     * @see LoadConfiguration#getOperations()
     * @see LoadConfiguration#getRequests()
     * @see LoadConfiguration#getMessages()
     */
    Long getRequests() throws UserErrorException;

    /**
     * @return the number of messages to be sent into the target service during the load run. null means "unlimited".
     * It is semantically equivalent with getOperations() and getRequests(), it can be used interchangeably,
     * all these methods will return the same value.
     *
     * @see LoadConfiguration#getOperations()
     * @see LoadConfiguration#getRequests()
     * @see LoadConfiguration#getMessages()
     */
    Long getMessages() throws UserErrorException;

    // Untyped Access --------------------------------------------------------------------------------------------------

}
