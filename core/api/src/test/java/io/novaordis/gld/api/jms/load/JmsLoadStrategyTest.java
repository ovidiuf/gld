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

import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.service.ServiceType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

        Destination d = ls.getDestination();
        assertNotNull(d);

        ConnectionPolicy cp = ls.getConnectionPolicy();
        assertEquals(ConnectionPolicy.CONNECTION_PER_RUN, cp);

        SessionPolicy sp = ls.getSessionPolicy();
        assertEquals(SessionPolicy.SESSION_PER_OPERATION, sp);

        //
        // unlimited operations
        //

        assertNull(ls.getRemainingOperations());
    }

    // next() ----------------------------------------------------------------------------------------------------------


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract JmsLoadStrategy getLoadStrategyToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}
