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
import com.novaordis.gld.service.jms.embedded.EmbeddedMessageConsumer;
import com.novaordis.gld.service.jms.embedded.EmbeddedMessageProducer;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.Queue;
import com.novaordis.gld.strategy.load.jms.ReceiveLoadStrategy;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import com.novaordis.gld.strategy.load.jms.Topic;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JmsResourceManagerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsResourceManagerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void testReactionOnClosedManager() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager =
            new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_NEW_ENDPOINT_PER_OPERATION);

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

    // NEW_SESSION_NEW_ENDPOINT_PER_OPERATION --------------------------------------------------------------------------

    @Test
    public void test_NEW_SESSION_NEW_ENDPOINT_PER_OPERATION_send_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager =
            new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_NEW_ENDPOINT_PER_OPERATION);

        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
        Send send = new Send(loadStrategy);

        Producer endpoint = (Producer)manager.checkOutEndpoint(send);

        // check that the corresponding Session and MessageProducer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
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
    public void test_NEW_SESSION_NEW_ENDPOINT_PER_OPERATION_receive_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();
        JmsResourceManager manager =
            new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_NEW_ENDPOINT_PER_OPERATION);

        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        loadStrategy.setDestination(new Topic("TEST-TOPIC"));
        Receive receive = new Receive(loadStrategy);

        Consumer endpoint = (Consumer)manager.checkOutEndpoint(receive);

        // check that the corresponding Session and MessageConsumer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedMessageConsumer> createdConsumers = createdSessions.get(0).getCreatedConsumers();
        assertEquals(1, createdConsumers.size());
        assertEquals(endpoint.getConsumer(), createdConsumers.get(0));

        assertFalse(session.isClosed());

        manager.returnEndpoint(endpoint);

        // check that the corresponding Session and MessageProducer have been released

        assertTrue(session.isClosed());

        // we trust that the message producer is closed, and they're all released because the manager
        // does not keep references to them
    }

    // REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION ------------------------------------------------------------------------

    @Test
    public void test_REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION_send_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();

        JmsResourceManager manager =
            new JmsResourceManager(connection, EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION);

        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
        Send send = new Send(loadStrategy);

        Producer endpoint = (Producer)manager.checkOutEndpoint(send);

        // check that the corresponding Session and MessageProducer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
        assertEquals(1, createdProducers.size());
        EmbeddedMessageProducer messageProducer = createdProducers.get(0);
        assertEquals(endpoint.getProducer(), messageProducer);

        assertFalse(session.isClosed());
        assertFalse(messageProducer.isClosed());

        // return the endpoint

        manager.returnEndpoint(endpoint);

        // check that the corresponding MessageProducer have been released and closed

        assertTrue(messageProducer.isClosed());

        // check that the corresponding session is still around and opened - it was cached for reuse on the
        // same thread

        assertFalse(session.isClosed());

        // check out another endpoint - make sure we get a different one, but on the same session

        Producer endpoint2 = (Producer)manager.checkOutEndpoint(send);

        // check that the corresponding Session and MessageProducer have been created

        List<EmbeddedSession> createdSessions2 = connection.getCreatedSessions();
        assertEquals(1, createdSessions2.size());
        EmbeddedSession session2 = createdSessions2.get(0);
        assertEquals(endpoint2.getSession(), session2);

        // make sure it's the same session
        assertEquals(endpoint2.getSession(), session);

        List<EmbeddedMessageProducer> createdProducers2 = session2.getCreatedProducers();
        assertEquals(2, createdProducers2.size());
        EmbeddedMessageProducer messageProducer2 = createdProducers2.get(1);
        assertEquals(endpoint2.getProducer(), messageProducer2);

        assertFalse(session2.isClosed());
        assertFalse(messageProducer2.isClosed());

        // return the endpoint

        manager.returnEndpoint(endpoint2);

        // check that the corresponding MessageProducer have been released and closed

        assertTrue(messageProducer2.isClosed());

        // check that the corresponding session is still around and opened - it was cached for reuse on the
        // same thread

        assertFalse(session2.isClosed());

        // make sure the session is closed when we close the manager

        manager.close();

        assertTrue(session.isClosed());
        assertTrue(session2.isClosed());
    }

    @Test
    public void test_REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION_receive_lifecycle() throws Exception
    {
        EmbeddedConnection connection = new EmbeddedConnection();

        JmsResourceManager manager =
            new JmsResourceManager(connection, EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION);

        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
        Receive receive = new Receive(loadStrategy);

        Consumer endpoint = (Consumer)manager.checkOutEndpoint(receive);

        // check that the corresponding Session and MessageProducer have been created

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());
        EmbeddedSession session = createdSessions.get(0);
        assertEquals(endpoint.getSession(), session);

        List<EmbeddedMessageConsumer> createdConsumers = createdSessions.get(0).getCreatedConsumers();
        assertEquals(1, createdConsumers.size());
        EmbeddedMessageConsumer messageConsumer = createdConsumers.get(0);
        assertEquals(endpoint.getConsumer(), messageConsumer);

        assertFalse(session.isClosed());
        assertFalse(messageConsumer.isClosed());

        // return the endpoint

        manager.returnEndpoint(endpoint);

        // check that the corresponding MessageConsumer have been released and closed

        assertTrue(messageConsumer.isClosed());

        // check that the corresponding session is still around and opened - it was cached for reuse on the
        // same thread

        assertFalse(session.isClosed());

        // check out another endpoint - make sure we get a different one, but on the same session

        Consumer endpoint2 = (Consumer)manager.checkOutEndpoint(receive);

        // check that the corresponding Session and MessageConsumer have been created

        List<EmbeddedSession> createdSessions2 = connection.getCreatedSessions();
        assertEquals(1, createdSessions2.size());
        EmbeddedSession session2 = createdSessions2.get(0);
        assertEquals(endpoint2.getSession(), session2);

        // make sure it's the same session
        assertEquals(endpoint2.getSession(), session);

        List<EmbeddedMessageConsumer> createdConsumers2 = session2.getCreatedConsumers();
        assertEquals(2, createdConsumers2.size());
        EmbeddedMessageConsumer messageConsumer2 = createdConsumers2.get(1);
        assertEquals(endpoint2.getConsumer(), messageConsumer2);

        assertFalse(session2.isClosed());
        assertFalse(messageConsumer2.isClosed());

        // return the endpoint

        manager.returnEndpoint(endpoint2);

        // check that the corresponding MessageConsumer have been released and closed

        assertTrue(messageConsumer2.isClosed());

        // check that the corresponding session is still around and opened - it was cached for reuse on the
        // same thread

        assertFalse(session2.isClosed());

        // make sure the session is closed when we close the manager

        manager.close();

        assertTrue(session.isClosed());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
