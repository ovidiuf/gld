/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novaordis.gld.service.jms;

import com.novaordis.gld.service.jms.embedded.EmbeddedQueue;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import org.junit.Test;

import javax.jms.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class JmsEndpointTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JmsEndpointTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // close -----------------------------------------------------------------------------------------------------------

    @Test
    public void closeDoesNotCloseSession() throws Exception
    {
        EmbeddedSession session = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);

        JmsEndpoint e = getEndpointToTest(session, new EmbeddedQueue("TEST"));

        e.close();

        assertEquals(session, e.getSession());

        assertFalse(session.isClosed());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract JmsEndpoint getEndpointToTest(Session session, javax.jms.Destination jmsDestination)
        throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
