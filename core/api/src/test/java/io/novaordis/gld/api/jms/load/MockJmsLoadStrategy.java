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
import io.novaordis.gld.api.jms.embedded.EmbeddedJMSService;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public class MockJmsLoadStrategy extends MockLoadStrategy implements JmsLoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    private SessionPolicy sessionPolicy;

    private String username;
    private char[] password;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockJmsLoadStrategy() {

        this(new Queue("test-queue"));
    }

    public MockJmsLoadStrategy(Destination d) {

        this.destination = d;

        //
        // default behavior
        //

        this.sessionPolicy = SessionPolicy.SESSION_PER_OPERATION;
    }

    // JmsLoadStrategy implementation ----------------------------------------------------------------------------------

    @Override
    public Destination getDestination() {

        return destination;
    }

    @Override
    public String getConnectionFactoryName() {

        return EmbeddedJMSService.DEFAULT_CONNECTION_FACTORY_NAME;
    }

    @Override
    public String getUsername() {

        return username;
    }

    @Override
    public char[] getPassword() {

        return password;
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

        return sessionPolicy;
    }

    @Override
    public Long getRemainingOperations() {
        throw new RuntimeException("getRemainingOperations() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setSessionPolicy(SessionPolicy sessionPolicy) {

        this.sessionPolicy = sessionPolicy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
