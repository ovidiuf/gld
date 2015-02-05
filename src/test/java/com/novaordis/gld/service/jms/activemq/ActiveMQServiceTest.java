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
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnection;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.DefaultJmsLoadStrategy;
import com.novaordis.gld.strategy.load.jms.Queue;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    // toBrokerUrl -----------------------------------------------------------------------------------------------------

    @Test
    public void toBrokerUrl_nullList() throws Exception
    {
        try
        {
            ActiveMQService.toBrokerUrl(null);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toBrokerUrl_emptyList() throws Exception
    {
        try
        {
            ActiveMQService.toBrokerUrl(new ArrayList<Node>());
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toBrokerUrl() throws Exception
    {
        List<Node> nodes = Node.toNodeList("localhost:61616");
        String s = ActiveMQService.toBrokerUrl(nodes);
        assertEquals("tcp://localhost:61616", s);
    }

    // session-per-operation behavior ----------------------------------------------------------------------------------

    @Test
    public void sessionPerOperation_makeSureSessionsAreClosedAfterUse() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        ActiveMQService s = getServiceToTest(mc, Arrays.asList((Node)new EmbeddedNode()));

        s.start();

        EmbeddedConnection embeddedConnection = (EmbeddedConnection)s.getConnection();

        assertTrue(embeddedConnection.getCreatedSessions().isEmpty());

        DefaultJmsLoadStrategy jmsLoadStrategy = new DefaultJmsLoadStrategy();
        jmsLoadStrategy.setDestination(new Queue("TEST"));
        jmsLoadStrategy.setSessionPerOperation(true);

        Send send = new Send(jmsLoadStrategy);

        s.perform(send);

        // make sure the only created session is closed
        List<EmbeddedSession> sessions = embeddedConnection.getCreatedSessions();
        assertEquals(1, sessions.size());
        assertTrue(sessions.get(0).isClosed());

        s.stop();
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
