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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.jms.embedded.EmbeddedConnection;
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.embedded.EmbeddedSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class JMSEndpointTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JMSEndpointTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // session ---------------------------------------------------------------------------------------------------------

    @Test
    public void session() throws Exception {

        EmbeddedConnection connection = new EmbeddedConnection();
        EmbeddedSession session = new EmbeddedSession(connection, 0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedQueue queue = new EmbeddedQueue("test");

        JMSEndpoint e = getEndpointToTest(queue, session, connection);

        assertEquals(session, e.getSession());
    }

    // connection ---------------------------------------------------------------------------------------------------------

    @Test
    public void connection() throws Exception {

        EmbeddedConnection connection = new EmbeddedConnection();
        EmbeddedSession session = new EmbeddedSession(connection, 0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedQueue queue = new EmbeddedQueue("test");

        JMSEndpointBase e = (JMSEndpointBase)getEndpointToTest(queue, session, connection);

        assertEquals(connection, e.getConnection());
    }

    // close -----------------------------------------------------------------------------------------------------------

    @Test
    public void closeDoesNotCloseSession() throws Exception {

        EmbeddedConnection connection = new EmbeddedConnection();
        EmbeddedSession session = new EmbeddedSession(connection, 0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedQueue queue = new EmbeddedQueue("test");

        JMSEndpoint e = getEndpointToTest(queue, session, connection);

        e.close();

        assertEquals(session, e.getSession());

        assertFalse(session.isClosed());

        log.debug(".");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract JMSEndpoint getEndpointToTest(
            javax.jms.Destination jmsDestination, Session session, Connection connection) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
