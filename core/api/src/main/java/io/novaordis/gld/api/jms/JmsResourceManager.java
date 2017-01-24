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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

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
 *  Note that this implementation of the JmsResourceManager expects to be accessed within the context of a gld session
 *  and it relies on the fact that deals with MultiThreadedRunner threads and not other threads. If you use it in a
 *  different context, you will need to consider that.
 *
 * @see JmsEndpoint
 */
public class JmsResourceManager {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsResourceManager.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Connection connection;
    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    // <thread name - session instance>
    final private Map<String, Session> sessions;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JmsResourceManager(Connection connection, ConnectionPolicy connectionPolicy, SessionPolicy sessionPolicy) {

        this.connection = connection;
        this.connectionPolicy = connectionPolicy;
        this.sessionPolicy = sessionPolicy;
        this.sessions = new HashMap<>();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public JmsEndpoint checkOutEndpoint(JmsOperation jmsOperation) throws Exception {

        if (connection == null) {

            throw new IllegalStateException(this + " is closed");
        }

        JmsEndpoint result;

//        if (EndpointPolicy.NEW_SESSION_NEW_ENDPOINT_PER_OPERATION.equals(policy)) {
//
//            // create a new Session and a new endpoint
//
//            Session session = createNewSession();
//            result = createNewEndpointForSession(jmsOperation instanceof Send, session, jmsOperation.getDestination());
//        }
//        else if (EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION.equals(policy)) {
//
//            // attempt to look up a previously created session for this thread - if it exists, use it,
//            // if not create it and store it for reuse
//            Session session = getSession();
//            result = createNewEndpointForSession(jmsOperation instanceof Send, session, jmsOperation.getDestination());
//        }
//        else {
//            throw new IllegalStateException(policy + " NOT SUPPORTED YET");
//        }
//
//        return result;

        throw new RuntimeException("RETURN HERE");
    }

    public void returnEndpoint(JmsEndpoint endpoint) throws Exception {

//        // if we attempt to return an endpoint to a closed resource manager, close the endpoint and discard it
//
//        if (connection == null) {
//
//            try {
//
//                endpoint.close();
//            }
//            catch(Exception e) {
//
//                log.warn("failed to close " + e, e);
//            }
//            return;
//        }
//
//        if (EndpointPolicy.NEW_SESSION_NEW_ENDPOINT_PER_OPERATION.equals(policy))
//        {
//            // close the session, we're not going to use it anymore
//            Session session = endpoint.getSession();
//            session.close();
//        }
//        else if (EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION.equals(policy))
//        {
//            // close the endpoint, but leave the session alone, we'll re-use it for anything that gets
//            // send on the same thread
//            endpoint.close();
//        }
//        else {
//
//            throw new IllegalStateException(policy + " NOT SUPPORTED YET");
//        }

        throw new RuntimeException("RETURN HERE");
    }

    /**
     * Clean up.
     */
    public void close() {

        // clean up whatever resources were checked out
        this.connection = null;

        synchronized (sessions) {

            for(Session s: sessions.values()) {

                try {

                    s.close();
                }
                catch(Exception e) {

                    log.warn("failed to close JMS Session " + s, e);
                }
            }

            sessions.clear();
        }
    }

    public ConnectionPolicy getConnectionPolicy() {

        return connectionPolicy;
    }

    public SessionPolicy getSessionPolicy() {

        return sessionPolicy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Attempt to look up a previously created session for this thread - if it exists, use it, if not create it and
     * store it for further reuse.
     */
    private Session getSession() throws Exception {

        // if this thread has already a Session associated with it, use that

        String threadName = Thread.currentThread().getName();

        Session session;

        synchronized (sessions) {

            session = sessions.get(threadName);

            if (session == null) {

                session = createNewSession();
                sessions.put(threadName, session);
            }
        }

        return session;
    }

    private Session createNewSession() throws Exception {

        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * @param send true fro send, false for receive
     */
    private JmsEndpoint createNewEndpointForSession(boolean send, Session session, Destination destination)
        throws Exception {

        JmsEndpoint result;

        String name = destination.getName();
        javax.jms.Destination jmsDestination =
            destination.isQueue() ? session.createQueue(name) : session.createTopic(name);

        if (send) {

            MessageProducer producer = session.createProducer(jmsDestination);
            result = new Producer(producer, session);
        }
        else {

            MessageConsumer consumer = session.createConsumer(jmsDestination);
            result = new Consumer(consumer, session);
        }

        return result;
    }

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

}
