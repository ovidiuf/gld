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
import com.novaordis.gld.service.jms.EndpointPolicy;
import com.novaordis.gld.service.jms.JmsEndpoint;
import com.novaordis.gld.service.jms.JmsResourceManager;
import com.novaordis.gld.service.jms.embedded.EmbeddedConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import java.util.List;

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

    private Configuration configuration;

    private JmsResourceManager resourceManager;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ActiveMQService(Configuration configuration, List<Node> nodes)
    {
        this.nodes = nodes;
        this.configuration = configuration;
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
        resourceManager = new JmsResourceManager(connection, EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION);
    }

    @Override
    public void stop() throws Exception
    {
        if (connection != null)
        {
            resourceManager.close();
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
        if (!isStarted())
        {
            throw new IllegalStateException(this + " not started");
        }

        if (!(o instanceof JmsOperation))
        {
            throw new IllegalArgumentException(o + " is not a JMS operation");
        }

        // figure what session to use based on the current policy in place
        JmsOperation jmsOperation = (JmsOperation)o;

        JmsEndpoint endpoint = resourceManager.checkOutEndpoint(jmsOperation);

        try
        {
            jmsOperation.perform(endpoint);
        }
        finally
        {
            resourceManager.returnEndpoint(endpoint);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Connection getConnection()
    {
        return connection;
    }

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

    // Inner classes ---------------------------------------------------------------------------------------------------

}
