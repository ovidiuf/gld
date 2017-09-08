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

package io.novaordis.gld.api.jms.embedded;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedConnectionFactory implements ConnectionFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<EmbeddedConnection> createdConnections;

    private String authorizedUser;
    private String authorizedUserPassword;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedConnectionFactory() {

        this(null, null);
    }

    /**
     * @param authorizedUser may be null, in which case anonymous connections are allowed.
     */
    public EmbeddedConnectionFactory(String authorizedUser, String authorizedPassword) {

        this.authorizedUser = authorizedUser;
        this.authorizedUserPassword = authorizedPassword;
        this.createdConnections = new ArrayList<>();
    }

    // ConnectionFactory implementation --------------------------------------------------------------------------------

    @Override
    public Connection createConnection() throws JMSException {

        return createConnection(null, null);
    }

    @Override
    public Connection createConnection(String username, String password) throws JMSException {

        if (username != null) {

            if (!username.equals(authorizedUser) || !password.equals(authorizedUserPassword)) {

                throw new JMSException("AUTHENTICATION FAILURE");
            }
        }

        EmbeddedConnection c = new EmbeddedConnection(username);
        createdConnections.add(c);
        return c;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return an empty list if there were no message sent, or the queue does not exist.
     */
    public List<Message> getMessagesSentToDestination(String destinationName, boolean queue) throws Exception {

        List<Message> messages = new ArrayList<>();

        for(EmbeddedConnection c: createdConnections) {

            messages.addAll(c.getMessagesSentToDestination(destinationName, queue));
        }

        return messages;

    }
    @Override
    public String toString() {

        return "EmbeddedJMSConnectionFactory[" + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
