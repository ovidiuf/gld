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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.service.Service;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public interface JMSService extends Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Checks out an endpoint, to be used by the load driver to perform an operation.
     */
    JMSEndpoint checkOut(JmsOperation jmsOperation) throws JMSServiceException;

    /**
     * Returns the endpoint to the service, to be recycled or closed.
     *
     * Note that for a thread sensitive connection or session policy, an endpoint cannot be checked in from a thread
     * different than the one that checked the endpoint out. If this situation is detected, an IllegalStateException
     * will be thrown.
     *
     * @exception IllegalStateException if a thread-sensitive connection or session policy is in effect, and the
     * endpoint is returned by a different thread than the one that checked it out.
     *
     * @exception IllegalArgumentException if we are attempting to check in an endpoint for which we don't find
     * the associated structures, in case SessionPolicy.SESSION_PER_THREAD or ConnectionPolicy.CONNECTION_PER_THREAD are
     * in effect.
     */
    void checkIn(JMSEndpoint session) throws JMSServiceException;

    /**
     * @return the JMS Destination instance corresponding to given destination, or null if the Destination is not found.
     *
     * @exception IllegalArgumentException on null destination.
     * @exception JMSServiceException on any other underlying naming failure.
     */
    javax.jms.Destination resolveDestination(Destination d) throws JMSServiceException;

    /**
     * @return the ConnectionFactory instance corresponding to the name, or null if the ConnectionFactory is not found.
     *
     * @exception IllegalArgumentException on null connection factory name
     * @exception JMSServiceException on any other underlying naming failure.
     */
    javax.jms.ConnectionFactory resolveConnectionFactory(String connectionFactoryName) throws JMSServiceException;
}
