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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Enumeration;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/7/17
 */
public class MockTextMessage implements TextMessage {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String text;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockTextMessage(String text) {

        this.text = text;
    }

    // TextMessage implementation --------------------------------------------------------------------------------------

    @Override
    public void setText(String string) throws JMSException {
        throw new RuntimeException("setText() NOT YET IMPLEMENTED");
    }

    @Override
    public String getText() throws JMSException {

        return text;
    }

    @Override
    public String getJMSMessageID() throws JMSException {

        return "mock-jms-message-id";
    }

    @Override
    public void setJMSMessageID(String id) throws JMSException {

        //
        // noop
        //
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        throw new RuntimeException("getJMSTimestamp() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException {
        throw new RuntimeException("setJMSTimestamp() NOT YET IMPLEMENTED");
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        throw new RuntimeException("getJMSCorrelationIDAsBytes() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        throw new RuntimeException("setJMSCorrelationIDAsBytes() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws JMSException {
        throw new RuntimeException("setJMSCorrelationID() NOT YET IMPLEMENTED");
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        throw new RuntimeException("getJMSCorrelationID() NOT YET IMPLEMENTED");
    }

    @Override
    public javax.jms.Destination getJMSReplyTo() throws JMSException {
        throw new RuntimeException("getJMSReplyTo() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSReplyTo(Destination replyTo) throws JMSException {
        throw new RuntimeException("setJMSReplyTo() NOT YET IMPLEMENTED");
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        throw new RuntimeException("getJMSDestination() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        throw new RuntimeException("setJMSDestination() NOT YET IMPLEMENTED");
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        throw new RuntimeException("getJMSDeliveryMode() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        throw new RuntimeException("setJMSDeliveryMode() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        throw new RuntimeException("getJMSRedelivered() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        throw new RuntimeException("setJMSRedelivered() NOT YET IMPLEMENTED");
    }

    @Override
    public String getJMSType() throws JMSException {
        throw new RuntimeException("getJMSType() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSType(String type) throws JMSException {
        throw new RuntimeException("setJMSType() NOT YET IMPLEMENTED");
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        throw new RuntimeException("getJMSExpiration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSExpiration(long expiration) throws JMSException {
        throw new RuntimeException("setJMSExpiration() NOT YET IMPLEMENTED");
    }

    @Override
    public int getJMSPriority() throws JMSException {
        throw new RuntimeException("getJMSPriority() NOT YET IMPLEMENTED");
    }

    @Override
    public void setJMSPriority(int priority) throws JMSException {
        throw new RuntimeException("setJMSPriority() NOT YET IMPLEMENTED");
    }

    @Override
    public void clearProperties() throws JMSException {
        throw new RuntimeException("clearProperties() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean propertyExists(String name) throws JMSException {
        throw new RuntimeException("propertyExists() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        throw new RuntimeException("getBooleanProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        throw new RuntimeException("getByteProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        throw new RuntimeException("getShortProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        throw new RuntimeException("getIntProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        throw new RuntimeException("getLongProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        throw new RuntimeException("getFloatProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        throw new RuntimeException("getDoubleProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        throw new RuntimeException("getStringProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        throw new RuntimeException("getObjectProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException {
        throw new RuntimeException("getPropertyNames() NOT YET IMPLEMENTED");
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
        throw new RuntimeException("setBooleanProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
        throw new RuntimeException("setByteProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
        throw new RuntimeException("setShortProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
        throw new RuntimeException("setIntProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
        throw new RuntimeException("setLongProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
        throw new RuntimeException("setFloatProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
        throw new RuntimeException("setDoubleProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException {
        throw new RuntimeException("setStringProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        throw new RuntimeException("setObjectProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public void acknowledge() throws JMSException {
        throw new RuntimeException("acknowledge() NOT YET IMPLEMENTED");
    }

    @Override
    public void clearBody() throws JMSException {
        throw new RuntimeException("clearBody() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return "\"" + text + "\"";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
