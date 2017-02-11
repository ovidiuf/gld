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
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.embedded.EmbeddedTextMessage;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import io.novaordis.gld.api.jms.load.MockReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public class ReceiveTest extends JmsOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void perform_NoTimeout() throws Exception {

        ReceiveLoadStrategy ls = new ReceiveLoadStrategy();
        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        assertEquals("/jms/test-queue", msc.getQueueName());
        msc.setLoadStrategyName(ls.getName());
        ls.init(msc, new MockLoadConfiguration());

        Receive receive = ls.next(null, null, false);

        MockJmsService service = new MockJmsService();
        service.setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        service.setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);
        service.setLoadStrategy(new MockJmsLoadStrategy());

        service.start();

        EmbeddedQueue q = (EmbeddedQueue)service.resolveDestination(new Queue("/jms/test-queue"));
        q.add(new EmbeddedTextMessage("b3snB3"));

        receive.perform(service);

        String s = receive.getPayload();
        assertEquals("b3snB3", s);
    }

    @Test
    public void perform_WithTimeout() throws Exception {

        long timeout = 10L;

        ReceiveLoadStrategy ls = new ReceiveLoadStrategy();
        ls.setTimeoutMs(timeout);

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        assertEquals("/jms/test-queue", msc.getQueueName());
        msc.setLoadStrategyName(ls.getName());
        ls.init(msc, new MockLoadConfiguration());

        Receive receive = ls.next(null, null, false);

        MockJmsService service = new MockJmsService();
        service.setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        service.setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);
        service.setLoadStrategy(new MockJmsLoadStrategy());

        service.start();

        EmbeddedQueue q = (EmbeddedQueue)service.resolveDestination(new Queue("/jms/test-queue"));

        assertTrue(q.isEmpty());

        long t0 = System.currentTimeMillis();

        receive.perform(service);

        long t1 = System.currentTimeMillis();

        String s = receive.getPayload();
        assertNull(s);

        assertTrue(t1 - t0 >= timeout);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Receive getOperationToTest(String key) throws Exception {

        MockReceiveLoadStrategy ms = new MockReceiveLoadStrategy();

        Receive r = new Receive(ms);
        r.setId(key);

        return r;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
