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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.jms.embedded.EmbeddedMessageConsumer;
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.embedded.EmbeddedSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MessageConsumer;
import javax.jms.Session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsumerTest extends JmsEndpointTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ConsumerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void close() throws Exception {

        EmbeddedSession session = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedQueue queue = new EmbeddedQueue("TEST");
        Consumer c = getEndpointToTest(session, queue);

        c.close();

        EmbeddedMessageConsumer mc = (EmbeddedMessageConsumer)c.getConsumer();

        assertFalse(session.isClosed());
        assertTrue(mc.isClosed());

        log.debug(".");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Consumer getEndpointToTest(Session session, javax.jms.Destination jmsDestination) throws Exception {

        MessageConsumer c = session.createConsumer(jmsDestination);
        return new Consumer(c, session);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
