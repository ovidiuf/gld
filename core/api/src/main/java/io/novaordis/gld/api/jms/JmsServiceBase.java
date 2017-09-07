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
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
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
 * A base implementation of a JMS service. Most JMS Service implementations, unless they have special needs, should
 * inherit from this class.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public abstract class JMSServiceBase extends ServiceBase implements JMSService {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JMSServiceBase.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    private String connectionFactoryName;
    private ConnectionFactory connectionFactory;

    private String username; // may be null
    private char[] password; // may be null

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

    // JMSService implementation and overrides -------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return ServiceType.jms;
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        if (!(serviceConfiguration instanceof JMSServiceConfiguration)) {

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

        if (!(s instanceof JMSLoadStrategy)) {

            throw new IllegalArgumentException("invalid load strategy " + s);
        }

        super.setLoadStrategy(s);

        JMSLoadStrategy jmsLoadStrategy = (JMSLoadStrategy)s;

        setConnectionPolicy(jmsLoadStrategy.getConnectionPolicy());
        setSessionPolicy(jmsLoadStrategy.getSessionPolicy());
        setConnectionFactoryName(jmsLoadStrategy.getConnectionFactoryName());
        setUsername(jmsLoadStrategy.getUsername());
        setPassword(jmsLoadStrategy.getPassword());
    }

    @Override
    public void start() throws Exception {

        synchronized (this) {

            //
            // TODO this implies ConnectionPolicy.CONNECTION_PER_RUN, this code will need to be reviewed
            //

            if (!ConnectionPolicy.CONNECTION_PER_RUN.equals(connectionPolicy)) {

                throw new RuntimeException("WE DON'T KNOW HOW TO HANDLE " + connectionPolicy);
            }

            if (connection != null) {

                //
                // already started
                //

                log.debug(this + " already started");

                return;
            }

            //
            // after possibly starting the subclass layer, we need to start this one to build and install JMS objects.
            //

            log.debug("starting JMS service base of " + this);

            //
            // look up the ConnectionFactory
            //

            connectionFactory = resolveConnectionFactory(connectionFactoryName);

            if (connectionFactory == null) {

                throw new UserErrorException("connection factory " + connectionFactoryName + " not bound in JNDI");
            }

            Connection c;

            String username = getUsername();

            try {

                if (username != null) {

                    c = connectionFactory.createConnection(username, getPassword());
                }
                else {

                    c = connectionFactory.createConnection();
                }

                log.debug("connection " + c + " created");

                c.start();

                log.debug("connection " + c + " started");
            }
            catch(Exception e) {

                //
                // wrap JMS exceptions into UserErrorExceptions for friendlier reporting
                //

                throw new UserErrorException(e);
            }

            setConnection(c);
        }

        super.start();
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

        log.debug("stopping JMS service base of " + this);

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
    public JMSEndpoint checkOut(JmsOperation jmsOperation) throws Exception {

        Connection connection = getConnection();

        Session session = getSession(connection);

        JMSEndpoint endpoint;

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
    public void checkIn(JMSEndpoint endpoint) throws Exception {

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

        log.debug(this + " installed " + this.connectionPolicy);
    }

    protected void setUsername(String username) {

        this.username = username;
    }

    /**
     * May return null.
     */
    protected String getUsername() {

        return username;
    }

    protected void setPassword(char[] p) {

        this.password = p;
    }

    /**
     * May return null.
     */
    protected String getPassword() {

        if (password == null) {

            return null;
        }

        return new String(password);
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

        log.debug(this + " installed connection " + c);
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
