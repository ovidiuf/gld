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

import io.novaordis.gld.api.jms.embedded.TestableQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockQueue implements javax.jms.Queue, TestableQueue {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Message> messages;
    private List<Message> messagesSent;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockQueue() {

        this.messages = new ArrayList<>();
        this.messagesSent = new ArrayList<>();
    }

    // Queue implementation --------------------------------------------------------------------------------------------

    @Override
    public String getQueueName() throws JMSException {

        throw new RuntimeException("getQueueName() NOT YET IMPLEMENTED");
    }

    // TestableQueue implementation ------------------------------------------------------------------------------------

    @Override
    public List<Message> getMessagesSent() {

        return messagesSent;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void addMessage(Message m) {

        messages.add(m);
        messagesSent.add(m);
    }

    public Message receive(long timeout) {

        if (messages.isEmpty()) {

            return null;
        }

        return messages.remove(0);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
