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
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
import io.novaordis.gld.api.jms.load.MockJMSLoadStrategy;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Before;
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

    private String testConnectionFactoryName = "/TestConnectionFactory";

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    protected File scratchDirectory;

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

        s.setJndiUrl("mock://something");
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());

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

        s.setJndiUrl("mock://something");
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setConnectionFactoryName("i-am-sure-there-is-no-such-JMS-connection-factory");
        s.setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);

        try {

            s.start();
            fail("should have thrown Exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("connection factory"));
            assertTrue(msg.contains("i-am-sure-there-is-no-such-JMS-connection-factory"));
            assertTrue(msg.contains("not bound in JNDI"));
        }
    }

    @Test
    public void start_LoadStrategyNotInstalled() throws Exception {

        JNDIBasedJMSService s = getJNDIBasedJMSServiceToTest();

        s.setJndiUrl("mock://something");
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        s.setConnectionFactoryName("/some-connection-factory");
        s.setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);

        MockConnectionFactory mcf = new MockConnectionFactory();
        MockInitialContextFactory.install("/some-connection-factory", mcf);

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

        MockJMSLoadStrategy mls = new MockJMSLoadStrategy();
        s.setLoadStrategy(mls);

        //
        // MockJMSLoadStrategy configures the service with to lookup /MockConnectionFactory
        //
        String connectionFactoryName = mls.getConnectionFactoryName();
        MockConnectionFactory mcf = new MockConnectionFactory();
        MockInitialContextFactory.install(connectionFactoryName, mcf);

        s.setJndiUrl("mock://something");
        s.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());

        s.start();

        assertTrue(s.isStarted());

        InitialContext ic = s.getInitialContext();

        assertNotNull(ic);
        MockConnectionFactory mcf2 = (MockConnectionFactory)ic.lookup(connectionFactoryName);
        assertEquals(mcf, mcf2);

        MockConnection jmsConnection = (MockConnection)s.getConnection();
        assertTrue(jmsConnection.isStarted());

        LoadStrategy ls = s.getLoadStrategy();
        assertTrue(ls.isStarted());
    }

    // stop() ----------------------------------------------------------------------------------------------------------

    @Test
    public void stop() throws Exception {

        throw new RuntimeException("RETURN HERE");
    }

    // resolveDestination() -------------------------------------------------------------------------------------------

    @Test
    public void resolveDestination() throws Exception {

        throw new RuntimeException("RETURN HERE");
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
        MockConnectionFactory mcf = new MockConnectionFactory();

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

        JNDIBasedJMSService jndiBasedJmsService = getJNDIBasedJMSServiceToTest();

        //
        // we need to configure it to work it properly
        //

        jndiBasedJmsService.setJndiUrl("mock://mock-jndi-server");
        jndiBasedJmsService.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());
        jndiBasedJmsService.setConnectionFactoryName(testConnectionFactoryName);

        //
        // install a mock connection factory in the mock JNDI
        //

        MockConnectionFactory mcf = new MockConnectionFactory();
        MockInitialContextFactory.install(testConnectionFactoryName, mcf);

        return jndiBasedJmsService;
    }

    protected abstract JNDIBasedJMSService getJNDIBasedJMSServiceToTest() throws Exception;

    @Override
    protected JMSLoadStrategy getMatchingLoadStrategy() {

        MockJMSLoadStrategy ms = new MockJMSLoadStrategy();
        ms.setConnectionFactoryName(testConnectionFactoryName);
        return ms;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
