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

package io.novaordis.gld.api.jms.embedded;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedConnection implements Connection {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    // a closed connection cannot be reused
    private boolean closed;

    private List<EmbeddedSession> createdSessions;

    private int sessionCounter;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedConnection() {

        this.started = false;
        this.closed = false;
        this.createdSessions = new ArrayList<>();
        this.sessionCounter = 0;
    }

    // Connection implementation ---------------------------------------------------------------------------------------

    @Override
    public Session createSession(boolean b, int i) throws JMSException {

        EmbeddedSession s = new EmbeddedSession(sessionCounter++, b, i);
        createdSessions.add(s);
        return s;
    }

    @Override
    public String getClientID() throws JMSException {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setClientID(String s) throws JMSException {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws JMSException {

        started = true;
    }

    @Override
    public void stop() throws JMSException {

        started = false;
    }

    @Override
    public void close() throws JMSException {

        stop();
        closed = true;
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(
            Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException  {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(
            Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<EmbeddedSession> getCreatedSessions()
    {
        return createdSessions;
    }

    /**
     * May return an empty list if there were no message sent, or the queue does not exist.
     */
    public List<Message> getMessagesSentToDestination(String destinationName, boolean queue) throws Exception {

        List<Message> result = new ArrayList<>();

        for(EmbeddedSession s: createdSessions) {

            result.addAll(s.getMessagesSentToDestination(destinationName, queue));
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "EmbeddedJMSConnection[" + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
