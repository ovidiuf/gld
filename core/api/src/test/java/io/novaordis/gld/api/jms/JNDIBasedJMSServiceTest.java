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
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public abstract class JNDIBasedJMSServiceTest extends JMSServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // configure() -----------------------------------------------------------------------------------------------------

    @Test
    public void configure_NotAJMSServiceConfiguration() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        try {

            s.configure(new MockServiceConfiguration());
            fail("should have thrown Exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("not a JMS service configuration"));
        }
    }

    @Test
    public void configure_NoJNDIUrl() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        File mockConfigDir = new File(scratchDirectory, "mock-config");
        assertTrue(mockConfigDir.mkdirs());

        JMSServiceConfigurationImpl sc = new JMSServiceConfigurationImpl(new HashMap<>(), mockConfigDir);

        try {

            s.configure(sc);
            fail("should have thrown Exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains(
                    "missing required '" + JNDIBasedJMSService.JNDI_URL_LABEL + "' configuration element"));
        }
    }

    @Test
    public void configure_JNDIUrlDoesNotHavePrefix() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        File mockConfigDir = new File(scratchDirectory, "mock-config");
        assertTrue(mockConfigDir.mkdirs());

        Map<String, Object> content = new HashMap<>();
        content.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, new HashMap<>());

        JMSServiceConfigurationImpl sc = new JMSServiceConfigurationImpl(content, mockConfigDir);
        sc.set("something",
                ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL,
                JNDIBasedJMSService.JNDI_URL_LABEL);

        s.configure(sc);

        String jndiPrefix = s.getJndiUrlPrefix();
        assertNotNull(jndiPrefix);

        String expected = jndiPrefix + "something";
        assertEquals(expected, s.getJndiUrl());
    }

    @Test
    public void configure_JNDIUrlHasPrefix() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        File mockConfigDir = new File(scratchDirectory, "mock-config");
        assertTrue(mockConfigDir.mkdirs());

        Map<String, Object> content = new HashMap<>();
        content.put(ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, new HashMap<>());

        JMSServiceConfigurationImpl sc = new JMSServiceConfigurationImpl(content, mockConfigDir);
        sc.set("something://something-else",
                ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL,
                JNDIBasedJMSService.JNDI_URL_LABEL);

        s.configure(sc);

        String jndiPrefix = s.getJndiUrlPrefix();
        assertNotNull(jndiPrefix);

        assertEquals("something://something-else", s.getJndiUrl());
        assertFalse("something://something-else".contains(jndiPrefix));
    }

    // start() ---------------------------------------------------------------------------------------------------------

    @Test
    public void start_jndiUrlNotInstalled() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        s.setJndiUrl(null);
        assertNull(s.getJndiUrl());

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("JNDI URL not initialized"));
        }
    }

    @Test
    public void start_initialContextFactoryNotInitialized() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        s.setNamingInitialContextFactoryClassName(null);
        assertNull(s.getNamingInitialContextFactoryClassName());

        s.setJndiUrl("mock://something");

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("initial context factory not initialized"));
        }
    }

    @Test
    public void start_InitialContextListFails() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        MockInitialContextFactory.setListFails(true);

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(UserErrorException e) {

            NamingException cause = (NamingException)e.getCause();
            String msg = cause.getMessage();
            assertEquals("SYNTHETIC", msg);
        }
    }

    @Test
    public void start_NoSuchJmsConnectionFactory() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        //
        // remove the connection factory from the JNDI space
        //

        String connectionFactoryName = s.getConnectionFactoryName();

        assertNotNull(MockInitialContextFactory.getJndiSpace().remove(connectionFactoryName));

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("connection factory"));
            assertTrue(msg.contains(connectionFactoryName));
            assertTrue(msg.contains("not bound in JNDI"));
        }
    }

    @Test
    public void start_LoadStrategyNotInstalled() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("load strategy not installed"));
        }
    }

    @Test
    public void start() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        s.start();

        assertTrue(s.isStarted());

        InitialContext ic = s.getInitialContext();

        assertNotNull(ic);

        String connectionFactoryName = ls.getConnectionFactoryName();

        MockConnectionFactory mcf = (MockConnectionFactory)ic.lookup(connectionFactoryName);

        assertNotNull(mcf);

        MockConnection jmsConnection = (MockConnection)s.getConnection();
        assertTrue(jmsConnection.isClosed());

        LoadStrategy ls2 = s.getLoadStrategy();
        assertTrue(ls2.isStarted());
    }

    // stop() ----------------------------------------------------------------------------------------------------------

    @Test
    public void stop() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        JMSLoadStrategy ls = getMatchingLoadStrategyToTest(s);

        s.setLoadStrategy(ls);

        s.start();

        assertTrue(s.isStarted());
        assertTrue(ls.isStarted());

        MockConnection c = (MockConnection)s.getConnection();
        assertTrue(c.isClosed());

        s.stop();

        assertFalse(s.isStarted());
        assertFalse(ls.isStarted());
        assertTrue(c.isClosed());

        //
        // idempotence
        //

        s.stop();

        assertFalse(s.isStarted());
        assertFalse(ls.isStarted());
        assertTrue(c.isClosed());
    }

    // resolveDestination() -------------------------------------------------------------------------------------------

    @Test
    public void resolveDestination_NullDestination() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        try {

            s.resolveDestination(null);
            fail("should have thrown exceptions");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null destination"));
        }
    }

    @Test
    public void resolveDestination_NoSuchDestinationInJNDI() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setJndiUrl("mock://something");

        s.initializeJNDI();

        Queue q = new Queue("I-am-pretty-sure-there-is-no-such-queue-in-JNDI");

        javax.jms.Destination result = s.resolveDestination(q);

        assertNull(result);
    }

    @Test
    public void resolveDestination() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        String destinationJndiName = "/mock-queue";
        MockQueue mq = new MockQueue();

        MockInitialContextFactory.install(destinationJndiName, mq);

        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setJndiUrl("mock://something");

        s.initializeJNDI();

        Queue q = new Queue(destinationJndiName);

        javax.jms.Destination result = s.resolveDestination(q);
        assertEquals(mq, result);
    }

    // resolveConnectionFactory() --------------------------------------------------------------------------------------

    @Test
    public void resolveJmsConnectionFactory_NullName() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        try {

            s.resolveConnectionFactory(null);
            fail("should have thrown exceptions");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null connection factory name"));
        }
    }

    @Test
    public void resolveJmsConnectionFactory_NoSuchConnectionFactory() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setJndiUrl("mock://something");

        s.initializeJNDI();

        javax.jms.ConnectionFactory result =
                s.resolveConnectionFactory("i-am-sure-there-is-no-such-connection-factory");
        assertNull(result);
    }

    @Test
    public void resolveJmsConnectionFactory() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        String connectionFactoryJNDIName = "some-mock-connection-factory";
        MockConnectionFactory mcf = new MockConnectionFactory(null, null);

        MockInitialContextFactory.install(connectionFactoryJNDIName, mcf);

        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setJndiUrl("mock://something");

        s.initializeJNDI();

        javax.jms.ConnectionFactory cf = s.resolveConnectionFactory(connectionFactoryJNDIName);
        assertEquals(mcf, cf);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JMSService getJMSServiceToTest() throws Exception {

        return getJNDIBasedJMSServiceToTest();
    }

    @Override
    protected void createDestinationInContext(JMSService service, String destinationJndiName) throws Exception {

        MockQueue mq = new MockQueue();
        MockInitialContextFactory.install(destinationJndiName, mq);
    }

    @Override
    protected void removeDestinationFromContext(JMSService service, String destinationJndiName) throws Exception {

        MockInitialContextFactory.getJndiSpace().remove(destinationJndiName);
    }

    @Override
    protected void createConnectionFactoryInContext(
            JMSService service, String connectionFactoryJndiName, String username, String password)
            throws Exception {

        MockConnectionFactory mcf = new MockConnectionFactory(username, password);
        MockInitialContextFactory.install(connectionFactoryJndiName, mcf);
    }

    protected abstract JNDIBasedJMSService getJNDIBasedJMSServiceToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
