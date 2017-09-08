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

package io.novaordis.gld.api.jms.embedded;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EmbeddedDestination implements javax.jms.Destination, TestableDestination {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;

    private Queue<Message> messages;
    private List<Message> messagesSent;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedDestination(String name) {

        this.name = name;
        this.messages = new ConcurrentLinkedQueue<>();
        this.messagesSent = new ArrayList<>();
    }

    // TestableDestination implementation ------------------------------------------------------------------------------

    @Override
    public List<Message> getMessagesSent() {

        return messagesSent;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getName()  {

        return name;
    }

    public void add(Message m) {

        messages.add(m);
        messagesSent.add(m);
    }

    public Message get(long timeoutMs) {

        Message m = messages.poll();

        if (m == null) {

            //
            // simulate timeout
            //

            if (timeoutMs > 0) {

                try {

                    Thread.sleep(timeoutMs);
                }
                catch(Exception e) {

                    throw new IllegalArgumentException(e);
                }
            }
        }

        return m;
    }

    public boolean isEmpty() {

        return messages.isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
