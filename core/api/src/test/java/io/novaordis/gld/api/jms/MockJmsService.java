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

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.embedded.EmbeddedConnectionFactory;
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.embedded.EmbeddedTopic;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public class MockJmsService extends JmsServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, EmbeddedQueue> queues;
    private Map<String, EmbeddedTopic> topics;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockJmsService() {

        this.queues = new HashMap<>();
        this.topics = new HashMap<>();
    }

    // JmsServiceBase overrides ----------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {
        throw new NotYetImplementedException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public javax.jms.Destination resolveDestination(Destination d) {

        //
        // we resolve any destination
        //

        String name = d.getName();

        javax.jms.Destination jmsDestination;

        if (d.isQueue()) {

            jmsDestination = queues.get(d.getName());

            if (jmsDestination == null) {

                jmsDestination = new EmbeddedQueue(name);
                queues.put(name, (EmbeddedQueue)jmsDestination);
            }
        }
        else {

            jmsDestination = topics.get(d.getName());

            if (jmsDestination == null) {

                jmsDestination = new EmbeddedTopic(name);
                topics.put(name, (EmbeddedTopic)jmsDestination);
            }
        }

        return jmsDestination;
    }

    @Override
    public ConnectionFactory resolveConnectionFactory(String connectionFactoryName) {

        return new EmbeddedConnectionFactory();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setConnectionPolicy(ConnectionPolicy cp) {

        super.setConnectionPolicy(cp);
    }

    public void setSessionPolicy(SessionPolicy sp) {

        super.setSessionPolicy(sp);
    }

    public void setConnection(Connection connection) {

        super.setConnection(connection);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
