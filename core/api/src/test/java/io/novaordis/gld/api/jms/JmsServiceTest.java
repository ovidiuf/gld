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
import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.LoadConfigurationImpl;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.embedded.EmbeddedJMSService;
import io.novaordis.gld.api.jms.embedded.EmbeddedSession;
import io.novaordis.gld.api.jms.embedded.TestableConnection;
import io.novaordis.gld.api.jms.embedded.TestableMessageProducer;
import io.novaordis.gld.api.jms.embedded.TestableQueue;
import io.novaordis.gld.api.jms.embedded.TestableSession;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
import io.novaordis.gld.api.jms.load.JMSLoadStrategyBase;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.jms.operation.MockSend;
import io.novaordis.gld.api.jms.operation.Send;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceTest;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public abstract class JMSServiceTest extends ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JMSServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    protected File scratchDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //

        assertTrue(Files.rmdir(scratchDirectory, false));

        MockInitialContextFactory.reset();
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getServiceType() throws Exception {

        JMSService s = getServiceToTest();
        assertEquals(ServiceType.jms, s.getType());
    }

    //
    // configure() -----------------------------------------------------------------------------------------------------
    //

    @Test
    public void configure_NotTheRightTypeOfServiceConfiguration() throws Exception {

        JMSService s = getServiceToTest();

        MockServiceConfiguration msc = new MockServiceConfiguration();

        try {

            s.configure(msc);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("not a JMS service configuration"));
        }
    }

    //
    // ConnectionPolicy.CONNECTION_PER_RUN -----------------------------------------------------------------------------
    //

    @Test
    public void ConnectionPolicy_CONNECTION_PER_RUN_lifecycle() throws Exception {

        JMSService s = getServiceToTest();

        LoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        JMSServiceBase sb = (JMSServiceBase)s;

        Connection c = sb.getConnection();

        assertNull(c);

        s.start();

        c = sb.getConnection();

        Connection c2 = sb.getConnection();

        assertTrue(c == c2);

        s.stop();

        c = sb.getConnection();

        assertNull(c);
    }

    @Test
    public void ConnectionPolicy_CONNECTION_PER_RUN_BehaviorOnCheckInCheckOut() throws Exception {

        JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        s.start();

        MockSend mo = new MockSend(ls);

        JMSEndpointBase endpoint = (JMSEndpointBase)s.checkOut(mo);

        Connection c = endpoint.getConnection();

        s.checkIn(endpoint);

        MockSend mo2 = new MockSend(ls);

        JMSEndpointBase endpoint2 = (JMSEndpointBase)s.checkOut(mo2);

        Connection c2 = endpoint2.getConnection();

        s.checkIn(endpoint2);

        assertTrue(c == c2);
    }

    //
    // SessionPolicy.SESSION_PER_OPERATION -----------------------------------------------------------------------------
    //

    @Test
    public void SessionPolicy_SESSION_PER_OPERATION_lifecycle() throws Exception {

        JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                "test-user",
                "test-password");

        s.setLoadStrategy(ls);

        assertEquals(SessionPolicy.SESSION_PER_OPERATION, ls.getSessionPolicy());

        s.start();

        JMSServiceBase sb = (JMSServiceBase)s;

        Connection c = sb.getConnection();

        Session session = sb.getSession(c);

        Session session2 = sb.getSession(c);

        assertNotEquals(session, session2);
    }

    @Test
    public void SessionPolicy_SESSION_PER_OPERATION_BehaviorOnCheckInCheckOut() throws Exception {

        JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        assertEquals(SessionPolicy.SESSION_PER_OPERATION, ls.getSessionPolicy());

        s.start();

        MockSend mo = new MockSend(ls);

        //
        // checking out
        //

        JMSEndpoint endpoint = s.checkOut(mo);

        TestableSession session = (TestableSession)endpoint.getSession();

        //
        // check that the corresponding Session and MessageProducer have been created
        //

        TestableConnection c = (TestableConnection)((JMSServiceBase)s).getConnection();

        List<TestableSession> cs = c.getCreatedSessions();
        assertEquals(1, cs.size());

        TestableSession session2 = cs.get(0);
        assertEquals(session, session2);

        List<TestableMessageProducer> cp = session2.getCreatedProducers();
        assertEquals(1, cp.size());
        assertEquals(((Producer) endpoint).getProducer(), cp.get(0));

        assertFalse(session.isClosed());

        //
        // checking in
        //

        s.checkIn(endpoint);

        assertTrue(session.isClosed());

        MockSend mo2 = new MockSend(ls);

        //
        // checking out the second
        //

        JMSEndpoint endpoint2 = s.checkOut(mo2);

        TestableSession session3 = (TestableSession)endpoint2.getSession();

        assertNotEquals(session, session3);

        cs = c.getCreatedSessions();
        assertEquals(2, cs.size());

        TestableSession session4 = cs.get(1);
        assertEquals(session3, session4);

        cp = session4.getCreatedProducers();
        assertEquals(1, cp.size());
        assertEquals(((Producer)endpoint2).getProducer(), cp.get(0));

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

        final JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_THREAD,
                "test-user",
                "test-password");


        s.setLoadStrategy(ls);

        s.start();

        final JMSServiceBase sb = (JMSServiceBase)s;

        assertEquals(SessionPolicy.SESSION_PER_THREAD, sb.getSessionPolicy());

        final Connection connection = ((JMSServiceBase) s).getConnection();

        Session session = sb.getSession(connection);

        //
        // get the session on the same thread
        //

        Session session2 = sb.getSession(connection);

        assertEquals(session, session2);

        //
        // get a session on a different thread
        //

        final Session[] sessions = new Session[2];
        final Exception[] exceptions = new Exception[1];
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {

            try {

                sessions[0] = sb.getSession(connection);

                sessions[1] = sb.getSession(connection);

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

        final JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_THREAD,
                "test-user",
                "test-password");


        s.setLoadStrategy(ls);

        final JMSServiceBase sb = (JMSServiceBase)s;

        assertEquals(SessionPolicy.SESSION_PER_THREAD, sb.getSessionPolicy());

        s.start();

        MockSend mo = new MockSend(ls);

        //
        // checking out
        //

        JMSEndpointBase endpoint = (JMSEndpointBase)s.checkOut(mo);

        TestableSession session = (TestableSession)endpoint.getSession();

        //
        // check that the corresponding Session and MessageProducer have been created
        //

        TestableConnection c = (TestableConnection)sb.getConnection();

        List<TestableSession> cs = c.getCreatedSessions();
        assertEquals(1, cs.size());

        TestableSession session2 = cs.get(0);
        assertEquals(session, session2);

        List<TestableMessageProducer> cp = cs.get(0).getCreatedProducers();
        assertEquals(1, cp.size());
        assertEquals(((Producer) endpoint).getProducer(), cp.get(0));

        assertFalse(session.isClosed());

        //
        // checking out the second session on a different thread
        //

        final MockSend mo2 = new MockSend(ls);

        final JMSEndpoint[] jmsEndpoints = new JMSEndpoint[1];
        final Exception[] exceptions = new Exception[1];

        final CountDownLatch endpointCheckedOut = new CountDownLatch(1);
        final CountDownLatch holdThreadBetweenCheckOutAndCheckIn = new CountDownLatch(1);
        final CountDownLatch threadDone = new CountDownLatch(1);

        new Thread(() -> {

            try {

                JMSEndpoint mo2Endpoint = s.checkOut(mo2);
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

        JMSEndpoint endpoint2 = jmsEndpoints[0];
        TestableSession session3 = (TestableSession)endpoint2.getSession();

        cs = c.getCreatedSessions();
        assertEquals(2, cs.size());

        TestableSession session4 = cs.get(1);
        assertEquals(session3, session4);
        assertNotEquals(session, session3);

        cp = cs.get(1).getCreatedProducers();
        assertEquals(1, cp.size());
        assertEquals(((Producer) endpoint2).getProducer(), cp.get(0));

        assertFalse(session.isClosed());
        assertFalse(session3.isClosed());

        //
        // checking in the first endpoint, it is supposed to leave the session open, but to close the
        // MessageProducer/Consumer
        //

        s.checkIn(endpoint);

        TestableMessageProducer p = (TestableMessageProducer)((Producer) endpoint).getProducer();
        assertTrue(p.isClosed());

        TestableSession s2 = (TestableSession)endpoint.getSession();
        assertFalse(s2.isClosed());

        //
        // make sure both sessions are alive
        //

        cs = c.getCreatedSessions();
        assertEquals(2, cs.size());
        assertFalse(cs.get(0).isClosed());
        assertFalse(cs.get(1).isClosed());

        //
        // return the second endpoint from the thread that created
        //

        holdThreadBetweenCheckOutAndCheckIn.countDown();

        threadDone.await();

        if (exceptions[0] != null) {

            fail("(2) session getter thread produced exception " + exceptions[0]);
        }

        TestableMessageProducer p2 = (TestableMessageProducer)((Producer) jmsEndpoints[0]).getProducer();
        assertTrue(p2.isClosed());

        TestableSession s3 = (TestableSession)jmsEndpoints[0].getSession();
        assertFalse(s3.isClosed());

        //
        // make sure both sessions are alive
        //

        cs = c.getCreatedSessions();
        assertEquals(2, cs.size());
        assertFalse(cs.get(0).isClosed());
        assertFalse(cs.get(1).isClosed());
    }

    @Test
    public void SessionPolicy_SESSION_PER_THREAD_EndpointCheckedInFromADifferentThread() throws Exception {

        final JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_THREAD,
                "test-user",
                "test-password");

        s.setLoadStrategy(ls);

        final JMSServiceBase sb = (JMSServiceBase)s;

        assertEquals(SessionPolicy.SESSION_PER_THREAD, sb.getSessionPolicy());

        s.start();

        final MockSend mo = new MockSend(ls);

        final JMSEndpoint[] endpoints = new JMSEndpoint[1];
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

        final JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_THREAD,
                "test-user",
                "test-password");

        s.setLoadStrategy(ls);

        final JMSServiceBase sb = (JMSServiceBase)s;

        assertEquals(SessionPolicy.SESSION_PER_THREAD, sb.getSessionPolicy());

        JMSEndpoint me = new MockJMSEndpoint();

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

        final JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_THREAD,
                "test-user",
                "test-password");

        s.setLoadStrategy(ls);

        final JMSServiceBase sb = (JMSServiceBase)s;

        assertEquals(SessionPolicy.SESSION_PER_THREAD, sb.getSessionPolicy());

        s.start();

        //
        // allocate a session and associate it with the current thread
        //

        s.checkOut(new MockSend(ls));

        MockJMSEndpoint me = new MockJMSEndpoint();
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
    public void start_UsernameAndPasswordPresent_AuthorizationFailure() throws Exception {

        final JMSService s = getServiceToTest();

        //
        // the default authorized user is EmbeddedJMSService.DEFAULT_AUTHORIZED_USER
        //

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_USER,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_PASSWORD);

        //
        // override username/password with some random value
        //

        JMSLoadStrategyBase lsb = (JMSLoadStrategyBase)ls;

        lsb.setUsername("some-random-user");
        lsb.setPassword("something".toCharArray());

        s.setLoadStrategy(lsb);

        try {

            s.start();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            Throwable cause = e.getCause();
            assertTrue(cause instanceof JMSException);
            String msg = cause.getMessage();
            assertTrue(msg.contains("AUTHENTICATION FAILURE"));
        }
    }

    @Test
    public void start_UsernameAndPasswordPresent() throws Exception {

        final JMSService s = getServiceToTest();

        //
        // the default authorized user is EmbeddedJMSService.DEFAULT_AUTHORIZED_USER
        //

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_USER,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_PASSWORD);

        s.setLoadStrategy(ls);

        s.start();

        assertTrue(s.isStarted());
    }

    // end to end ------------------------------------------------------------------------------------------------------

    @Test
    public void sendEndToEnd_CONNECTION_PER_RUN_And_SESSION_PER_OPERATION() throws Exception {

        final JMSService s = getServiceToTest();

        //
        // the default authorized user is EmbeddedJMSService.DEFAULT_AUTHORIZED_USER
        //

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_USER,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_PASSWORD);

        s.setLoadStrategy(ls);

        ls.setService(s);

        s.start();

        //
        // this is how the load driver handles an operation
        //

        Operation operation = ls.next(null, null, false);

        operation.perform(s);

        TestableConnection c = (TestableConnection)((JMSServiceBase) s).getConnection();

        s.stop();

        //
        // test whether the message made it through
        //

        List<TestableSession> ss = c.getCreatedSessions();
        assertEquals(1, ss.size());
        TestableSession ts = ss.get(0);
        List<TestableMessageProducer> ps = ts.getCreatedProducers();
        assertEquals(1, ps.size());
        TestableMessageProducer p = ps.get(0);

        assertTrue(p.isClosed());

        TestableQueue q = (TestableQueue)p.getDestination();
        List<Message> messages = q.getMessagesSent();
        assertEquals(1, messages.size());
        assertEquals(ls.getReusedValue(), ((TextMessage)messages.get(0)).getText());
    }

    @Test
    public void receiveEndToEnd_CONNECTION_PER_RUN_And_SESSION_PER_OPERATION() throws Exception {

        final JMSService s = getServiceToTest();

        //
        // the default authorized user is EmbeddedJMSService.DEFAULT_AUTHORIZED_USER
        //

        JMSLoadStrategy ls = getMatchingJMSLoadStrategyToTest(
                s,
                ReceiveLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_USER,
                EmbeddedJMSService.DEFAULT_AUTHORIZED_PASSWORD);

        s.setLoadStrategy(ls);

        ls.setService(s);

        //
        // place a test message in the test queue read from the load strategy
        //
        String messageContent = "n32Hw2";
        String destinationName = ls.getDestination().getName();
        placeTextMessageInQueue(s, messageContent, destinationName);

        s.start();

        //
        // this is how the load driver handles an operation
        //

        JmsOperation operation = (JmsOperation)ls.next(null, null, false);

        operation.perform(s);

        s.stop();

        //
        // test whether the message was received
        //

        String payload = operation.getPayload();
        assertEquals(messageContent, payload);
    }

    // checkOut() ------------------------------------------------------------------------------------------------------

    @Test
    public void checkOut_DestinationNotFound() throws Exception {

        JMSService s = getServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        s.start();

        //
        // remove destination from whatever context is in effect
        //

        String name = ls.getDestination().getName();
        removeDestinationFromContext(s, name);

        JmsOperation operation = new Send(ls);

        try {

            s.checkOut(operation);
            fail("should throw exception");
        }
        catch(JMSServiceException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("destination not found"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JMSService getServiceToTest() throws Exception {

        return getJMSServiceToTest();
    }

    @Override
    protected JMSLoadStrategy getMatchingLoadStrategyToTest(Service s) throws Exception {

        return getMatchingJMSLoadStrategyToTest(
                (JMSService) s,
                SendLoadStrategy.NAME,
                "/TestQueue",
                "/TestConnectionFactory",
                ConnectionPolicy.CONNECTION_PER_RUN,
                SessionPolicy.SESSION_PER_OPERATION,
                "test-user",
                "test-password");
    }

    /**
     * @return a JMSService instance fully configured so it can be successfully started, provided that the associated
     * JMSLoadStrategy instance, returned by getMatchingJMSLoadStrategyToTest(), is applied with setLoadStrategy().
     * The method is also responsible with configuring the context with the elements it introduces, to allow a
     * successful lifecycle.
     */
    protected abstract JMSService getJMSServiceToTest() throws Exception;

    /**
     * The method is also responsible with configuring the context with the elements it introduces, to allow a
     * successful lifecycle.
     */
    protected JMSLoadStrategy getMatchingJMSLoadStrategyToTest(
            JMSService service,
            String loadStrategyName,
            String destinationJNDIName,
            String connectionFactoryJNDIName,
            ConnectionPolicy connectionPolicy,
            SessionPolicy sessionPolicy,
            String user,
            String password)
            throws Exception {

        JMSLoadStrategyBase result;

        if (SendLoadStrategy.NAME.equals(loadStrategyName)) {

            result = new SendLoadStrategy();
        }
        else if (ReceiveLoadStrategy.NAME.equals(loadStrategyName)) {

            result = new ReceiveLoadStrategy();
        }
        else {

            throw new RuntimeException("UNKNOWN LOAD STRATEGY NAME: " + loadStrategyName);
        }

        File configDir = new File(scratchDirectory, "mock-config");
        assertTrue(configDir.mkdirs());

        Map<String, Object> rsc = new HashMap<>();
        Map<String, Object> rls = new HashMap<>();

        rsc.put(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, rls);
        rsc.put(ServiceConfiguration.TYPE_LABEL, "jms");
        rls.put(LoadStrategyBase.NAME_LABEL, loadStrategyName);
        rls.put(JMSLoadStrategy.QUEUE_LABEL, destinationJNDIName);
        rls.put(JMSLoadStrategy.CONNECTION_FACTORY_LABEL, connectionFactoryJNDIName);
        rls.put(JMSLoadStrategy.CONNECTION_POLICY_LABEL, connectionPolicy.getLabel());
        rls.put(JMSLoadStrategy.SESSION_POLICY_LABEL, sessionPolicy.getLabel());
        rls.put(JMSLoadStrategy.USERNAME_LABEL, user);
        rls.put(JMSLoadStrategy.PASSWORD_LABEL, password);

        JMSServiceConfigurationImpl sc = new JMSServiceConfigurationImpl(rsc, configDir);

        Map<String, Object> rlc = new HashMap<>();
        LoadConfiguration lc = new LoadConfigurationImpl(ServiceType.jms, rlc, configDir);

        result.init(sc, lc);

        createDestinationInContext(service, destinationJNDIName);
        createConnectionFactoryInContext(service, connectionFactoryJNDIName, user, password);

        return result;
    }

    protected abstract void placeTextMessageInQueue(JMSService service, String text, String queueNme);

    protected abstract void createDestinationInContext(JMSService service, String destinationJndiName)
            throws Exception;

    protected abstract void removeDestinationFromContext(JMSService service, String destinationJndiName)
            throws Exception;

    protected abstract void createConnectionFactoryInContext(
            JMSService service, String connectionFactoryJndiName, String username, String password) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
