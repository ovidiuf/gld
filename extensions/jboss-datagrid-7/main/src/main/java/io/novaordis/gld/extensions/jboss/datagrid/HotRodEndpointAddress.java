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

package io.novaordis.gld.extensions.jboss.datagrid;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class HotRodEndpointAddress {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_HOTROD_PORT = 11222;
    public static final String DEFAULT_HOST = "localhost";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String host;
    private int port;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HotRodEndpointAddress() {

        this(null, null);
    }

    public HotRodEndpointAddress(String host, Integer port) {

        if (host == null) {

            this.host = DEFAULT_HOST;
        }

        if (port == null) {

            this.port = DEFAULT_HOTROD_PORT;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the host name or the default "localhost"
     */
    public String getHost() {

        return host;
    }

    /**
     * @return the port name or the default (11222).
     */
    public int getPort() {

        return port;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
