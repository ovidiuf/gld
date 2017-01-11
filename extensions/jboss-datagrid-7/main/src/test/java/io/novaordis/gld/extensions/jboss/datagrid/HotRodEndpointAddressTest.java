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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class HotRodEndpointAddressTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HotRodEndpointAddressTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // extractHost() ---------------------------------------------------------------------------------------------------

    @Test
    public void extractHost_Null() throws Exception {

        String s = HotRodEndpointAddress.extractHost(null);
        //noinspection ConstantConditions
        assertNull(s);
    }

    @Test
    public void extractHost() throws Exception {

        String s = HotRodEndpointAddress.extractHost("something");
        assertEquals("something", s);
    }

    @Test
    public void extractHost_MissingPortValue() throws Exception {

        try {
            HotRodEndpointAddress.extractHost("something:");
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing port information", msg);
        }
    }

    @Test
    public void extractHost2() throws Exception {

        String s = HotRodEndpointAddress.extractHost("something:does not matter");
        assertEquals("something", s);
    }

    // extractPort() ---------------------------------------------------------------------------------------------------

    @Test
    public void extractPort_Null() throws Exception {

        Integer i = HotRodEndpointAddress.extractPort(null);
        //noinspection ConstantConditions
        assertNull(i);
    }

    @Test
    public void extractPort() throws Exception {

        Integer i = HotRodEndpointAddress.extractPort("something");
        assertNull(i);
    }

    @Test
    public void extractPort_MissingPortValue() throws Exception {

        try {

            HotRodEndpointAddress.extractPort("something:");
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing port information", msg);
        }
    }

    @Test
    public void extractPort_NotAnInt() throws Exception {

        try {

            HotRodEndpointAddress.extractPort("something:blah");
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid port value \"blah\"", msg);
        }
    }

    @Test
    public void extractPort2() throws Exception {

        Integer i = HotRodEndpointAddress.extractPort("something:22222");
        assertEquals(22222, i.intValue());
    }

    // validatePort() ---------------------------------------------------------------------------------------------------

    @Test
    public void validatePort_InvalidValue() throws Exception {

        try {

            HotRodEndpointAddress.validatePort(-1);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid port value -1", msg);
        }
    }

    @Test
    public void validatePort_InvalidValue2() throws Exception {

        try {

            HotRodEndpointAddress.validatePort(0);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid port value 0", msg);
        }
    }

    @Test
    public void validatePort_InvalidValue3() throws Exception {

        try {

            HotRodEndpointAddress.validatePort(65536);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid port value 65536", msg);
        }
    }

    @Test
    public void validatePort() throws Exception {

        HotRodEndpointAddress.validatePort(65535);
    }

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void defaults() throws Exception {

        HotRodEndpointAddress a = new HotRodEndpointAddress();
        assertEquals(HotRodEndpointAddress.DEFAULT_HOST, a.getHost());
        assertEquals(HotRodEndpointAddress.DEFAULT_HOTROD_PORT, a.getPort());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
