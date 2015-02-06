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
import com.novaordis.gld.service.jms.embedded.EmbeddedProducer;
import com.novaordis.gld.service.jms.embedded.EmbeddedQueue;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.jms.Session;

public class SendTest extends JmsOperationTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SendTest.class);

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
        EmbeddedSession jmsSession = new EmbeddedSession(false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedProducer jmsProducer = (EmbeddedProducer)jmsSession.createProducer(jmsQueue);

        Producer endpoint = new Producer(jmsProducer, jmsSession);

        send.perform(endpoint);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Send getJmsOperationToTest(JmsLoadStrategy loadStrategy)
    {
        return new Send(loadStrategy);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
