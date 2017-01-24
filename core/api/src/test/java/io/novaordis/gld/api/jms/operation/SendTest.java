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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

        fail("return here");

//        SendLoadStrategy loadStrategy = new SendLoadStrategy();
//
//        Send send = getJmsOperationToTest(loadStrategy);
//
//        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
//        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
//        EmbeddedMessageProducer jmsProducer = (EmbeddedMessageProducer)jmsSession.createProducer(jmsQueue);
//
//        Producer endpoint = new Producer(jmsProducer, jmsSession);
//
//        send.perform(endpoint);
    }

    @Test
    public void perform_SpecificMessageSize() throws Exception {

        fail("return here");

//        int messageSize = 758;
//
//        SendLoadStrategy loadStrategy = new SendLoadStrategy();
//        loadStrategy.setMessageSize(messageSize);
//
//        Send send = getJmsOperationToTest(loadStrategy);
//
//        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
//        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
//        EmbeddedMessageProducer jmsProducer = (EmbeddedMessageProducer)jmsSession.createProducer(jmsQueue);
//
//        Producer endpoint = new Producer(jmsProducer, jmsSession);
//
//        send.perform(endpoint);
//
//        List<Message> messages = jmsProducer.getMessagesSentByThisProducer();
//        assertEquals(1, messages.size());
//        Message m = messages.get(0);
//        assertEquals(messageSize, ((TextMessage)m).getText().length());
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
