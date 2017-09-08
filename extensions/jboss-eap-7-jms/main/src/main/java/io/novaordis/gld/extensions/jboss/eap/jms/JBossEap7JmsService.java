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

import io.novaordis.gld.api.jms.JNDIBasedJMSService;
import io.novaordis.utilities.version.VersionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public class JBossEap7JmsService extends JNDIBasedJMSService {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossEap7JmsService.class);

    public static final String
            DEFAULT_INITIAL_CONTEXT_FACTORY_CLASS_NAME = "org.jboss.naming.remote.client.InitialContextFactory";

    public static final String JNDI_URL_PREFIX = "http-remoting://";

    public static final String EXTENSION_VERSION_METADATA_FILE_NAME = "jboss-eap-7-jms-extension-version";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public JBossEap7JmsService() {

        setNamingInitialContextFactoryClassName(DEFAULT_INITIAL_CONTEXT_FACTORY_CLASS_NAME);

        log.debug(this + " constructed");
    }

    // JNDIBasedJMSService overrides -----------------------------------------------------------------------------------

    @Override
    protected String getJndiUrlPrefix() {

        return JNDI_URL_PREFIX;
    }

    @Override
    public String getVersion() {

        return VersionUtilities.getVersion(EXTENSION_VERSION_METADATA_FILE_NAME);
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

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConnectionFactoryName(String s) {

        super.setConnectionFactoryName(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
