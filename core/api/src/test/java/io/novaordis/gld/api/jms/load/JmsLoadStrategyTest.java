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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JmsServiceConfiguration;
import io.novaordis.gld.api.jms.MockJmsServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        Destination d = s.getDestination();
        assertEquals("/jms/test-queue", d.getName());
        assertTrue(d.isQueue());

        String cfn = s.getConnectionFactoryName();
        assertEquals("/jms/TestConnectionFactory", cfn);

        assertEquals(ConnectionPolicy.CONNECTION_PER_RUN, s.getConnectionPolicy());
        assertEquals(SessionPolicy.SESSION_PER_OPERATION, s.getSessionPolicy());
        assertNull(s.getOperations());
        assertNull(s.getMessages());

        assertEquals(ServiceConfiguration.DEFAULT_VALUE_SIZE, s.getMessageSize());

        assertNull(s.getUsername());
        assertNull(s.getPassword());
    }

    @Test
    public void init_MissingDestination() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set(null, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set(null, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.TOPIC_LABEL);

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

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set(null, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("required configuration element 'connection-factory' missing", msg);
        }
    }

    @Test
    public void init_BothQueueAndTopic() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set("A", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set("B", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.TOPIC_LABEL);

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

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set(null, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set("/jms/test-topic", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.TOPIC_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        Destination d = s.getDestination();
        assertEquals("/jms/test-topic", d.getName());
        assertTrue(d.isTopic());
    }

    @Test
    public void init_ServiceConfigurationMessageSizeDifferFromDefault() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.setMessageSize(777);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        assertEquals(777, s.getMessageSize());
    }

    @Test
    public void init_LoadConfigurationMessageSizeOverride() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.setMessageSize(777);

        msc.set(778,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsServiceConfiguration.MESSAGE_SIZE_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        assertEquals(778, s.getMessageSize());
    }

    @Test
    public void init_NonDefaultMaxOperations() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

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

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set("connection-per-thread",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_POLICY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        ConnectionPolicy cp  = s.getConnectionPolicy();
        assertEquals(ConnectionPolicy.CONNECTION_PER_THREAD, cp);
    }

    @Test
    public void init_InvalidConnectionPolicy() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set("no-such-connection-policy",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_POLICY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid connection policy 'no-such-connection-policy', valid options: 'connection-per-run', 'connection-per-thread', 'connection-per-operation', 'connection-pool'", msg);
        }
    }

    @Test
    public void init_NonDefaultSessionPolicy() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set("session-per-thread",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.SESSION_POLICY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        SessionPolicy sp  = s.getSessionPolicy();
        assertEquals(SessionPolicy.SESSION_PER_THREAD, sp);
    }

    @Test
    public void init_InvalidSessionPolicy() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();
        JmsLoadStrategy s = getLoadStrategyToTest();
        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);

        msc.set("no-such-session-policy",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.SESSION_POLICY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        try {

            s.init(msc, mlc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid session policy 'no-such-session-policy', valid options: 'session-per-thread', 'session-per-operation'", msg);
        }
    }

    @Test
    public void init_NonNullUsername() throws Exception {

        MockJmsServiceConfiguration msc = getCorrespondingServiceConfiguration();

        JmsLoadStrategy s = getLoadStrategyToTest();

        msc.set(s.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        msc.set("test-user", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.USERNAME_LABEL);
        msc.set("test", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.PASSWORD_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        s.init(msc, mlc);

        assertEquals("test-user", s.getUsername());

        assertEquals(4, s.getPassword().length);
        assertEquals('t', s.getPassword()[0]);
        assertEquals('e', s.getPassword()[1]);
        assertEquals('s', s.getPassword()[2]);
        assertEquals('t', s.getPassword()[3]);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract JmsLoadStrategy getLoadStrategyToTest() throws Exception;

    @Override
    protected MockJmsServiceConfiguration getCorrespondingServiceConfiguration() {

        return new MockJmsServiceConfiguration();
    }

    @Override
    protected void initialize(LoadStrategy ls, MockServiceConfiguration sc) throws Exception {

        assertTrue(ls instanceof JmsLoadStrategy);
        sc.set(ls.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        ls.init(sc, new MockLoadConfiguration());
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}
