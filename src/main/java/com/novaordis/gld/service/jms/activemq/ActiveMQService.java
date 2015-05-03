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
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import java.util.Iterator;
import java.util.List;

public class ActiveMQService implements Service
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static String toClientUrl(List<Node> nodes)
    {
        if (nodes == null)
        {
            throw new IllegalArgumentException("null node list");
        }

        if (nodes.isEmpty())
        {
            throw new IllegalArgumentException("empty node list");
        }

        if (nodes.size() == 1)
        {
            return toClientUrl(nodes.get(0));

        }
        else
        {
            // multiple nodes, we generate a "failover:()" URL
            StringBuilder sb = new StringBuilder("failover:(");
            for(Iterator<Node> i = nodes.iterator(); i.hasNext(); )
            {
                sb.append(toClientUrl(i.next()));

                if (i.hasNext())
                {
                    sb.append(",");
                }

            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static String toClientUrl(Node node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("null node");
        }

        if (node instanceof EmbeddedNode)
        {
            return EmbeddedNode.EMBEDDED_LABEL.toLowerCase() + "://";
        }
        else
        {
            return  "tcp://" + node.getHost() + ":" + node.getPort();
        }
    }

    public static boolean isEmbedded(String clientUrl)
    {
        if (clientUrl == null)
        {
            return false;
        }

        if (clientUrl.startsWith("failover:("))
        {
            clientUrl = clientUrl.substring("failover:(".length());
        }

        return clientUrl.startsWith(EmbeddedNode.EMBEDDED_LABEL.toLowerCase() + "://");
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String clientUrl;

    private Connection connection;

    private Configuration configuration;

    private JmsResourceManager resourceManager;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ActiveMQService(Configuration configuration, List<Node> nodes)
    {
        this.clientUrl = toClientUrl(nodes);
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

        // we get our endpoint policy from the load strategy; it can only be a JMS load strategy

        JmsLoadStrategy loadStrategy = (JmsLoadStrategy)configuration.getLoadStrategy();
        EndpointPolicy endpointPolicy = loadStrategy.getEndpointPolicy();
        resourceManager = new JmsResourceManager(connection, endpointPolicy);
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
        return "ActiveMQService[" + clientUrl + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private ConnectionFactory getConnectionFactory()
    {
        if (isEmbedded(clientUrl))
        {
            return new EmbeddedConnectionFactory(clientUrl);
        }
        else
        {
            return new ActiveMQConnectionFactory(clientUrl);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
