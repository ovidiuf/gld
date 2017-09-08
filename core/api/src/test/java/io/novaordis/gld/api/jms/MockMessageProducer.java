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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.jms.embedded.TestableMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockMessageProducer implements MessageProducer, TestableMessageProducer {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockMessageProducer.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean closed;

    private Destination destination;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMessageProducer(Destination d) {

        if (d == null) {

            throw new IllegalArgumentException("null destination");
        }

        this.closed = false;
        this.destination = d;
    }

    // TestableMessageProducer -----------------------------------------------------------------------------------------


    @Override
    public boolean isClosed() {

        return closed;
    }

    // MessageProducer implementation ----------------------------------------------------------------------------------

    @Override
    public void setDisableMessageID(boolean value) throws JMSException {
        throw new RuntimeException("setDisableMessageID() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getDisableMessageID() throws JMSException {
        throw new RuntimeException("getDisableMessageID() NOT YET IMPLEMENTED");
    }

    @Override
    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        throw new RuntimeException("setDisableMessageTimestamp() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        throw new RuntimeException("getDisableMessageTimestamp() NOT YET IMPLEMENTED");
    }

    @Override
    public void setDeliveryMode(int deliveryMode) throws JMSException {
        throw new RuntimeException("setDeliveryMode() NOT YET IMPLEMENTED");
    }

    @Override
    public int getDeliveryMode() throws JMSException {
        throw new RuntimeException("getDeliveryMode() NOT YET IMPLEMENTED");
    }

    @Override
    public void setPriority(int defaultPriority) throws JMSException {
        throw new RuntimeException("setPriority() NOT YET IMPLEMENTED");
    }

    @Override
    public int getPriority() throws JMSException {
        throw new RuntimeException("getPriority() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTimeToLive(long timeToLive) throws JMSException {
        throw new RuntimeException("setTimeToLive() NOT YET IMPLEMENTED");
    }

    @Override
    public long getTimeToLive() throws JMSException {
        throw new RuntimeException("getTimeToLive() NOT YET IMPLEMENTED");
    }

    @Override
    public Destination getDestination() throws JMSException {

        return destination;
    }

    @Override
    public void close() throws JMSException {

        this.closed = true;
    }

    @Override
    public void send(Message message) throws JMSException {

        ((MockQueue)destination).addMessage(message);
        log.info(this + " sending " + message);
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {

        throw new RuntimeException("send() NOT YET IMPLEMENTED");
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {

        throw new RuntimeException("send() NOT YET IMPLEMENTED");
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive)
            throws JMSException {

        throw new RuntimeException("send() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "MockMessageProducer[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
