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

import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import org.junit.Test;

import static org.junit.Assert.fail;

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

        fail("return here");

//        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
//        Receive receive = getJmsOperationToTest(loadStrategy);
//
//        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
//        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
//        EmbeddedMessageConsumer jmsConsumer = (EmbeddedMessageConsumer)jmsSession.createConsumer(jmsQueue);
//
//        Consumer endpoint = new Consumer(jmsConsumer, jmsSession);
//
//        receive.perform(endpoint);
    }

    @Test
    public void perform_WithTimeout() throws Exception {

        fail("return here");

//        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
//        loadStrategy.setTimeoutMs(7L);
//
//        Receive receive = getJmsOperationToTest(loadStrategy);
//
//        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
//        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
//        EmbeddedMessageConsumer jmsConsumer = (EmbeddedMessageConsumer)jmsSession.createConsumer(jmsQueue);
//
//        Consumer endpoint = new Consumer(jmsConsumer, jmsSession);
//
//        receive.perform(endpoint);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Send getOperationToTest(String key) throws Exception {

        MockJmsLoadStrategy ms = new MockJmsLoadStrategy();
        return new Send(ms);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
