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

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import java.util.Properties;

/**
 * A generic implementation of a JMS service that obtains its connection factories and destinations from JNDI.
 *
 * The implementation initializes the InitialContext during start up and terminates it during shutdown.
 *
 *
 * It handles resolveDestination(...) and resolveConnectionFactory(...) internally by looking the corresponding
 * objects up in the JNDI context.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public abstract class JNDIBasedJMSService extends JMSServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JNDIBasedJMSService.class);

    public static final String JNDI_URL_LABEL = "jndi-url";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    //
    // the server's JNDI URL including the protocol part
    //
    private String jndiUrl;

    private String namingInitialContextFactoryClassName;

    //
    // As long as the service is started, the variable contains a reference to an active, verified InitialContext into
    // the server's JNDI space
    //
    private InitialContext ic;

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // JMSServiceBase overrides ----------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        if (!(serviceConfiguration instanceof JMSServiceConfiguration)) {

            throw new IllegalArgumentException("not a JMS service configuration");
        }

        //
        // we need the JNDI endpoint to connect to the EAP server
        //

        String rawJndiUrl;

        try {

            rawJndiUrl = serviceConfiguration.get(
                    String.class, ServiceConfiguration.IMPLEMENTATION_CONFIGURATION_LABEL, JNDI_URL_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException(e);
        }

        if (rawJndiUrl == null) {

            throw new UserErrorException("missing required '" + JNDI_URL_LABEL + "' configuration element");
        }

        log.debug(JNDI_URL_LABEL + ": " + rawJndiUrl);

        if (!rawJndiUrl.contains("://")) {

            rawJndiUrl = getJndiUrlPrefix() + rawJndiUrl;
        }

        setJndiUrl(rawJndiUrl);

        log.debug(this + " successfully configured");
    }

    @Override
    public void start() throws Exception {

        synchronized (this) {

            if (isStarted()) {

                log.debug(this + " already started");
                return;
            }

            log.debug("starting JNDI-based JMS service base of " + this);

            //
            // we first need to start this layer, as the superclass will need the JNDI context up and running when
            // attempting to look up JMS objects
            //

            initializeJNDI();

            //
            // start the superclass layer, install the JMS objects ...
            //

            super.start();

            this.started = true;

            log.debug("service " + this + " fully started");
        }
    }

    @Override
    public boolean isStarted() {

        synchronized (this) {

            return started;
        }
    }

    @Override
    public void stop() {

        log.debug("stopping JNDI-based JMS service base of " + this);

        super.stop();

        synchronized (this) {

        }
    }

    @Override
    public javax.jms.Destination resolveDestination(Destination d) throws Exception {

        throw new RuntimeException("NYE");
    }

    @Override
    public javax.jms.ConnectionFactory resolveConnectionFactory(String connectionFactoryName) throws Exception {

        if (connectionFactoryName == null) {

            throw new IllegalArgumentException("null connection factory name");
        }

        try {

            Object o = ic.lookup(connectionFactoryName);
            return (javax.jms.ConnectionFactory)o;
        }
        catch(NameNotFoundException e) {

            log.debug(connectionFactoryName + " not bound in JNDI");

            return null;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the initial context. May be null if the service was not started.
     */
    public InitialContext getInitialContext() {

        return ic;
    }

    public String getNamingInitialContextFactoryClassName() {

        return namingInitialContextFactoryClassName;
    }

    public void setNamingInitialContextFactoryClassName(String s) {

        this.namingInitialContextFactoryClassName = s;

        log.debug(this + " set initial context factory class name to " + namingInitialContextFactoryClassName);
    }

    /**
     * Different implementations require specific JNDI prefixes. EAP 6 uses remote://, EAP 7 uses http-remoting://
     *
     * If the external configuration does not specifies one, the value returned by this method will be prefixed to
     * the configuration value.
     *
     * @return the appropriate JNDI protocol prefix for the implementation, including the "://" separator.
     */
    protected abstract String getJndiUrlPrefix();

    // Package protected -----------------------------------------------------------------------------------------------

    String getJndiUrl() {

        return jndiUrl;
    }

    void setJndiUrl(String s) {

        this.jndiUrl = s;

        log.debug(this + " set JNDI URL to " + jndiUrl);
    }

    /**
     *  We first attempt to connect to the JNDI service before initializing the JMS machinery in the superclass.
     *
     *  @exception IllegalStateException if jndiUrl or the initial context factory not set
     *  @exception UserErrorException on other errors.
     */
    void initializeJNDI() throws UserErrorException {

        if (jndiUrl == null) {

            throw new IllegalStateException("JNDI URL not initialized");
        }

        String icf = getNamingInitialContextFactoryClassName();

        if (icf == null) {

            throw new IllegalStateException("initial context factory not initialized");
        }

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, getNamingInitialContextFactoryClassName());
        p.put(Context.PROVIDER_URL, jndiUrl);

        try {

            ic = new InitialContext(p);

            //
            // make a noop lookup to insure valid connectivity
            //

            ic.list("");

            //
            // at this point we have a valid initial context
            //
        }
        catch(Exception e) {

            throw new UserErrorException(e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
