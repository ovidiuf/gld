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

import io.novaordis.gld.api.jms.embedded.TestableConnection;
import io.novaordis.gld.api.jms.embedded.TestableSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockConnection implements Connection, TestableConnection {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockConnection.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean closed;

    private List<MockSession> createdSessions;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockConnection() {

        this.createdSessions = new ArrayList<>();
    }

    // TestableConnection implementation -------------------------------------------------------------------------------

    @Override
    public List<TestableSession> getCreatedSessions() {

        List<TestableSession> result = new ArrayList<>();

        for(MockSession s: createdSessions) {

            result.add(s);
        }

        return result;
    }

    // Connection implementation ---------------------------------------------------------------------------------------

    @Override
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {

        MockSession s = new MockSession();
        createdSessions.add(s);
        return s;
    }

    @Override
    public String getClientID() throws JMSException {
        throw new RuntimeException("getClientID() NOT YET IMPLEMENTED");
    }

    @Override
    public void setClientID(String clientID) throws JMSException {
        throw new RuntimeException("setClientID() NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        throw new RuntimeException("getMetaData() NOT YET IMPLEMENTED");
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        throw new RuntimeException("getExceptionListener() NOT YET IMPLEMENTED");
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        throw new RuntimeException("setExceptionListener() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws JMSException {

        this.closed = true;

        log.info(this + " started");
    }

    @Override
    public void stop() throws JMSException {

        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws JMSException {

        for(MockSession s: createdSessions) {

            s.close();
        }

        log.info(this + " closed");
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new RuntimeException("createConnectionConsumer() NOT YET IMPLEMENTED");
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new RuntimeException("createDurableConnectionConsumer() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "MockConnection[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    public boolean isClosed() {

        return closed;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
