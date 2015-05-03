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

package com.novaordis.gld.service.jms.activemq;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.EmbeddedNode;
import com.novaordis.gld.Node;
import com.novaordis.gld.ServiceTest;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.service.jms.EndpointPolicy;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnection;
import com.novaordis.gld.service.jms.embedded.EmbeddedMessageProducer;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ActiveMQServiceTest extends ServiceTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ActiveMQServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // toClientUrl - single node ---------------------------------------------------------------------------------------

    @Test
    public void toClientUrl_nullNode() throws Exception
    {
        try
        {
            ActiveMQService.toClientUrl((Node)null);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toClientUrl_OneNode_EmbeddedNode() throws Exception
    {
        String s = ActiveMQService.toClientUrl(new EmbeddedNode());
        log.info(s);
        assertTrue(ActiveMQService.isEmbedded(s));
    }

    @Test
    public void toClientUrl_OneNode_RegularNode() throws Exception
    {
        String s = ActiveMQService.toClientUrl(new Node("localhost", 10101));
        log.info(s);
        assertEquals("tcp://localhost:10101", s);
    }

    // toClientUrl - multiple nodes ------------------------------------------------------------------------------------

    @Test
    public void toClientUrl_nullList() throws Exception
    {
        try
        {
            ActiveMQService.toClientUrl((List<Node>)null);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toClientUrl_emptyList() throws Exception
    {
        try
        {
            ActiveMQService.toClientUrl(new ArrayList<Node>());
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toClientUrl_OneEmbeddedNode() throws Exception
    {
        String s = ActiveMQService.toClientUrl(Arrays.<Node>asList(new EmbeddedNode()));
        log.info(s);
        assertTrue(ActiveMQService.isEmbedded(s));
    }

    @Test
    public void toClientUrl_TwoEmbeddedNode() throws Exception
    {
        String s = ActiveMQService.toClientUrl(Arrays.<Node>asList(new EmbeddedNode(), new EmbeddedNode()));
        log.info(s);
        assertTrue(ActiveMQService.isEmbedded(s));
    }

    @Test
    public void toClientUrl_OneNode() throws Exception
    {
        List<Node> nodes = Node.toNodeList("localhost:61616");
        String s = ActiveMQService.toClientUrl(nodes);
        assertEquals("tcp://localhost:61616", s);
    }

    @Test
    public void toClientUrl_TwoNodes() throws Exception
    {
        List<Node> nodes = Node.toNodeList("localhost:61616,example.com:11111");
        String s = ActiveMQService.toClientUrl(nodes);
        assertEquals("failover:(tcp://localhost:61616,tcp://example.com:11111)", s);
    }

    // service lifecycle -----------------------------------------------------------------------------------------------

    @Test
    public void lifeCycle() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        mc.setLoadStrategy(loadStrategy);

        ActiveMQService service = getServiceToTest(mc, Arrays.asList((Node)new EmbeddedNode()));

        service.start();

        EmbeddedConnection connection = (EmbeddedConnection)service.getConnection();

        assertNotNull(connection);

        service.stop();

        // second stop is supposed to be a noop
        service.stop();
    }

    // endpoint policy -------------------------------------------------------------------------------------------------

    @Test
    public void endpointPolicy_NEW_SESSION_AND_ENDPOINT_PER_OPERATION() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        new Load(mc, Collections.<String>emptyList(), 0);

        // test for send

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test", "--endpoint-strategy", "blah"));
        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.configure(mc, args, 0);
        mc.setLoadStrategy(loadStrategy);

        ActiveMQService service = getServiceToTest(mc, Arrays.asList((Node)new EmbeddedNode()));

        service.start();

        assertEquals(EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION, loadStrategy.getEndpointPolicy());

        Send send = (Send)loadStrategy.next(null, null);

        // make sure no sessions were created at this point
        EmbeddedConnection c = (EmbeddedConnection)service.getConnection();
        assertTrue(c.getCreatedSessions().isEmpty());

        send.perform(service);

        // make sure that the only session created is still running
        List<EmbeddedSession> sessions = c.getCreatedSessions();
        assertEquals(1, sessions.size());
        EmbeddedSession s = sessions.get(0);
        assertFalse(s.isClosed());

        service.stop();

        // make sure that the only session created was closed
        sessions = c.getCreatedSessions();
        assertEquals(1, sessions.size());
        s = sessions.get(0);
        assertTrue(s.isClosed());
    }

    @Test
    public void endpointPolicy_defaultBehavior_REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        new Load(mc, Collections.<String>emptyList(), 0);

        // test for send

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));
        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.configure(mc, args, 0);
        mc.setLoadStrategy(loadStrategy);

        assertEquals(EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION, loadStrategy.getEndpointPolicy());

        ActiveMQService service = getServiceToTest(mc, Arrays.asList((Node)new EmbeddedNode()));

        service.start();

        // make sure no sessions were created at this point
        EmbeddedConnection c = (EmbeddedConnection)service.getConnection();
        assertTrue(c.getCreatedSessions().isEmpty());

        Send send = (Send)loadStrategy.next(null, null);

        send.perform(service);

        // make sure a session was created and its active, but the endpoint was closed
        List<EmbeddedSession> sessions = c.getCreatedSessions();
        assertEquals(1, sessions.size());
        EmbeddedSession s = sessions.get(0);
        assertFalse(s.isClosed());

        List<EmbeddedMessageProducer> createdMessageProducers = s.getCreatedProducers();
        assertEquals(1, createdMessageProducers.size());
        EmbeddedMessageProducer messageProducer = createdMessageProducers.get(0);
        assertTrue(messageProducer.isClosed());

        // perform another operation, make sure we reuse the same session, but a different producer, that
        // is closed after the operation

        Send send2 = (Send)loadStrategy.next(null, null);

        send2.perform(service);

        List<EmbeddedSession> sessions2 = c.getCreatedSessions();
        assertEquals(1, sessions2.size());
        EmbeddedSession s2 = sessions2.get(0);
        assertFalse(s2.isClosed());
        assertEquals(s, s2);

        List<EmbeddedMessageProducer> createdMessageProducers2 = s2.getCreatedProducers();
        assertEquals(2, createdMessageProducers2.size());
        messageProducer = createdMessageProducers2.get(0);
        assertTrue(messageProducer.isClosed());
        EmbeddedMessageProducer messageProducer2 = createdMessageProducers2.get(1);
        assertTrue(messageProducer2.isClosed());

        service.stop();

        // make sure the session is closed

        assertTrue(s.isClosed());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected ActiveMQService getServiceToTest(Configuration configuration, List<Node> nodes) throws Exception
    {
        return new ActiveMQService(configuration, nodes);
    }

    @Override
    protected Node getTestNode()
    {
        return new EmbeddedNode();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
