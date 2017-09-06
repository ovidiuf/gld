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

import io.novaordis.gld.api.jms.JmsServiceBase;
import io.novaordis.utilities.version.VersionUtilities;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/5/17
 */
public class JBossEap6JmsService extends JmsServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String EXTENSION_VERSION_METADATA_FILE_NAME = "jboss-eap-6-jms-extension-version";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsServiceBase overrides ----------------------------------------------------------------------------------------

    @Override
    public Destination resolveDestination(io.novaordis.gld.api.jms.Destination d) throws Exception {

        throw new RuntimeException("resolveDestination() NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionFactory resolveConnectionFactory(String connectionFactoryName) throws Exception {

        throw new RuntimeException("resolveConnectionFactory() NOT YET IMPLEMENTED");
    }

    @Override
    public String getVersion() {

        return VersionUtilities.getVersion(EXTENSION_VERSION_METADATA_FILE_NAME);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
