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

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.ConnectionFactory;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JmsServiceConfiguration;
import io.novaordis.gld.api.jms.MockJmsServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public abstract  class JmsLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getServiceType() throws Exception {

        JmsLoadStrategy ls = getLoadStrategyToTest();
        assertEquals(ServiceType.jms, ls.getServiceType());
        log.debug(".");
    }

    @Test
    public void identityAndDefaults() throws Exception {

        JmsLoadStrategy ls = getLoadStrategyToTest();

        ConnectionPolicy cp = ls.getConnectionPolicy();
        assertEquals(ConnectionPolicy.CONNECTION_PER_RUN, cp);

        SessionPolicy sp = ls.getSessionPolicy();
        assertEquals(SessionPolicy.SESSION_PER_OPERATION, sp);

        //
        // unlimited operations
        //

        assertNull(ls.getRemainingOperations());
    }

    // init() ----------------------------------------------------------------------------------------------------------

    @Test
    public void init_Defaults() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsLoadStrategy.CONNECTION_FACTORY_LABEL, "/jms/TestConnectionFactory");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        Destination d = s.getDestination();
        assertEquals("/jms/test-queue", d.getName());
        assertTrue(d.isQueue());

        ConnectionFactory cf = s.getConnectionFactory();
        assertEquals("/jms/TestConnectionFactory", cf.getName());

        assertEquals(ConnectionPolicy.CONNECTION_PER_RUN, s.getConnectionPolicy());
        assertEquals(SessionPolicy.SESSION_PER_OPERATION, s.getSessionPolicy());
        assertNull(s.getOperations());
        assertNull(s.getMessages());

        assertEquals(ServiceConfiguration.DEFAULT_VALUE_SIZE, s.getMessageSize());
    }

    @Test
    public void init_MissingDestination() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());

        assertNull(rawLSC.get(JmsLoadStrategy.QUEUE_LABEL));
        assertNull(rawLSC.get(JmsLoadStrategy.TOPIC_LABEL));

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("required configuration element queue|topic missing", msg);
        }
    }

    @Test
    public void init_MissingConnectionFactory() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");

        assertNull(rawLSC.get(JmsLoadStrategy.CONNECTION_FACTORY_LABEL));

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("required configuration element connection-factory missing", msg);
        }
    }

    @Test
    public void init_BothQueueAndTopic() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "A");
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "B");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("both a queue and a topic are specified, they should be mutually exclusive", msg);
        }
    }

    @Test
    public void init_Topic() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.TOPIC_LABEL, "/jms/test-topic");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        Destination d = s.getDestination();
        assertEquals("/jms/test-topic", d.getName());
        assertTrue(d.isTopic());
    }

    @Test
    public void init_ServiceConfigurationMessageSizeDifferFromDefault() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        msc.setMessageSize(777);
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        assertEquals(777, s.getMessageSize());
    }

    @Test
    public void init_LoadConfigurationMessageSizeOverride() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        msc.setMessageSize(777);
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsServiceConfiguration.MESSAGE_SIZE_LABEL, 778);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        assertEquals(778, s.getMessageSize());
    }

    @Test
    public void init_NonDefaultMaxOperations() throws Exception {


        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");

        //
        // currently, max-operations this is configured with LoadConfiguration
        //

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        mlc.setMessages(123L);

        s.init(msc, mlc);

        assertEquals(123L, s.getMessages().longValue());
    }

    @Test
    public void init_NonDefaultConnectionPolicy() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsLoadStrategy.CONNECTION_POLICY_LABEL, "connection-per-thread");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        ConnectionPolicy cp  = s.getConnectionPolicy();
        assertEquals(ConnectionPolicy.CONNECTION_PER_THREAD, cp);
    }

    @Test
    public void init_InvalidConnectionPolicy() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsLoadStrategy.CONNECTION_POLICY_LABEL, "no-such-connection-policy");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid connection policy 'no-such-connection-policy', valid options: 'blah', 'blah', 'blah'", msg);
        }
    }

    @Test
    public void init_NonDefaultSessionPolicy() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsLoadStrategy.SESSION_POLICY_LABEL, "session-per-thread");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        SessionPolicy sp  = s.getSessionPolicy();
        assertEquals(SessionPolicy.SESSION_PER_THREAD, sp);
    }

    @Test
    public void init_InvalidSessionPolicy() throws Exception {

        JmsLoadStrategy s = getLoadStrategyToTest();

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        Map<String, Object> rawLSC = new HashMap<>();
        msc.set(rawLSC, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        rawLSC.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, s.getName());
        rawLSC.put(JmsLoadStrategy.QUEUE_LABEL, "/jms/test-queue");
        rawLSC.put(JmsLoadStrategy.SESSION_POLICY_LABEL, "no-such-session-policy");

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid session policy 'no-such-session-policy', valid options: 'blah', 'blah', 'blah'", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract JmsLoadStrategy getLoadStrategyToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}
