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
import com.novaordis.gld.Operation;
import com.novaordis.gld.Service;
import com.novaordis.gld.operations.jms.JmsOperation;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveMQService implements Service
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static String toBrokerUrl(List<Node> nodes)
    {
        if (nodes == null)
        {
            throw new IllegalArgumentException("null node list");
        }

        if (nodes.isEmpty())
        {
            throw new IllegalArgumentException("empty node list");
        }

        if (nodes.size() > 1)
        {
            throw new RuntimeException("NOT YET IMPLEMENTED");
        }

        Node n = nodes.get(0);

        return "tcp://" + n.getHost() + ":" + n.getPort();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Node> nodes;

    private Connection connection;

    private final Map<String, Session> sessions;

    private Configuration configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ActiveMQService(Configuration configuration, List<Node> nodes)
    {
        this.nodes = nodes;
        this.configuration = configuration;
        this.sessions = new HashMap<>();
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception
    {
        if (connection != null)
        {
            throw new IllegalStateException(this + " already started");
        }

        ConnectionFactory cf = getConnectionFactory();

        String username = configuration.getUsername();

        if (username == null)
        {
            connection = cf.createConnection();
        }
        else
        {
            connection = cf.createConnection(username, configuration.getPassword());

        }
        connection.start();
    }

    @Override
    public void stop() throws Exception
    {
        if (connection != null)
        {
            connection.stop();
            connection.close();
            connection = null;
        }
    }

    /**
     * "started" semantics in an ActiveMQService context means the service initialized and started a JMS connection
     * to the node(s). Once we create a connection, we automatically start it, so the connection existence test is
     * good enough.
     */
    @Override
    public boolean isStarted()
    {
        return connection != null;
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        if (!(o instanceof JmsOperation))
        {
            throw new IllegalArgumentException(o + " is not a JMS operation");
        }

        JmsOperation jmsOp = (JmsOperation)o;

        // lookup the corresponding session
        Session s = getSession();

        jmsOp.perform(s);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "ActiveMQService" + nodes;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private ConnectionFactory getConnectionFactory()
    {
        if (nodes != null && !nodes.isEmpty() && nodes.get(0) instanceof EmbeddedNode)
        {
            return new EmbeddedConnectionFactory((EmbeddedNode)nodes.get(0));
        }

        String brokerUrl = toBrokerUrl(nodes);
        return new ActiveMQConnectionFactory(brokerUrl);
    }

    private Session getSession() throws Exception
    {
        // if this thread has already a Session associated with it, use that

        String threadName = Thread.currentThread().getName();

        Session session;

        synchronized (sessions)
        {
            session = sessions.get(threadName);

            if (session == null)
            {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                sessions.put(threadName, session);
            }
        }

        return session;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
