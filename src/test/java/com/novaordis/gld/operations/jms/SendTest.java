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

package com.novaordis.gld.operations.jms;

import com.novaordis.gld.service.jms.Producer;
import com.novaordis.gld.service.jms.embedded.EmbeddedMessageProducer;
import com.novaordis.gld.service.jms.embedded.EmbeddedQueue;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import org.junit.Test;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SendTest extends JmsOperationTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void perform() throws Exception
    {
        SendLoadStrategy loadStrategy = new SendLoadStrategy();

        Send send = getJmsOperationToTest(loadStrategy);

        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedMessageProducer jmsProducer = (EmbeddedMessageProducer)jmsSession.createProducer(jmsQueue);

        Producer endpoint = new Producer(jmsProducer, jmsSession);

        send.perform(endpoint);
    }

    @Test
    public void perform_SpecificMessageSize() throws Exception
    {
        int messageSize = 758;

        SendLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.setMessageSize(messageSize);

        Send send = getJmsOperationToTest(loadStrategy);

        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
        EmbeddedSession jmsSession = new EmbeddedSession(0, false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedMessageProducer jmsProducer = (EmbeddedMessageProducer)jmsSession.createProducer(jmsQueue);

        Producer endpoint = new Producer(jmsProducer, jmsSession);

        send.perform(endpoint);

        List<Message> messages = jmsProducer.getMessagesSentByThisProducer();
        assertEquals(1, messages.size());
        Message m = messages.get(0);
        assertEquals(messageSize, ((TextMessage)m).getText().length());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Send getJmsOperationToTest(JmsLoadStrategy loadStrategy)
    {
        return new Send((SendLoadStrategy)loadStrategy);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
