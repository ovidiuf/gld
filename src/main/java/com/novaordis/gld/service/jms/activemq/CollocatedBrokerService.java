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
import com.novaordis.gld.ContentType;
import com.novaordis.gld.Node;
import com.novaordis.gld.Operation;
import com.novaordis.gld.operations.jms.JmsOperation;
import com.novaordis.utilities.Files;
import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * A service that spins up an entire collocated ActiveMQ browser and establishes a network bridge (or multiple
 * network bridges, depending on configuration) to the target broker. Can be used for sending or receiving
 * depending on the directionality of the bridge (or both for duplex bridges).
 *
 * The broker is started, managed and used over a Spring wrapper.
 *
 * This service configures gld to wait for an explicit console quit, as messages may need to propagate through the
 * collocated broker.
 *
 * @see Configuration#waitForConsoleQuit()
 *
 */
public class CollocatedBrokerService implements com.novaordis.gld.Service
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CollocatedBrokerService.class);

    private static final File LOCAL_DIRECTORY = new File("/tmp/gld");

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;
    private List<Node> nodes;
    private JmsTemplate jmsTemplate;


    // Constructors ----------------------------------------------------------------------------------------------------

    public CollocatedBrokerService()
    {
        log.debug(this + " created");
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public ContentType getContentType()
    {
        return ContentType.JMS;
    }

    @Override
    public void setConfiguration(Configuration c)
    {
        this.configuration = c;
    }

    @Override
    public void setTarget(List<Node> nodes)
    {
        this.nodes = nodes;
    }

    /**
     * @see com.novaordis.gld.Service#start()
     * @throws Exception
     */
    @Override
    public void start() throws Exception
    {
        if (jmsTemplate != null)
        {
            throw new IllegalStateException(this + " already started");
        }

        if (LOCAL_DIRECTORY.isDirectory())
        {
            // remove it and re-created it
            if (!Files.rmdir(LOCAL_DIRECTORY, true))
            {
                throw new IllegalStateException("failed to delete " + LOCAL_DIRECTORY);
            }

            log.info(LOCAL_DIRECTORY + " deleted");
        }

        if (Files.mkdir(LOCAL_DIRECTORY))
        {
            log.info(LOCAL_DIRECTORY + " created");
        }
        else
        {
            throw new IllegalStateException("failed to create " + LOCAL_DIRECTORY);
        }

        System.setProperty("embedded.gld.activemq.directory", LOCAL_DIRECTORY.getAbsolutePath());
        System.setProperty("embedded.gld.activemq.brokerid", "0");

        // tcp://b01:61616,tcp://b02:61616,tcp://b03:61616
        String delegateBrokerList = "";
        for(Iterator<Node> i = nodes.iterator(); i.hasNext(); )
        {
            Node n = i.next();
            delegateBrokerList += "tcp://" + n.getHost() + ":" + n.getPort();
            if (i.hasNext())
            {
                delegateBrokerList += ",";
            }
        }

        log.info("delegate broker list: " + delegateBrokerList);
        System.setProperty("embedded.gld.activemq.masterSlaveBrokerList", delegateBrokerList);

        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load("classpath:amq-embedded-broker/application-context.xml");
        context.refresh();

        jmsTemplate = (JmsTemplate)context.getBean("jmsTemplate");

        //
        // we configure the runtime to wait for explicit console quit, as messages may need to propagate
        // through the system after sending them to the collocated broker
        //

        log.debug("configuring the runtime to wait for explicit console quit");
        configuration.setWaitForConsoleQuit(true);

        log.debug(this + " started");
    }

    @Override
    public void stop() throws Exception
    {
        if (jmsTemplate == null)
        {
            return;
        }

        jmsTemplate = null;

        // TODO we don't delete the LOCAL_DIRECTORY just yet

        log.debug(this + " stopped");
    }

    @Override
    public boolean isStarted()
    {
        return jmsTemplate != null;
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        if (!isStarted())
        {
            throw new IllegalArgumentException(this + " not started");
        }

        if (!(o instanceof JmsOperation))
        {
            throw new IllegalArgumentException(o + " is not a JMS operation");
        }

        log.debug(this + " performing " + o);

        // figure what session to use based on the current policy in place
        final JmsOperation jmsOperation = (JmsOperation)o;
        String destinationName = jmsOperation.getDestination().getName();

        jmsTemplate.send(destinationName, new MessageCreator()
        {
            @Override
            public Message createMessage(final Session session) throws JMSException
            {
                return session.createTextMessage();
            }
        });
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "Embedded ActiveMQ Broker CollocatedBrokerService" + nodes;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
