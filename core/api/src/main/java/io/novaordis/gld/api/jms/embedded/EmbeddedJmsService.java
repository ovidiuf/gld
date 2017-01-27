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

package io.novaordis.gld.api.jms.embedded;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JmsServiceBase;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/25/17
 */
public class EmbeddedJmsService extends JmsServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EmbeddedJmsService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, EmbeddedConnectionFactory> connectionFactories;
    private Map<String, EmbeddedDestination> destinations;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedJmsService() {

        this.connectionFactories = new HashMap<>();
        this.destinations = new HashMap<>();
    }

    // JmsService implementation ---------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        super.configure(serviceConfiguration);

        log.debug(this + " configured");
    }

    @Override
    public javax.jms.Destination resolveDestination(Destination d) {

        //
        // for the time being, any destination "exists"
        //

        EmbeddedDestination ed = destinations.get(d.getName());

        if (ed != null) {

            if (ed instanceof javax.jms.Queue && d instanceof Topic ||
                    ed instanceof javax.jms.Topic && d instanceof Queue) {

                throw new IllegalArgumentException(
                        "destination " + d.getName() + " exists but it is a " +
                                (ed instanceof javax.jms.Queue ? "queue" : "topic"));
            }
        }
        else {

            ed = d.isQueue() ? new EmbeddedQueue(d.getName()) : new EmbeddedTopic(d.getName());
            destinations.put(d.getName(), ed);
        }

        return ed;
    }

    @Override
    public ConnectionFactory resolveConnectionFactory(String connectionFactoryName) {

        //
        // for the time being, any connection factory "exists"
        //

        EmbeddedConnectionFactory cf = null;

        if ((cf = connectionFactories.get(connectionFactoryName)) == null) {

            cf = new EmbeddedConnectionFactory();
            connectionFactories.put(connectionFactoryName, cf);
        }

        return cf;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return an empty list if there were no message sent, or the queue does not exist.
     */
    public List<Message> getMessagesSentToDestination(String destinationName, boolean queue) throws Exception {

        List<Message> result = new ArrayList<>();

        EmbeddedConnectionFactory cf = (EmbeddedConnectionFactory)getConnectionFactory();
        result.addAll(cf.getMessagesSentToDestination(destinationName, queue));

        return result;
    }

    public void addToDestination(String name, boolean queue, Message m) {

        EmbeddedDestination d = destinations.get(name);

        if (d == null) {

            d = queue ? new EmbeddedQueue(name) : new EmbeddedTopic(name);
            destinations.put(name, d);

        }
        else {

            if (d instanceof Queue && !queue || d instanceof Topic && queue) {

                throw new IllegalArgumentException(d + " exists and it is a " + d.getClass().getSimpleName());
            }
        }

        d.add(m);
    }

    @Override
    public String toString() {

        return "EmbeddedJmsService[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------


    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
