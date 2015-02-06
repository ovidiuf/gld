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
import javax.jms.MessageProducer;

public class EmbeddedMessageProducer implements MessageProducer
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;
    private boolean closed;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedMessageProducer(Destination destination)
    {
        this.destination = destination;
        this.closed = false;
    }

    // MessageProducer implementation ----------------------------------------------------------------------------------
    @Override
    public void setDisableMessageID(boolean b) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getDisableMessageID() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setDisableMessageTimestamp(boolean b) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setDeliveryMode(int i) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public int getDeliveryMode() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setPriority(int i) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public int getPriority() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setTimeToLive(long l) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public long getTimeToLive() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Destination getDestination() throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws JMSException
    {
        closed = true;
    }

    @Override
    public void send(Message message) throws JMSException
    {
        // noop for the time being
    }

    @Override
    public void send(Message message, int i, int i1, long l) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void send(Destination destination, Message message, int i, int i1, long l) throws JMSException
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "EmbeddedProducer[" + destination + "]";
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
