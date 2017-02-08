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

package io.novaordis.gld.extensions.jboss.eap.jms;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.JmsServiceBase;
import io.novaordis.gld.api.jms.JmsServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.version.VersionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import java.util.Properties;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public class JBossEap7JmsService extends JmsServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossEap7JmsService.class);

    public static final String DEFAULT_INITIAL_CONTEXT_FACTORY_CLASS_NAME =
            "org.jboss.naming.remote.client.InitialContextFactory";

    public static final String EXTENSION_VERSION_METADATA_FILE_NAME = "jboss-eap-7-jms-extension-version";

    public static final String JNDI_URL_LABEL = "jndi-url";

    private String initialContextFactoryClassName;

    // the JNDI URL including the protocol part
    private String jndiUrl;

    // an active, verified InitialContext into the server's JNDI space
    private InitialContext ic;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public JBossEap7JmsService() {

        setInitialContextFactoryClassName(DEFAULT_INITIAL_CONTEXT_FACTORY_CLASS_NAME);
    }

    // JmsServiceBase overrides ----------------------------------------------------------------------------------------

    @Override
    public String getVersion() {

        return VersionUtilities.getVersion(EXTENSION_VERSION_METADATA_FILE_NAME);
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        if (!(serviceConfiguration instanceof JmsServiceConfiguration)) {

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

        if (!rawJndiUrl.contains("://")) {

            rawJndiUrl = "http-remoting://" + rawJndiUrl;
        }

        setJndiUrl(rawJndiUrl);
    }

    @Override
    public void start() throws Exception {

        //
        // we first attempt to connect to the JNDI service before initializing the JMS machinery in the superclass.
        //

        if (jndiUrl == null) {

            throw new IllegalStateException("JNDI URL was not initialized");
        }

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, getInitialContextFactoryClassName());
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

        super.start();

    }
    @Override
    public Destination resolveDestination(io.novaordis.gld.api.jms.Destination d) throws Exception {

        throw new RuntimeException("resolveDestination() NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionFactory resolveConnectionFactory(String connectionFactoryName) throws Exception {

        if (connectionFactoryName == null) {

            throw new IllegalArgumentException("null connection factory");
        }

        try {

            Object o = ic.lookup(connectionFactoryName);
            return (ConnectionFactory)o;
        }
        catch(NameNotFoundException e) {

            log.debug(connectionFactoryName + " not bound in JNDI");

            return null;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        String s = "EAP7 JMS ";

        String jndiUrl = getJndiUrl();

        if (jndiUrl == null) {

            s += " (UNCONFIGURED)";
        }
        else {

            s += jndiUrl;
        }

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    String getJndiUrl() {

        return jndiUrl;
    }

    void setJndiUrl(String s) {

        this.jndiUrl = s;
    }

    String getInitialContextFactoryClassName() {

        return initialContextFactoryClassName;
    }

    void setInitialContextFactoryClassName(String s) {

        this.initialContextFactoryClassName = s;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConnectionFactoryName(String s) {

        super.setConnectionFactoryName(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
