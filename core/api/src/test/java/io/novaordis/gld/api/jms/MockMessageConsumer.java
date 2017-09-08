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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockMessageConsumer implements MessageConsumer {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockMessageConsumer.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMessageConsumer(Destination d) {

        if (d == null) {

            throw new IllegalArgumentException("null destination");
        }

        this.destination = d;
    }

    // MessageConsumer implementation ----------------------------------------------------------------------------------

    @Override
    public String getMessageSelector() throws JMSException {
        throw new RuntimeException("getMessageSelector() NOT YET IMPLEMENTED");
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        throw new RuntimeException("getMessageListener() NOT YET IMPLEMENTED");
    }

    @Override
    public void setMessageListener(MessageListener listener) throws JMSException {
        throw new RuntimeException("setMessageListener() NOT YET IMPLEMENTED");
    }

    @Override
    public Message receive() throws JMSException {
        throw new RuntimeException("receive() NOT YET IMPLEMENTED");
    }

    @Override
    public Message receive(long timeout) throws JMSException {

        MockQueue mockQueue = (MockQueue)destination;

        return mockQueue.receive(timeout);
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        throw new RuntimeException("receiveNoWait() NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws JMSException {
        throw new RuntimeException("close() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "MockMessageConsumer[" + Integer.toHexString(System.identityHashCode(this)) + ", " + destination + "]";
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
