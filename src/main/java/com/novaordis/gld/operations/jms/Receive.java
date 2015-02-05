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

import com.novaordis.gld.strategy.load.jms.Destination;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class Receive extends JmsOperation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long timeoutMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Receive(Destination destination)
    {
        super(destination);
    }

    // JMSOperation overrides ------------------------------------------------------------------------------------------

    @Override
    public void perform(Session session) throws Exception
    {
        javax.jms.Destination jmsDestination = getJmsDestination(session);
        MessageConsumer c = session.createConsumer(jmsDestination);

        Message m;

        if (timeoutMs == null)
        {
            m = c.receive();
        }
        else
        {
            m = c.receive(timeoutMs);
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

    public void setTimeoutMs(Long timeoutMs)
    {
        this.timeoutMs = timeoutMs;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
