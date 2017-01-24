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

import io.novaordis.gld.api.jms.Producer;
import io.novaordis.gld.api.jms.embedded.EmbeddedMessageProducer;
import io.novaordis.gld.api.jms.embedded.EmbeddedQueue;
import io.novaordis.gld.api.jms.embedded.EmbeddedSession;
import io.novaordis.gld.api.jms.load.MockJmsLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import org.junit.Test;

import javax.jms.Session;

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

        SendLoadStrategy ls = new SendLoadStrategy();

        Send send = ls.next(null, null, false);

        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedMessageProducer jmsProducer = (EmbeddedMessageProducer)jmsSession.createProducer(jmsQueue);

        Producer endpoint = new Producer(jmsProducer, jmsSession);

        fail("return here");
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
