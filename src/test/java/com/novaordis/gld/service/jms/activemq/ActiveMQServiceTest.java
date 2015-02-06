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
import com.novaordis.gld.service.jms.EndpointPolicy;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnection;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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

    // service lifecycle -----------------------------------------------------------------------------------------------

    @Test
    public void lifeCycle() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
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
    public void defaultBehavior_NEW_SESSION_AND_ENDPOINT_PER_OPERATION() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        ActiveMQService service = getServiceToTest(mc, Arrays.asList((Node)new EmbeddedNode()));

        service.start();

        // test for send

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));
        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.configure(mc, args, 0);

        assertEquals(EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION, loadStrategy.getEndpointPolicy());

        Send send = (Send)loadStrategy.next(null, null);

        // make sure no sessions were created at this point
        EmbeddedConnection c = (EmbeddedConnection)service.getConnection();
        assertTrue(c.getCreatedSessions().isEmpty());

        send.perform(service);

        // make sure that the only session created was closed
        List<EmbeddedSession> sessions = c.getCreatedSessions();
        assertEquals(1, sessions.size());
        EmbeddedSession s = sessions.get(0);
        assertTrue(s.isClosed());

        service.stop();

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
