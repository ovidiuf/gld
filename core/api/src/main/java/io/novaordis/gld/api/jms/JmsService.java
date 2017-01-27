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
public interface JmsService extends Service {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Checks out an endpoint, to be used by the load driver to perform an operation.
     */
    JmsEndpoint checkOut(JmsOperation jmsOperation) throws Exception;

    /**
     * Returns the endpoint to the service, to be recycled or closed.
     */
    void checkIn(JmsEndpoint session) throws Exception;

    javax.jms.Destination resolveDestination(Destination d);

    javax.jms.ConnectionFactory resolveConnectionFactory(String connectionFactoryName);


}
