/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.ErrorCodes;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.jms.operation.Receive;
import io.novaordis.gld.api.jms.operation.Send;
import io.novaordis.gld.api.service.ServiceBase;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.lang.IllegalStateException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public abstract class JmsServiceBase extends ServiceBase implements JmsService {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsServiceBase.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    private String connectionFactoryName;
    private ConnectionFactory connectionFactory;

    //
    // the only connection per service when the connection policy is ConnectionPolicy.CONNECTION_PER_RUN
    //
    private Connection connection;

    //
    // the structures that associate sessions to threads, if SessionPolicy.SESSION_PER_THREAD is in effect; will
    // be initialized by setSessionPolicy();
    //
    private final Object sessionMutex = new Object();
    private Map<Thread, Session> threadsToSessions;
    private Map<Session, Thread> sessionsToThreads;

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsService implementation and overrides -------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return ServiceType.jms;
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        if (!(serviceConfiguration instanceof JmsServiceConfiguration)) {

            throw new IllegalArgumentException("invalid JMS service configuration " + serviceConfiguration);
        }

        //
        // TODO this is convoluted and ... wrong. We must not do anything related to the load strategy, because
        // later code will extract configuration when building the load strategy instance. We need to review this
        // See setLoadStrategy(LoadStrategy s) below.
    }

    /**
     * We intercept the method that installs the load strategy to get some configuration from it.
     */
    @Override
    public void setLoadStrategy(LoadStrategy s) {

        if (!(s instanceof JmsLoadStrategy)) {

            throw new IllegalArgumentException("invalid load strategy " + s);
        }

        super.setLoadStrategy(s);

        JmsLoadStrategy jmsLoadStrategy = (JmsLoadStrategy)s;

        setConnectionPolicy(jmsLoadStrategy.getConnectionPolicy());
        setSessionPolicy(jmsLoadStrategy.getSessionPolicy());
        setConnectionFactoryName(jmsLoadStrategy.getConnectionFactoryName());
    }

    @Override
    public void start() throws Exception {

        super.start();

        synchronized (this) {

            //
            // TODO this implies ConnectionPolicy.CONNECTION_PER_RUN, this code will need to be reviewed
            //

            if (connection != null) {

                //
                // already started
                //

                log.debug(this + " already started");

                return;
            }

            //
            // look up the ConnectionFactory
            //

            connectionFactory = resolveConnectionFactory(connectionFactoryName);
            Connection c  = connectionFactory.createConnection();
            setConnection(c);
        }
    }

    @Override
    public boolean isStarted() {

        synchronized (this) {

            return connection != null;
        }
    }

    @Override
    public void stop() {

        super.stop();

        synchronized (this) {

            //
            // TODO this implies ConnectionPolicy.CONNECTION_PER_RUN, this code will need to be reviewed
            //

            if (connection == null) {

                return;
            }

            try {

                connection.stop();
            }
            catch(Exception e) {

                log.warn("failed to stop connection", e);

            }

            connection = null;
        }
    }


    @Override
    public JmsEndpoint checkOut(JmsOperation jmsOperation) throws Exception {

        Connection connection = getConnection();

        Session session = getSession(connection);

        JmsEndpoint endpoint;

        javax.jms.Destination d = resolveDestination(jmsOperation.getDestination());

        if (jmsOperation instanceof Send) {

            MessageProducer jmsProducer = session.createProducer(d);
            endpoint = new Producer(jmsProducer, session, connection);

        }
        else if (jmsOperation instanceof Receive) {

            MessageConsumer jmsConsumer = session.createConsumer(d);
            endpoint = new Consumer(jmsConsumer, session, connection);
        }
        else {

            throw new IllegalArgumentException("unknown JMS operation " + jmsOperation);
        }

        log.debug("created " + endpoint);
        return endpoint;
    }

    @Override
    public void checkIn(JmsEndpoint endpoint) throws Exception {

        //
        // look at the session policy and handle accordingly
        //

        if (SessionPolicy.SESSION_PER_OPERATION.equals(sessionPolicy)) {

            //
            // done with it, close
            //

            Session session = endpoint.getSession();
            session.close();
        }
        else if (SessionPolicy.SESSION_PER_THREAD.equals(sessionPolicy)) {

            //
            // look for an association with the thread and fail if the endpoint is returned from a different thread
            //

            Thread thread = Thread.currentThread();
            Session endpointSession = endpoint.getSession();

            synchronized (sessionMutex) {

                Session session = threadsToSessions.get(thread);

                if (session == null) {

                    //
                    // consistency check, make sure the session is not associated with any other thread
                    //

                    Thread thread2 = sessionsToThreads.get(endpointSession);

                    if (thread2 != null) {

                        throw new IllegalStateException(
                                "session " + endpointSession + " was checked out by " + thread2 + " but is being checked in from a different thread " + thread);
                    }

                    //
                    // no session associated with this thread, and our session not associated with any other thread
                    //

                    throw new IllegalArgumentException("no session associated with " + thread);
                }

                //
                // we find a different session that our endpoint's session
                //

                if (!session.equals(endpointSession)) {

                    throw new IllegalArgumentException(
                            "the session associated with " + thread + " is different from the endpoint session");
                }

                //
                // session match, we're good, leave the session alone but close the endpoint
                //

                endpoint.close();
            }
        }
        else {

            throw new RuntimeException(sessionPolicy + " SUPPORT NOT YET IMPLEMENTED");
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null if the service was not started or the start was not successful.
     */
    public ConnectionFactory getConnectionFactory() {

        return connectionFactory;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    Connection getConnection() throws Exception {

        if (ConnectionPolicy.CONNECTION_PER_RUN.equals(connectionPolicy)) {

            //
            // we use the only one connection per service, created when the service is started
            //

            return connection;
        }
        else {

            throw new RuntimeException("WE DON'T KNOW HOW TO HANDLE " + connectionPolicy);
        }
    }

    Session getSession(Connection connection) throws Exception {

        if (SessionPolicy.SESSION_PER_OPERATION.equals(sessionPolicy)) {

            //
            // we use the only one connection per service, created when the service is started
            //

            return createSession(connection);
        }
        else if (SessionPolicy.SESSION_PER_THREAD.equals(sessionPolicy)) {

            //
            // each thread has an associated session
            //

            Thread thread = Thread.currentThread();
            Session session;

            synchronized (sessionMutex) {

                session = threadsToSessions.get(thread);

                if (session == null) {

                    session = createSession(connection);

                    threadsToSessions.put(thread, session);

                    log.debug("session " + session + " created and associated with " + thread);

                    Thread oldThread = sessionsToThreads.put(session, thread);

                    if (oldThread != null) {

                        //
                        // this can't happen
                        //
                        throw new IllegalStateException(ErrorCodes.GLD_10001.toString());
                    }
                }
            }

            return session;
        }
        else {

            throw new RuntimeException("WE DON'T KNOW HOW TO HANDLE " + sessionPolicy);
        }
    }

    /**
     * Always creates a new session.
     */
    Session createSession(Connection connection) throws Exception {

        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConnectionPolicy(ConnectionPolicy cp) {

        this.connectionPolicy = cp;
    }

    protected SessionPolicy getSessionPolicy() {

        return this.sessionPolicy;
    }

    protected void setSessionPolicy(SessionPolicy sp) {

        this.sessionPolicy = sp;

        if (SessionPolicy.SESSION_PER_THREAD.equals(sp)) {

            //
            // reset and initialize data structures associated with the SESSION_PER_THREAD policy
            //

            threadsToSessions = new HashMap<>();
            sessionsToThreads = new HashMap<>();
        }
    }

    protected void setConnection(Connection c) {

        this.connection = c;
    }

    protected void setConnectionFactoryName(String s) {

        this.connectionFactoryName = s;
    }

    protected String getConnectionFactoryName() {

        return connectionFactoryName;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
