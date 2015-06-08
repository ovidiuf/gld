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
import com.novaordis.gld.service.jms.JmsEndpoint;
import com.novaordis.gld.strategy.load.jms.Destination;
import com.novaordis.gld.strategy.load.jms.ReceiveLoadStrategy;
import org.apache.log4j.Logger;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

public class Receive extends JmsOperation
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Receive.class);
    private static final boolean trace = log.isTraceEnabled();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long timeoutMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Receive(ReceiveLoadStrategy loadStrategy)
    {
        super(loadStrategy);
        this.timeoutMs = loadStrategy.getTimeoutMs();
    }

    // JmsOperation overrides ------------------------------------------------------------------------------------------

    @Override
    public void perform(JmsEndpoint endpoint) throws Exception
    {
        Consumer consumer = (Consumer)endpoint;
        MessageConsumer jmsConsumer = consumer.getConsumer();

        Message m;

        if (timeoutMs == null)
        {
            m = jmsConsumer.receive();
        }
        else
        {
            m = jmsConsumer.receive(timeoutMs);
        }

        if (trace)
        {
            String messageID = m.getJMSMessageID();
            String textPayload = null;
            if (m instanceof TextMessage)
            {
                textPayload = ((TextMessage)m).getText();
            }

            log.trace(messageID + ": " + (textPayload == null ? "0:null" : textPayload.length() + ":" + textPayload));
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return null if there is no receive timeout
     */
    public Long getTimeoutMs()
    {
        return timeoutMs;
    }

    @Override
    public String toString()
    {
        Destination d = getDestination();
        String name = d.getName();

        return
            "receive[" +
                ((d.isQueue() ? "queue=" : "topic=") + name) +
                (", timeout=" + (getTimeoutMs() == null ? "0" : getTimeoutMs())) + "]";

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
