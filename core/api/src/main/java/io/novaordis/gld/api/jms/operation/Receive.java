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

package io.novaordis.gld.api.jms.operation;

import io.novaordis.gld.api.jms.Consumer;
import io.novaordis.gld.api.jms.JmsEndpoint;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

public class Receive extends JmsOperationBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(Receive.class);
    private static final boolean trace = log.isTraceEnabled();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long timeoutMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Receive(ReceiveLoadStrategy loadStrategy) {

        super(loadStrategy);
        this.timeoutMs = loadStrategy.getTimeoutMs();
    }

    // JmsOperationBase overrides --------------------------------------------------------------------------------------

    @Override
    public void perform(JmsEndpoint endpoint) throws Exception {

        Consumer consumer = (Consumer)endpoint;
        MessageConsumer jmsConsumer = consumer.getConsumer();

        Message m;

        if (timeoutMs == null) {

            m = jmsConsumer.receive();
        }
        else {

            m = jmsConsumer.receive(timeoutMs);
        }

        if (trace) {

            String messageID = m.getJMSMessageID();
            String textPayload = null;
            if (m instanceof TextMessage) {

                textPayload = ((TextMessage)m).getText();
            }

            log.trace(messageID + ": " + (textPayload == null ? "0:null" : textPayload.length() + ":" + textPayload));
        }
    }

    @Override
    public boolean wasPerformed() {
        throw new RuntimeException("wasPerformed() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean wasSuccessful() {
        throw new RuntimeException("wasSuccessful() NOT YET IMPLEMENTED");
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
    public String toString() {

        return "NOT YET IMPLEMENTED";

//        Destination d = getDestination();
//        String name = d.getName();
//
//        return
//            "receive[" +
//                ((d.isQueue() ? "queue=" : "topic=") + name) +
//                (", timeout=" + (getTimeoutMs() == null ? "0" : getTimeoutMs())) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
