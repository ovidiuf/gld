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

import com.novaordis.gld.service.jms.JmsEndpoint;
import com.novaordis.gld.service.jms.Producer;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import org.apache.log4j.Logger;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Send extends JmsOperation
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Send.class);
    private static final boolean trace = log.isTraceEnabled();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String payload;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Send(SendLoadStrategy sendLoadStrategy)
    {
        super(sendLoadStrategy);

        // create the payload outside the perform() method to influence as little as possible the execution duration;
        // in this specific case we reuse the message created by the strategy (and presumably cached), because we are
        // not interested creating distinct message bodies, we're only interested in the payload length
        payload = sendLoadStrategy.getMessagePayload();
    }

    // JmsOperation overrides ------------------------------------------------------------------------------------------

    @Override
    public void perform(JmsEndpoint endpoint) throws Exception
    {
        Producer producerEndpoint = (Producer)endpoint;
        Session session = producerEndpoint.getSession();
        MessageProducer jmsProducer = producerEndpoint.getProducer();

        TextMessage m = session.createTextMessage(payload);

        if (trace) { log.trace("sending message with payload \"" + payload + "\""); }

        jmsProducer.send(m);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May be null.
     */
    public String getPayload()
    {
        return payload;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
