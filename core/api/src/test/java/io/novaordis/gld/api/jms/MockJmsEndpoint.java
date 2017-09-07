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

package io.novaordis.gld.api.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Session;

public class MockJMSEndpoint implements JMSEndpoint {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockJMSEndpoint.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean closed;
    private Session session;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockJMSEndpoint() {

        this.closed = false;
    }

    // JMSEndpoint implementation --------------------------------------------------------------------------------------

    @Override
    public void close() throws Exception {

        this.closed = true;
    }

    @Override
    public Session getSession() {

        return session;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public boolean isClosed() {

        return closed;
    }

    public void setSession(Session s) {

        this.session = s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
