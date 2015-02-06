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
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class Send extends JmsOperation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Send(JmsLoadStrategy loadStrategy)
    {
        super(loadStrategy);
    }

    // JMSOperation overrides ------------------------------------------------------------------------------------------

    @Override
    public void perform(JmsEndpoint endpoint) throws Exception
    {
        Producer producerEndpoint = (Producer)endpoint;
        Session session = producerEndpoint.getSession();
        MessageProducer jmsProducer = producerEndpoint.getProducer();
        Message m = session.createTextMessage();
        jmsProducer.send(m);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
