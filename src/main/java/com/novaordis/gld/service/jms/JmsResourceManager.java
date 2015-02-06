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

import com.novaordis.gld.operations.jms.JmsOperation;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.strategy.load.jms.Destination;
import org.apache.log4j.Logger;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * The purpose of this class is to encapsulate the logic that dictates of to reuse JMS resources (Sessions,
 * MessageProducers and MessageConsumers) across requests. By default, a new Session and respectively a MessageProducer
 * and MessageConsumer instance are created for *each* message, then closed after the operation. Different variations
 * can be externally configured (use the same Session for all messages sent/received on the same thread, use the
 * same MessageProducer/MessageConsumer for all messages sent/received on the same thread, etc.
 *
 * The usage pattern is:
 *
 *  JmsEndpoint jmse = manager.checkOutEndpoint();
 *
 *  try
 *  {
 *      jmsOperation.perform(jmse);
 *  }
 *  finally
 *  {
 *     manager.returnEndpoint(jmse);
 *  }
 *
 *  There is a one-to-one relationship between a JMS Connection and a JmsResourceManager instance:
 *  the JmsResourceManager is created after its connection is created and torn down when the association connection
 *  is closed.
 *
 * @see com.novaordis.gld.service.jms.JmsEndpoint
 */
public class JmsResourceManager
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(JmsResourceManager.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Connection connection;
    private EndpointPolicy policy;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JmsResourceManager(Connection connection, EndpointPolicy policy)
    {
        this.connection = connection;
        this.policy = policy;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public JmsEndpoint checkOutEndpoint(JmsOperation jmsOperation) throws Exception
    {
        if (connection == null)
        {
            throw new IllegalStateException(this + " is closed");
        }

        if (EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION.equals(policy))
        {
            // create a new Session and a new endpoint

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = jmsOperation.getDestination();
            String name = destination.getName();
            javax.jms.Destination jmsDestination =
                destination.isQueue() ? session.createQueue(name) : session.createTopic(name);

            if (jmsOperation instanceof Send)
            {
                MessageProducer producer = session.createProducer(jmsDestination);
                return new Producer(producer, session);
            }
            else
            {
                MessageConsumer consumer = session.createConsumer(jmsDestination);
                return new Consumer(consumer, session);
            }
        }
        else
        {
            throw new IllegalStateException(policy + " NOT SUPPORTED YET");
        }
    }

    public void returnEndpoint(JmsEndpoint endpoint) throws Exception
    {
        // if we attempt to return an endpoint to a closed resource manager, close the endpoint and discard it

        if (connection == null)
        {
            try
            {
                endpoint.close();
            }
            catch(Exception e)
            {
                log.warn("failed to close " + e, e);
            }
            return;
        }

        if (EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION.equals(policy))
        {
            // close the session, we're not going to use it anymore
            Session session = endpoint.getSession();
            session.close();
        }
        else
        {
            throw new IllegalStateException(policy + " NOT SUPPORTED YET");
        }
    }

    /**
     * Clean up.
     */
    public void close()
    {
        // clean up whatever resources were checked out
        this.connection = null;
    }

    public EndpointPolicy getPolicy()
    {
        return policy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

//
//    JmsLoadStrategy jmsLoadStrategy = jmsOp.getLoadStrategy();
//    boolean sessionPerOperation = false; // jmsLoadStrategy.isSessionPerOperation();
//
//    // lookup the corresponding session
//    Session s = getSession(sessionPerOperation);
//
//    try
//    {
//        jmsOp.perform(s);
//    }
//    finally
//    {
//        // if it's sessionPerOperation, close the session after we've performed the operation
//        if (sessionPerOperation)
//        {
//            s.close();
//        }
//    }


//    private Session getSession(boolean sessionPerOperation) throws Exception
//    {
//        if (sessionPerOperation)
//        {
//            return createNewSession();
//        }
//
//        // if this thread has already a Session associated with it, use that
//
//        String threadName = Thread.currentThread().getName();
//
//        Session session;
//
//        synchronized (sessions)
//        {
//            session = sessions.get(threadName);
//
//            if (session == null)
//            {
//                session = createNewSession();
//                sessions.put(threadName, session);
//            }
//        }
//
//        return session;
//    }
//
//    private Session createNewSession() throws Exception
//    {
//        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//    }



}
