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
import io.novaordis.gld.api.jms.JMSEndpoint;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
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

    private Message message;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Receive(JMSLoadStrategy ls) {

        super(ls);

        ReceiveLoadStrategy receiveLoadStrategy = (ReceiveLoadStrategy)ls;
        this.timeoutMs = receiveLoadStrategy.getTimeoutMs();
    }

    // JmsOperationBase overrides --------------------------------------------------------------------------------------

    @Override
    public void perform(JMSEndpoint endpoint) throws Exception {

        MessageConsumer jmsConsumer = ((Consumer) endpoint).getConsumer();

        if (timeoutMs == null) {

            message = jmsConsumer.receive();
        }
        else {

            message = jmsConsumer.receive(timeoutMs);
        }

        if (trace) {

            if (message == null) {

                log.trace("receive returned null");
            }
            else {

                String messageID = message.getJMSMessageID();
                String textPayload = null;
                if (message instanceof TextMessage) {

                    textPayload = ((TextMessage) message).getText();
                }

                log.trace(messageID + ": " + (textPayload == null ? "0:null" : textPayload.length() + ":" + textPayload));
            }
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

    @Override
    public String getPayload() {

        if (message == null) {

            return null;
        }

        if (message instanceof TextMessage) {

            try {
                return ((TextMessage) message).getText();
            }
            catch(Exception e) {

                throw new IllegalStateException(e);
            }
        }

        throw new RuntimeException("DON'T KNOW HOW TO HANDLE " + message);
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

        JMSLoadStrategy ls = getLoadStrategy();

        if (ls == null) {

            return "uninitialized";
        }

        io.novaordis.gld.api.jms.Destination d = ls.getDestination();

        return d + " timeout=" + (getTimeoutMs() == null ? "0" : getTimeoutMs());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
