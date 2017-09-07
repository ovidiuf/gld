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

import javax.jms.*;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import java.io.Serializable;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockSession implements Session {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Session implementation ------------------------------------------------------------------------------------------

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        throw new RuntimeException("createBytesMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        throw new RuntimeException("createMapMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public Message createMessage() throws JMSException {
        throw new RuntimeException("createMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        throw new RuntimeException("createObjectMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
        throw new RuntimeException("createObjectMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        throw new RuntimeException("createStreamMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        throw new RuntimeException("createTextMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public TextMessage createTextMessage(String text) throws JMSException {
        throw new RuntimeException("createTextMessage() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getTransacted() throws JMSException {
        throw new RuntimeException("getTransacted() NOT YET IMPLEMENTED");
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        throw new RuntimeException("getAcknowledgeMode() NOT YET IMPLEMENTED");
    }

    @Override
    public void commit() throws JMSException {
        throw new RuntimeException("commit() NOT YET IMPLEMENTED");
    }

    @Override
    public void rollback() throws JMSException {
        throw new RuntimeException("rollback() NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws JMSException {
        throw new RuntimeException("close() NOT YET IMPLEMENTED");
    }

    @Override
    public void recover() throws JMSException {
        throw new RuntimeException("recover() NOT YET IMPLEMENTED");
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
    public void run() {
        throw new RuntimeException("run() NOT YET IMPLEMENTED");
    }

    @Override
    public MessageProducer createProducer(Destination destination) throws JMSException {
        throw new RuntimeException("createProducer() NOT YET IMPLEMENTED");
    }

    @Override
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        throw new RuntimeException("createConsumer() NOT YET IMPLEMENTED");
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        throw new RuntimeException("createConsumer() NOT YET IMPLEMENTED");
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal) throws JMSException {
        throw new RuntimeException("createConsumer() NOT YET IMPLEMENTED");
    }

    @Override
    public Queue createQueue(String queueName) throws JMSException {
        throw new RuntimeException("createQueue() NOT YET IMPLEMENTED");
    }

    @Override
    public Topic createTopic(String topicName) throws JMSException {
        throw new RuntimeException("createTopic() NOT YET IMPLEMENTED");
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        throw new RuntimeException("createDurableSubscriber() NOT YET IMPLEMENTED");
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        throw new RuntimeException("createDurableSubscriber() NOT YET IMPLEMENTED");
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new RuntimeException("createBrowser() NOT YET IMPLEMENTED");
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        throw new RuntimeException("createBrowser() NOT YET IMPLEMENTED");
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new RuntimeException("createTemporaryQueue() NOT YET IMPLEMENTED");
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        throw new RuntimeException("createTemporaryTopic() NOT YET IMPLEMENTED");
    }

    @Override
    public void unsubscribe(String name) throws JMSException {
        throw new RuntimeException("unsubscribe() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
