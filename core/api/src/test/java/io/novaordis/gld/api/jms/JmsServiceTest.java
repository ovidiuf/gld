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
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.embedded.EmbeddedConnection;
import io.novaordis.gld.api.jms.embedded.EmbeddedConnectionFactory;
import io.novaordis.gld.api.jms.embedded.EmbeddedJmsService;
import io.novaordis.gld.api.jms.embedded.EmbeddedMessageProducer;
import io.novaordis.gld.api.jms.embedded.EmbeddedSession;
import io.novaordis.gld.api.jms.embedded.EmbeddedTextMessage;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.jms.operation.MockSend;
import io.novaordis.gld.api.service.ServiceTest;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public abstract class JmsServiceTest extends ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getServiceType() throws Exception {

        JmsService s = getServiceToTest();
        assertEquals(ServiceType.jms, s.getType());
    }

    //
    // configure() -----------------------------------------------------------------------------------------------------
    //

    @Test
    public void configure_NotTheRightTypeOfServiceConfiguration() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();

        MockServiceConfiguration msc = new MockServiceConfiguration();

        try {

            s.configure(msc);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("invalid JMS service configuration"));
        }
    }

    //
    // ConnectionPolicy.CONNECTION_PER_RUN -----------------------------------------------------------------------------
    //

    @Test
    public void ConnectionPolicy_CONNECTION_PER_RUN_lifecycle() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy();
        s.setLoadStrategy(mls);

        Connection c = s.getConnection();

        assertNull(c);

        s.start();

        c = s.getConnection();

        Connection c2 = s.getConnection();

        assertTrue(c == c2);

        s.stop();

        c = s.getConnection();

        assertNull(c);
    }

    @Test
    public void ConnectionPolicy_CONNECTION_PER_RUN_BehaviorOnCheckInCheckOut() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy(new Queue("mock-queue"));
        s.setLoadStrategy(mls);

        s.start();

        MockSend mo = new MockSend(mls);

        JmsEndpointBase endpoint = (JmsEndpointBase)s.checkOut(mo);
        Connection c = endpoint.getConnection();
        s.checkIn(endpoint);

        MockSend mo2 = new MockSend(mls);

        JmsEndpointBase endpoint2 = (JmsEndpointBase)s.checkOut(mo2);
        Connection c2 = endpoint2.getConnection();
        s.checkIn(endpoint2);

        assertTrue(c == c2);
    }

    //
    // SessionPolicy.SESSION_PER_OPERATION -----------------------------------------------------------------------------
    //

    @Test
    public void SessionPolicy_SESSION_PER_OPERATION_lifecycle() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy();
        s.setLoadStrategy(mls);

        assertEquals(SessionPolicy.SESSION_PER_OPERATION, mls.getSessionPolicy());

        EmbeddedConnection connection = new EmbeddedConnection();

        Session session = s.getSession(connection);

        Session session2 = s.getSession(connection);

        assertNotEquals(session, session2);
    }

    @Test
    public void SessionPolicy_SESSION_PER_OPERATION_BehaviorOnCheckInCheckOut() throws Exception {

        EmbeddedJmsService s = (EmbeddedJmsService)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy(new Queue("mock-queue"));
        s.setLoadStrategy(mls);

        assertEquals(SessionPolicy.SESSION_PER_OPERATION, mls.getSessionPolicy());

        s.start();

        MockSend mo = new MockSend(mls);

        //
        // checking out
        //

        JmsEndpointBase endpoint = (JmsEndpointBase)s.checkOut(mo);

        EmbeddedSession session = (EmbeddedSession)endpoint.getSession();

        //
        // check that the corresponding Session and MessageProducer have been created
        //

        List<EmbeddedConnection> connections =
                ((EmbeddedConnectionFactory)s.getConnectionFactory()).getCreatedConnections();

        assertEquals(1, connections.size());

        EmbeddedConnection connection = connections.get(0);

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());

        EmbeddedSession session2 = createdSessions.get(0);
        assertEquals(session, session2);

        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
        assertEquals(1, createdProducers.size());
        assertEquals(((Producer)endpoint).getProducer(), createdProducers.get(0));

        assertFalse(session.isClosed());

        //
        // checking in
        //

        s.checkIn(endpoint);

        assertTrue(session.isClosed());


        MockSend mo2 = new MockSend(mls);

        //
        // checking out the second
        //

        JmsEndpointBase endpoint2 = (JmsEndpointBase)s.checkOut(mo2);

        EmbeddedSession session3 = (EmbeddedSession)endpoint2.getSession();

        assertNotEquals(session, session3);

        createdSessions = connection.getCreatedSessions();
        assertEquals(2, createdSessions.size());

        EmbeddedSession session4 = createdSessions.get(1);
        assertEquals(session3, session4);

        createdProducers = session4.getCreatedProducers();
        assertEquals(1, createdProducers.size());
        assertEquals(((Producer)endpoint2).getProducer(), createdProducers.get(0));

        assertFalse(session3.isClosed());

        //
        // checking in
        //

        s.checkIn(endpoint2);

        assertTrue(session3.isClosed());

        //
        // we trust that the message producer is closed, and they're all released because the manager does not keep
        // references to them
        //
    }

    //
    // SessionPolicy.SESSION_PER_THREAD --------------------------------------------------------------------------------
    //

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_lifecycle() throws Exception {

        final JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy();
        mls.setSessionPolicy(SessionPolicy.SESSION_PER_THREAD);

        s.setLoadStrategy(mls);

        assertEquals(SessionPolicy.SESSION_PER_THREAD, s.getSessionPolicy());

        final EmbeddedConnection connection = new EmbeddedConnection();

        Session session = s.getSession(connection);

        //
        // get the session on the same thread
        //

        Session session2 = s.getSession(connection);

        assertEquals(session, session2);

        //
        // get a session on a different thread
        //

        final Session[] sessions = new Session[2];
        final Exception[] exceptions = new Exception[1];
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try {

                sessions[0] = s.getSession(connection);

                sessions[1] = s.getSession(connection);

            }
            catch(Exception e) {

                exceptions[0] = e;
            }
            finally {

                //
                // release the latch
                //
                latch.countDown();
            }
        }, "session getter").start();


        latch.await();

        if (exceptions[0] != null) {

            fail("session getter thread produced exception " + exceptions[0]);
        }

        Session session3 = sessions[0];
        Session session4 = sessions[1];

        assertNotEquals(session3, session);
        assertNotEquals(session3, session2);

        assertEquals(session3, session4);
    }

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_BehaviorOnCheckInCheckOut() throws Exception {

        final EmbeddedJmsService s = (EmbeddedJmsService)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy(new Queue("mock-queue"));
        mls.setSessionPolicy(SessionPolicy.SESSION_PER_THREAD);
        s.setLoadStrategy(mls);
        assertEquals(SessionPolicy.SESSION_PER_THREAD, s.getSessionPolicy());

        s.start();

        MockSend mo = new MockSend(mls);

        //
        // checking out
        //

        JmsEndpointBase endpoint = (JmsEndpointBase)s.checkOut(mo);

        EmbeddedSession session = (EmbeddedSession)endpoint.getSession();

        //
        // check that the corresponding Session and MessageProducer have been created
        //

        List<EmbeddedConnection> connections =
                ((EmbeddedConnectionFactory)s.getConnectionFactory()).getCreatedConnections();

        assertEquals(1, connections.size());

        EmbeddedConnection connection = connections.get(0);

        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
        assertEquals(1, createdSessions.size());

        EmbeddedSession session2 = createdSessions.get(0);
        assertEquals(session, session2);

        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
        assertEquals(1, createdProducers.size());
        assertEquals(((Producer)endpoint).getProducer(), createdProducers.get(0));

        assertFalse(session.isClosed());

        //
        // checking out the second session on a different thread
        //

        final MockSend mo2 = new MockSend(mls);

        final JmsEndpoint[] jmsEndpoints = new JmsEndpoint[1];
        final Exception[] exceptions = new Exception[1];

        final CountDownLatch endpointCheckedOut = new CountDownLatch(1);
        final CountDownLatch holdThreadBetweenCheckOutAndCheckIn = new CountDownLatch(1);
        final CountDownLatch threadDone = new CountDownLatch(1);

        new Thread(() -> {

            try {

                JmsEndpoint mo2Endpoint = s.checkOut(mo2);
                jmsEndpoints[0] = mo2Endpoint;

                log.info("endpoint checked out on " + Thread.currentThread());

                endpointCheckedOut.countDown();

                holdThreadBetweenCheckOutAndCheckIn.await();

                log.info("checking in endpoint on " + Thread.currentThread());

                s.checkIn(mo2Endpoint);

                log.info("endpoint checked in on " + Thread.currentThread());
            }
            catch(Exception e) {

                exceptions[0] = e;
            }
            finally {

                threadDone.countDown();
            }
        }, "session getter").start();


        endpointCheckedOut.await();

        if (exceptions[0] != null) {

            fail("(1) session getter thread produced exception " + exceptions[0]);
        }

        JmsEndpoint endpoint2 = jmsEndpoints[0];
        EmbeddedSession session3 = (EmbeddedSession)endpoint2.getSession();

        createdSessions = connection.getCreatedSessions();
        assertEquals(2, createdSessions.size());

        EmbeddedSession session4 = createdSessions.get(1);
        assertEquals(session3, session4);
        assertNotEquals(session, session3);

        createdProducers = createdSessions.get(1).getCreatedProducers();
        assertEquals(1, createdProducers.size());
        assertEquals(((Producer) endpoint2).getProducer(), createdProducers.get(0));

        assertFalse(session.isClosed());
        assertFalse(session3.isClosed());

        //
        // checking in the first endpoint, it is supposed to leave the session open, but to close the
        // MessageProducer/Consumer
        //

        s.checkIn(endpoint);

        EmbeddedMessageProducer p = (EmbeddedMessageProducer)((Producer) endpoint).getProducer();
        assertTrue(p.isClosed());

        EmbeddedSession s2 = (EmbeddedSession)endpoint.getSession();
        assertFalse(s2.isClosed());

        //
        // make sure both sessions are alive
        //

        createdSessions = connection.getCreatedSessions();
        assertEquals(2, createdSessions.size());
        assertFalse(createdSessions.get(0).isClosed());
        assertFalse(createdSessions.get(1).isClosed());

        //
        // return the second endpoint from the thread that created
        //

        holdThreadBetweenCheckOutAndCheckIn.countDown();

        threadDone.await();

        if (exceptions[0] != null) {

            fail("(2) session getter thread produced exception " + exceptions[0]);
        }

        EmbeddedMessageProducer p2 = (EmbeddedMessageProducer)((Producer) jmsEndpoints[0]).getProducer();
        assertTrue(p2.isClosed());

        EmbeddedSession s3 = (EmbeddedSession)jmsEndpoints[0].getSession();
        assertFalse(s3.isClosed());

        //
        // make sure both sessions are alive
        //

        createdSessions = connection.getCreatedSessions();
        assertEquals(2, createdSessions.size());
        assertFalse(createdSessions.get(0).isClosed());
        assertFalse(createdSessions.get(1).isClosed());
    }

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_EndpointCheckedInFromADifferentThread() throws Exception {

        final EmbeddedJmsService s = (EmbeddedJmsService)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy(new Queue("mock-queue"));
        mls.setSessionPolicy(SessionPolicy.SESSION_PER_THREAD);
        s.setLoadStrategy(mls);
        assertEquals(SessionPolicy.SESSION_PER_THREAD, s.getSessionPolicy());

        s.start();

        final MockSend mo = new MockSend(mls);

        final JmsEndpoint[] endpoints = new JmsEndpoint[1];
        final Exception[] exceptions = new Exception[1];
        final CountDownLatch threadDone = new CountDownLatch(1);

        //
        // check out the endpoint from a different thread
        //

        new Thread(() -> {

            try {

                endpoints[0] = s.checkOut(mo);
            }
            catch(Exception e) {

                exceptions[0] = e;
            }
            finally {

                threadDone.countDown();
            }
        }, "session getter").start();


        threadDone.await();

        if (exceptions[0] != null) {

            fail("session getter thread produced exception " + exceptions[0]);
        }

        //
        // check in the endpoint from a different thread
        //

        try {

            s.checkIn(endpoints[0]);
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "session .* was checked out by .* but is being checked in from a different thread .*"));
        }
    }

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_NoSessionAssociatedWithThreadFound() throws Exception {

        final JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy();
        mls.setSessionPolicy(SessionPolicy.SESSION_PER_THREAD);

        s.setLoadStrategy(mls);

        JmsEndpoint me = new MockJmsEndpoint();

        try {

            s.checkIn(me);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("no session associated with"));
        }
    }

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_EndpointSessionAndThreadSessionDiffer() throws Exception {

        final JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        MockJmsLoadStrategy mls = new MockJmsLoadStrategy();
        mls.setSessionPolicy(SessionPolicy.SESSION_PER_THREAD);

        s.setLoadStrategy(mls);

        s.start();

        //
        // allocate a session and associate it with the current thread
        //

        s.checkOut(new MockSend(mls));

        MockJmsEndpoint me = new MockJmsEndpoint();
        me.setSession(new EmbeddedSession(null, 0, false, Session.AUTO_ACKNOWLEDGE));

        try {

            s.checkIn(me);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("the session associated with .* is different from the endpoint session"));
        }
    }

    // start() ---------------------------------------------------------------------------------------------------------

    @Test
    public void start_ConnectionFactoryNotBoundInJNDI() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        JmsLoadStrategy ls = getMatchingLoadStrategy();
        s.setLoadStrategy(ls);

        s.setConnectionFactoryName("/something");

        try {

            s.start();
            fail("should throw exception");
        }

        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("Connection factory /something not bound in JNDI", msg);
        }
    }

    @Test
    public void start_UsernameAndPasswordPresent_AuthorizationFailure() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        JmsLoadStrategy ls = getMatchingLoadStrategy();
        s.setLoadStrategy(ls);

        //
        // the default authorized user is EmbeddedJmsService.DEFAULT_AUTHORIZED_USER
        //
        s.setUsername("some-random-user");
        s.setPassword(new char[] {'s', 'o', 'm', 'e', 't', 'h', 'i', 'n', 'g'});

        try {

            s.start();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("unauthorized connection attempt"));

            Throwable cause = e.getCause();
            assertTrue(cause instanceof JMSException);
        }
    }

    @Test
    public void start_UsernameAndPasswordPresent() throws Exception {

        JmsServiceBase s = (JmsServiceBase)getServiceToTest();
        JmsLoadStrategy ls = getMatchingLoadStrategy();
        s.setLoadStrategy(ls);

        s.setUsername(EmbeddedJmsService.DEFAULT_AUTHORIZED_USER);
        s.setPassword(EmbeddedJmsService.DEFAULT_AUTHORIZED_PASSWORD_AS_CHAR_ARRAY);

        s.start();

        assertTrue(s.isStarted());
    }

    // end to end ------------------------------------------------------------------------------------------------------

    @Test
    public void sendEndToEnd_CONNECTION_PER_RUN_And_SESSION_PER_OPERATION() throws Exception {

        //
        // this is how the load driver initializes the service and the load strategy
        //

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        msc.set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(SendLoadStrategy.NAME, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        msc.set("test-queue", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set(EmbeddedJmsService.DEFAULT_CONNECTION_FACTORY_NAME,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        JmsService service = getServiceToTest();

        service.configure(msc);

        JmsLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.init(msc, mlc);

        service.setLoadStrategy(loadStrategy);
        loadStrategy.setService(service);

        service.start();

        //
        // this is how the load driver handles an operation
        //

        Operation operation = loadStrategy.next(null, null, false);

        operation.perform(service);

        service.stop();

        //
        // test whether the message made it through
        //

        if (service instanceof EmbeddedJmsService) {

            EmbeddedJmsService es = (EmbeddedJmsService)service;

            List<Message> messages = es.getMessagesSentToDestination("test-queue", true);
            assertEquals(1, messages.size());
            assertEquals(loadStrategy.getReusedValue(), ((TextMessage)messages.get(0)).getText());
        }
    }

    @Test
    public void receiveEndToEnd_CONNECTION_PER_RUN_And_SESSION_PER_OPERATION() throws Exception {

        //
        // this is how the load driver initializes the service and the load strategy
        //

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        msc.set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(ReceiveLoadStrategy.NAME, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        msc.set("test-queue", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set(EmbeddedJmsService.DEFAULT_CONNECTION_FACTORY_NAME,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        JmsService service = getServiceToTest();

        service.configure(msc);

        //
        // place a test message in the test queue
        //

        String messageContent = "n32Hw2";

        if (service instanceof EmbeddedJmsService) {

            EmbeddedJmsService es = (EmbeddedJmsService)service;
            es.addToDestination("test-queue", true, new EmbeddedTextMessage(messageContent));
        }

        JmsLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        loadStrategy.init(msc, mlc);

        service.setLoadStrategy(loadStrategy);
        loadStrategy.setService(service);

        service.start();

        //
        // this is how the load driver handles an operation
        //

        JmsOperation operation = (JmsOperation)loadStrategy.next(null, null, false);

        operation.perform(service);

        service.stop();

        //
        // test whether the message was received
        //

        String payload = operation.getPayload();

        if (service instanceof EmbeddedJmsService) {

            assertEquals(messageContent, payload);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected abstract JmsService getServiceToTest() throws Exception;

    protected abstract JmsLoadStrategy getMatchingLoadStrategy();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
