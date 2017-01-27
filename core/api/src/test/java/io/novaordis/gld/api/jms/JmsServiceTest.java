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
import io.novaordis.gld.api.jms.embedded.EmbeddedJmsService;
import io.novaordis.gld.api.jms.embedded.EmbeddedTextMessage;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.service.ServiceTest;
import io.novaordis.gld.api.service.ServiceType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void ConnectionPolicy_CONNECTION_PER_RUN() throws Exception {

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


    // to sort out -----------------------------------------------------------------------------------------------------

    @Test
    public void sessionPerOperation_sendLifecycle() throws Exception {

        fail("return here");

//        EmbeddedConnection connection = new EmbeddedConnection();
//
//        JmsResourceManager manager = new JmsResourceManager(
//                connection, ConnectionPolicy.CONNECTION_PER_RUN, SessionPolicy.SESSION_PER_OPERATION);
//
//        SendLoadStrategy loadStrategy = new SendLoadStrategy();
//        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
//        Send send = new Send(loadStrategy);
//
//        Producer endpoint = (Producer)manager.checkOutEndpoint(send);
//
//        // check that the corresponding Session and MessageProducer have been created
//
//        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
//        assertEquals(1, createdSessions.size());
//        EmbeddedSession session = createdSessions.get(0);
//        assertEquals(endpoint.getSession(), session);
//
//        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
//        assertEquals(1, createdProducers.size());
//        assertEquals(endpoint.getProducer(), createdProducers.get(0));
//
//        assertFalse(session.isClosed());
//
//        manager.returnEndpoint(endpoint);
//
//        // check that the corresponding Session and MessageProducer have been released
//
//        assertTrue(session.isClosed());
//
//        //
//        // we trust that the message producer is closed, and they're all released because the manager does not keep
//        // references to them
//        //
    }

    @Test
    public void sessionPerOperation_receiveLifecycle() throws Exception {

        fail("return here");

//
//        EmbeddedConnection connection = new EmbeddedConnection();
//
//        JmsResourceManager manager = new JmsResourceManager(
//                connection, ConnectionPolicy.CONNECTION_PER_RUN, SessionPolicy.SESSION_PER_OPERATION);
//
//        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
//        loadStrategy.setDestination(new Topic("TEST-TOPIC"));
//        Receive receive = new Receive(loadStrategy);
//
//        Consumer endpoint = (Consumer)manager.checkOutEndpoint(receive);
//
//        // check that the corresponding Session and MessageConsumer have been created
//
//        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
//        assertEquals(1, createdSessions.size());
//        EmbeddedSession session = createdSessions.get(0);
//        assertEquals(endpoint.getSession(), session);
//
//        List<EmbeddedMessageConsumer> createdConsumers = createdSessions.get(0).getCreatedConsumers();
//        assertEquals(1, createdConsumers.size());
//        assertEquals(endpoint.getConsumer(), createdConsumers.get(0));
//
//        assertFalse(session.isClosed());
//
//        manager.returnEndpoint(endpoint);
//
//        // check that the corresponding Session and MessageProducer have been released
//
//        assertTrue(session.isClosed());
//
//        //
//        // we trust that the message consumer is closed, and they're all released because the manager does not keep
//        // references to them
//        //
    }

    @Test
    public void sessionPerThread_sendLifecycle() throws Exception {

        fail("return here");

//        EmbeddedConnection connection = new EmbeddedConnection();
//
//        JmsResourceManager manager =
//            new JmsResourceManager(connection, ConnectionPolicy.CONNECTION_PER_RUN, SessionPolicy.SESSION_PER_THREAD);
//
//        SendLoadStrategy loadStrategy = new SendLoadStrategy();
//        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
//        Send send = new Send(loadStrategy);
//
//        Producer endpoint = (Producer)manager.checkOutEndpoint(send);
//
//        //
//        // check that the corresponding Session and MessageProducer have been created
//        //
//
//        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
//        assertEquals(1, createdSessions.size());
//        EmbeddedSession session = createdSessions.get(0);
//        assertEquals(endpoint.getSession(), session);
//
//        List<EmbeddedMessageProducer> createdProducers = createdSessions.get(0).getCreatedProducers();
//        assertEquals(1, createdProducers.size());
//        EmbeddedMessageProducer messageProducer = createdProducers.get(0);
//        assertEquals(endpoint.getProducer(), messageProducer);
//
//        assertFalse(session.isClosed());
//        assertFalse(messageProducer.isClosed());
//
//        //
//        // return the endpoint
//        //
//
//        manager.returnEndpoint(endpoint);
//
//        //
//        // check that the corresponding MessageProducer have been released and closed
//        //
//
//        assertTrue(messageProducer.isClosed());
//
//        //
//        // check that the corresponding session is still around and opened - it was cached for reuse on the
//        // same thread
//        //
//
//        assertFalse(session.isClosed());
//
//        //
//        // check out another endpoint - make sure we get a different one, but on the same session
//        //
//
//        Producer endpoint2 = (Producer)manager.checkOutEndpoint(send);
//
//        // check that the corresponding Session and MessageProducer have been created
//
//        List<EmbeddedSession> createdSessions2 = connection.getCreatedSessions();
//        assertEquals(1, createdSessions2.size());
//        EmbeddedSession session2 = createdSessions2.get(0);
//        assertEquals(endpoint2.getSession(), session2);
//
//        // make sure it's the same session
//        assertEquals(endpoint2.getSession(), session);
//
//        List<EmbeddedMessageProducer> createdProducers2 = session2.getCreatedProducers();
//        assertEquals(2, createdProducers2.size());
//        EmbeddedMessageProducer messageProducer2 = createdProducers2.get(1);
//        assertEquals(endpoint2.getProducer(), messageProducer2);
//
//        assertFalse(session2.isClosed());
//        assertFalse(messageProducer2.isClosed());
//
//        // return the endpoint
//
//        manager.returnEndpoint(endpoint2);
//
//        // check that the corresponding MessageProducer have been released and closed
//
//        assertTrue(messageProducer2.isClosed());
//
//        // check that the corresponding session is still around and opened - it was cached for reuse on the
//        // same thread
//
//        assertFalse(session2.isClosed());
//
//        // make sure the session is closed when we close the manager
//
//        manager.close();
//
//        assertTrue(session.isClosed());
//        assertTrue(session2.isClosed());
    }

    @Test
    public void sessionPerThread__receiveLifecycle() throws Exception {

        fail("return here");

//        EmbeddedConnection connection = new EmbeddedConnection();
//
//        JmsResourceManager manager =
//            new JmsResourceManager(connection, ConnectionPolicy.CONNECTION_PER_RUN, SessionPolicy.SESSION_PER_THREAD);
//
//        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
//        loadStrategy.setDestination(new Queue("TEST-QUEUE"));
//        Receive receive = new Receive(loadStrategy);
//
//        Consumer endpoint = (Consumer)manager.checkOutEndpoint(receive);
//
//        // check that the corresponding Session and MessageProducer have been created
//
//        List<EmbeddedSession> createdSessions = connection.getCreatedSessions();
//        assertEquals(1, createdSessions.size());
//        EmbeddedSession session = createdSessions.get(0);
//        assertEquals(endpoint.getSession(), session);
//
//        List<EmbeddedMessageConsumer> createdConsumers = createdSessions.get(0).getCreatedConsumers();
//        assertEquals(1, createdConsumers.size());
//        EmbeddedMessageConsumer messageConsumer = createdConsumers.get(0);
//        assertEquals(endpoint.getConsumer(), messageConsumer);
//
//        assertFalse(session.isClosed());
//        assertFalse(messageConsumer.isClosed());
//
//        // return the endpoint
//
//        manager.returnEndpoint(endpoint);
//
//        // check that the corresponding MessageConsumer have been released and closed
//
//        assertTrue(messageConsumer.isClosed());
//
//        // check that the corresponding session is still around and opened - it was cached for reuse on the
//        // same thread
//
//        assertFalse(session.isClosed());
//
//        // check out another endpoint - make sure we get a different one, but on the same session
//
//        Consumer endpoint2 = (Consumer)manager.checkOutEndpoint(receive);
//
//        // check that the corresponding Session and MessageConsumer have been created
//
//        List<EmbeddedSession> createdSessions2 = connection.getCreatedSessions();
//        assertEquals(1, createdSessions2.size());
//        EmbeddedSession session2 = createdSessions2.get(0);
//        assertEquals(endpoint2.getSession(), session2);
//
//        // make sure it's the same session
//        assertEquals(endpoint2.getSession(), session);
//
//        List<EmbeddedMessageConsumer> createdConsumers2 = session2.getCreatedConsumers();
//        assertEquals(2, createdConsumers2.size());
//        EmbeddedMessageConsumer messageConsumer2 = createdConsumers2.get(1);
//        assertEquals(endpoint2.getConsumer(), messageConsumer2);
//
//        assertFalse(session2.isClosed());
//        assertFalse(messageConsumer2.isClosed());
//
//        // return the endpoint
//
//        manager.returnEndpoint(endpoint2);
//
//        // check that the corresponding MessageConsumer have been released and closed
//
//        assertTrue(messageConsumer2.isClosed());
//
//        // check that the corresponding session is still around and opened - it was cached for reuse on the
//        // same thread
//
//        assertFalse(session2.isClosed());
//
//        // make sure the session is closed when we close the manager
//
//        manager.close();
//
//        assertTrue(session.isClosed());
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
        msc.set("test-connection-factory",
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
        msc.set("test-connection-factory",
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
