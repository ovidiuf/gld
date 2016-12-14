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
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.operations.jms.Send;
import io.novaordis.gld.api.Service;
import io.novaordis.utilities.Files;
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
 * Required Configuration Parameters (the service won't work without it):
 *
 * "--memoryUsage 5GB": the broker memory limit.
 *
 * Optional Configuration Parameters
 *
 * "--directory <local-directory>": the local directory collocated broker persists to. If not specified, the default
 *     value is /tmp/gld.
 *
 * "--delete-directory-at-boot": if specified, the local directory is cleaned before each GLD instance startup. The
 *     default behavior is to do nothing to the directory if exists (and create if it does not exists).
 *
 * "--broker-id <broker-id>: Optional, the default value if not specified is "gld.0".
 *
 * @see Configuration#waitForConsoleQuit()
 *
 */
public class CollocatedBrokerService implements Service
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CollocatedBrokerService.class);
    private static final boolean trace = log.isTraceEnabled();

    public static final String DEFAULT_LOCAL_DIRECTORY = "/tmp/gld";
    public static final String DEFAULT_BROKER_ID = "gld.0";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;
    private List<Node> nodes;

    private GenericXmlApplicationContext context;
    private JmsTemplate jmsTemplate;

    //
    // Collocated broker configuration
    //

    // memoryUsage value, will go straight into the configuration.
    private String memoryUsage;
    private String directory;
    private boolean deleteDirectoryAtBoot;
    private String brokerId;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CollocatedBrokerService()
    {
        // set default values
        directory = DEFAULT_LOCAL_DIRECTORY;
        deleteDirectoryAtBoot = false;
        brokerId = DEFAULT_BROKER_ID;

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
     * @see Service#configure(List)
     */
    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException
    {
        for(int i = 0; i < commandLineArguments.size(); i ++)
        {
            String arg = commandLineArguments.get(i);

            if ("--memoryUsage".equals(arg))
            {
                if (i == commandLineArguments.size() - 1)
                {
                    throw new UserErrorException("a value must follow after --memoryUsage");
                }

                commandLineArguments.remove(i);
                memoryUsage = commandLineArguments.remove(i--);
                memoryUsage = ActiveMQConfigurationUtil.sanitizeMemoryUsage(memoryUsage);
            }
            else if ("--directory".equals(arg))
            {
                if (i == commandLineArguments.size() - 1)
                {
                    throw new UserErrorException("a value must follow after --directory");
                }

                commandLineArguments.remove(i);
                directory = commandLineArguments.remove(i--);
            }
            else if ("--delete-directory-at-boot".equals(arg))
            {
                commandLineArguments.remove(i--);
                deleteDirectoryAtBoot = true;
            }
            else if ("--broker-id".equals(arg))
            {
                if (i == commandLineArguments.size() - 1)
                {
                    throw new UserErrorException("a value must follow after --broker-id");
                }

                commandLineArguments.remove(i);
                brokerId = commandLineArguments.remove(i--);
            }
        }
    }

    /**
     * @see Service#start()
     *
     * @throws Exception
     * @throws UserErrorException contains a human readable message. Thrown on incomplete command line configuration.
     */
    @Override
    public void start() throws Exception
    {
        if (jmsTemplate != null)
        {
            throw new IllegalStateException(this + " already started");
        }

        verifyAndSetBrokerConfiguration();

        context = new GenericXmlApplicationContext();
        context.load("classpath:collocated-amq-broker/application-context.xml");
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

        context.stop();
        context = null;

        // we don't delete the associated directory just yet, we may need logs, etc.
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
            throw new IllegalStateException(this + " not started");
        }

        if (!(o instanceof Send))
        {
            throw new IllegalArgumentException(o + " is not a Send operation");
        }

        Send send = (Send)o;
        final String payload = send.getPayload();

        if (trace) { log.trace(this + " performing " + send); }

        // figure what session to use based on the current policy in place
        String destinationName = send.getDestination().getName();

        jmsTemplate.send(destinationName, new MessageCreator()
        {
            @Override
            public Message createMessage(final Session session) throws JMSException
            {
                if (payload == null)
                {
                    return session.createTextMessage();
                }
                else
                {
                    return session.createTextMessage(payload);
                }

            }
        });
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getMemoryUsage()
    {
        return memoryUsage;
    }

    public String getDirectory()
    {
        return directory;
    }

    public boolean isDeleteDirectoryAtBoot()
    {
        return deleteDirectoryAtBoot;
    }

    public String getBrokerId()
    {
        return brokerId;
    }

    @Override
    public String toString()
    {
        return "Embedded ActiveMQ Broker CollocatedBrokerService " + nodes;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void verifyAndSetBrokerConfiguration() throws UserErrorException
    {
        if (nodes == null || nodes.isEmpty())
        {
            throw new UserErrorException("no target nodes specified");
        }

        // tcp://b01:61616,tcp://b02:61616,tcp://b03:61616
        String targetBrokers = "";
        for(Iterator<Node> i = nodes.iterator(); i.hasNext(); )
        {
            Node n = i.next();
            targetBrokers += "tcp://" + n.getHost() + ":" + n.getPort();
            if (i.hasNext())
            {
                targetBrokers += ",";
            }
        }

        log.debug("target brokers: " + targetBrokers);
        System.setProperty("gld.collocated.activemq.broker.target.nodes", targetBrokers);

        if (memoryUsage == null)
        {
            throw new UserErrorException(
                "missing required ActiveMQ memoryUsage setting, set it with --memoryUsage <numeric_value><unit> (example --memoryUsage 5GB)");
        }

        System.setProperty("gld.collocated.activemq.broker.memoryUsage", memoryUsage);

        File d = new File(getDirectory());

        if (d.isDirectory() && isDeleteDirectoryAtBoot())
        {
            // remove it and re-created it
            if (!Files.rmdir(d, true))
            {
                throw new UserErrorException("failed to delete " + d.getPath());
            }

            log.info(d.getPath() + " deleted");
        }

        if (!d.isDirectory())
        {
            // create the directory
            if (Files.mkdir(d))
            {
                log.info(d.getPath() + " created");
            }
            else
            {
                throw new UserErrorException("failed to create " + d.getPath());
            }
        }

        System.setProperty("gld.collocated.activemq.broker.directory", d.getPath());
        System.setProperty("gld.collocated.activemq.broker.id", getBrokerId());
    }

// Inner classes ---------------------------------------------------------------------------------------------------

}
