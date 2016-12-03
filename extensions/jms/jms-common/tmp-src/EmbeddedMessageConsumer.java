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

package com.novaordis.gld.service.jms.embedded;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class EmbeddedMessageConsumer implements MessageConsumer
{

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    private boolean closed;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedMessageConsumer(Destination destination)
    {
        this.destination = destination;
        this.closed = false;
    }

    // MessageConsumer implementation ----------------------------------------------------------------------------------

    @Override
    public String getMessageSelector() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public MessageListener getMessageListener() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setMessageListener(MessageListener messageListener) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Message receive() throws JMSException
    {
        return new EmbeddedTextMessage("TEST");
    }

    @Override
    public Message receive(long l) throws JMSException
    {
        return new EmbeddedTextMessage("TEST");
    }

    @Override
    public Message receiveNoWait() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws JMSException
    {
        closed = true;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "EmbeddedConsumer[" + destination + "]";
    }

    public boolean isClosed()
    {
        return closed;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
