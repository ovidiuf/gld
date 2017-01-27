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

import javax.jms.*;
import javax.jms.ConnectionFactory;

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
        this.connectionFactoryName = jmsLoadStrategy.getConnectionFactoryName();
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
    public void checkIn(JmsEndpoint session) throws Exception {

        //
        // look at the session policy and handle accordingly
        //

        if (SessionPolicy.SESSION_PER_OPERATION.equals(sessionPolicy)) {

            //
            // done with it, close
            //

            session.close();
        }
        else if (SessionPolicy.SESSION_PER_THREAD.equals(sessionPolicy)) {

            //
            // get the session from the thread, and if not available, create one
            //

            throw new RuntimeException("NOT YET IMPLEMENTED");
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

            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        else {

            throw new RuntimeException("WE DON'T KNOW HOW TO HANDLE " + sessionPolicy);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConnectionPolicy(ConnectionPolicy cp) {

        this.connectionPolicy = cp;
    }

    protected void setSessionPolicy(SessionPolicy sp) {

        this.sessionPolicy = sp;
    }

    protected void setConnection(Connection c) {

        this.connection = c;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
