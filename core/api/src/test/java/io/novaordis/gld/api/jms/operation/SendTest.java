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

package io.novaordis.gld.api.jms.operation;

import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.jms.MockJmsService;
import io.novaordis.gld.api.jms.MockJmsServiceConfiguration;
import io.novaordis.gld.api.jms.Queue;
import io.novaordis.gld.api.jms.embedded.EmbeddedConnection;
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import org.junit.Test;

import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public class SendTest extends JmsOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void perform() throws Exception {

        SendLoadStrategy ls = new SendLoadStrategy();
        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        assertEquals("/jms/test-queue", msc.getQueueName());
        msc.setLoadStrategyName(ls.getName());
        ls.init(msc, new MockLoadConfiguration());

        Send send = ls.next(null, null, false);

        assertNull(send.getKey());

        MockJmsService service = new MockJmsService();
        service.setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        service.setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);
        service.setConnection(new EmbeddedConnection());

        send.perform(service);

        EmbeddedQueue q = (EmbeddedQueue)service.resolveDestination(new Queue("/jms/test-queue"));
        TextMessage m = (TextMessage)q.get(0);
        assertEquals(ls.getReusedValue(), m.getText());

        String key = send.getKey();

        assertNotNull(key);

        String id = send.getId();
        assertEquals(key, id);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Send getOperationToTest(String key) throws Exception {

        MockJmsLoadStrategy ms = new MockJmsLoadStrategy();
        Send s = new Send(ms);

        //
        // send operations do not get IDs (keys) right away, but only after transit through the JMS machinery, so
        // we need to simulate it
        //

        s.setId(key);

        return s;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
