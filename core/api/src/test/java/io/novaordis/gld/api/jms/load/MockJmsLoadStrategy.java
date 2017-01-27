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

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.Queue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public class MockJmsLoadStrategy extends MockLoadStrategy implements JmsLoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockJmsLoadStrategy() {

        this(new Queue("test-queue"));
    }

    public MockJmsLoadStrategy(Destination d) {

        this.destination = d;
    }

    // JmsLoadStrategy implementation ----------------------------------------------------------------------------------

    @Override
    public Destination getDestination() {

        return destination;
    }

    @Override
    public String getConnectionFactoryName() {

        return "mock-connection-factory-name";
    }

    @Override
    public ConnectionPolicy getConnectionPolicy() {

        //
        // default behavior
        //
        return ConnectionPolicy.CONNECTION_PER_RUN;
    }

    @Override
    public SessionPolicy getSessionPolicy() {

        //
        // default behavior
        //
        return SessionPolicy.SESSION_PER_OPERATION;
    }

    @Override
    public int getMessageSize() {
        throw new RuntimeException("getMessageSize() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getMessages() {
        throw new RuntimeException("getMessages() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getOperations() {
        throw new RuntimeException("getOperations() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getRemainingOperations() {
        throw new RuntimeException("getRemainingOperations() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
