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

import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class HotRodEndpointAddress {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_HOTROD_PORT = 11222;
    public static final String DEFAULT_HOST = "localhost";

    // Static ----------------------------------------------------------------------------------------------------------

    // Package protected static ----------------------------------------------------------------------------------------

    /**
     * @return the host part form a "host[:port]" string endpoint specification. May return null.
     */
    static String extractHost(String s) throws UserErrorException {

        if (s == null) {

            return null;
        }

        int i = s.indexOf(':');

        if (i == -1) {

            return s;
        }

        if (i == s.length() - 1) {

            throw new UserErrorException("missing port information");
        }

        return s.substring(0, i);
    }

    /**
     * Does not perform port boundary validation.
     *
     * @return the integer part form a "host[:port]" string endpoint specification. May return null.
     *
     * @see HotRodEndpointAddress#validatePort(int)
     */
    static Integer extractPort(String s) throws UserErrorException {

        if (s == null) {

            return null;
        }

        int i = s.indexOf(':');

        if (i == -1) {

            return null;
        }

        if (i == s.length() - 1) {

            throw new UserErrorException("missing port information");
        }

        s = s.substring(i + 1);

        try {

            return Integer.parseInt(s);
        }
        catch(Exception e) {

            throw new UserErrorException("invalid port value \""+ s + "\"");
        }
    }

    /**
     * Perform port boundary validation and throw exception if the port has an invalid value.
     */
    static void validatePort(int port) throws UserErrorException {

        if (port <= 0 || port >= 65536) {

            throw new UserErrorException("invalid port value " + port);
        }

    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String host;
    private int port;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HotRodEndpointAddress() throws UserErrorException {

        this(null, null);
    }

    /**
     * @param hostAndPort endpoint address in the format host[:port]
     */
    public HotRodEndpointAddress(String hostAndPort) throws UserErrorException {

        this(extractHost(hostAndPort), extractPort(hostAndPort));
    }

    public HotRodEndpointAddress(String host, Integer port) throws UserErrorException  {

        if (host == null) {

            this.host = DEFAULT_HOST;
        }
        else {

            this.host = host;
        }

        if (port == null) {

            this.port = DEFAULT_HOTROD_PORT;
        }
        else {

            validatePort(port);
            this.port = port;
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

    @Override
    public String toString() {

        return host + ":" + port;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
