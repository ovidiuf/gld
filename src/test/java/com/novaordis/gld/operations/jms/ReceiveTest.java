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

import com.novaordis.gld.service.jms.Consumer;
import com.novaordis.gld.service.jms.embedded.EmbeddedConsumer;
import com.novaordis.gld.service.jms.embedded.EmbeddedQueue;
import com.novaordis.gld.service.jms.embedded.EmbeddedSession;
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;
import com.novaordis.gld.strategy.load.jms.ReceiveLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.jms.Session;

public class ReceiveTest extends JmsOperationTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReceiveTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void perform_NoTimeout() throws Exception
    {
        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        Receive receive = getJmsOperationToTest(loadStrategy);

        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
        EmbeddedSession jmsSession = new EmbeddedSession(false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedConsumer jmsConsumer = (EmbeddedConsumer)jmsSession.createConsumer(jmsQueue);

        Consumer endpoint = new Consumer(jmsConsumer, jmsSession);

        receive.perform(endpoint);
    }

    @Test
    public void perform_WithTimeout() throws Exception
    {
        ReceiveLoadStrategy loadStrategy = new ReceiveLoadStrategy();
        loadStrategy.setTimeoutMs(7L);

        Receive receive = getJmsOperationToTest(loadStrategy);

        EmbeddedQueue jmsQueue = new EmbeddedQueue("TEST");
        EmbeddedSession jmsSession = new EmbeddedSession(false, Session.AUTO_ACKNOWLEDGE);
        EmbeddedConsumer jmsConsumer = (EmbeddedConsumer)jmsSession.createConsumer(jmsQueue);

        Consumer endpoint = new Consumer(jmsConsumer, jmsSession);

        receive.perform(endpoint);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Receive getJmsOperationToTest(JmsLoadStrategy loadStrategy)
    {
        return new Receive((ReceiveLoadStrategy)loadStrategy);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
