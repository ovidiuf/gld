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

package com.novaordis.gld.service.jms;

import com.novaordis.gld.mock.MockJmsEndpoint;
import com.novaordis.gld.operations.jms.Receive;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnection;
import com.novaordis.gld.service.jms.embedded.EmbeddedConsumer;
import com.novaordis.gld.service.jms.embedded.EmbeddedProducer;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.Queue;
import com.novaordis.gld.strategy.load.jms.ReceiveLoadStrategy;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import com.novaordis.gld.strategy.load.jms.Topic;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JmsResourceManagerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(JmsResourceManagerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void testReactionOnClosedManager() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager = new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION);

        manager.close();

        Send send = new Send(new SendLoadStrategy());

        try
        {
            manager.checkOutEndpoint(send);
            fail("should fail with IllegalStateException, manager is closed");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        // check how a closed manager reacts to returning an endpoint

        MockJmsEndpoint mockEndpoint = new MockJmsEndpoint();
        assertFalse(mockEndpoint.isClosed());

        manager.returnEndpoint(mockEndpoint);

        assertTrue(mockEndpoint.isClosed());
    }

    @Test
    public void test_ENDPOINT_PER_OPERATION_send_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager = new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION);

        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
        Send send = new Send(loadStrategy);

        Producer endpoint = (Producer)manager.checkOutEndpoint(send);

        // check that the corresponding Session and MessageProducer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
        assertEquals(1, createdProducers.size());
        assertEquals(endpoint.getProducer(), createdProducers.get(0));

        assertFalse(session.isClosed());

        manager.returnEndpoint(endpoint);

        // check that the corresponding Session and MessageProducer have been released

        assertTrue(session.isClosed());

        // we trust that the message producer is closed, and they're all released because the manager
        // does not keep references to them
    }

    @Test
    public void test_ENDPOINT_PER_OPERATION_receive_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager = new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION);

        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        loadStrategy.setDestination(new Topic("TEST-TOPIC"));
        Receive receive = new Receive(loadStrategy);

        Consumer endpoint = (Consumer)manager.checkOutEndpoint(receive);

        // check that the corresponding Session and MessageConsumer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedConsumer> createdConsumers = createdSessions.get(0).getCreatedConsumers();
        assertEquals(1, createdConsumers.size());
        assertEquals(endpoint.getConsumer(), createdConsumers.get(0));

        assertFalse(session.isClosed());

        manager.returnEndpoint(endpoint);

        // check that the corresponding Session and MessageProducer have been released

        assertTrue(session.isClosed());

        // we trust that the message producer is closed, and they're all released because the manager
        // does not keep references to them
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
