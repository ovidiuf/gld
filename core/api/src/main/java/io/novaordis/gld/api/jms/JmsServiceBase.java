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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.jms.load.ConnectionPolicy;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.jms.load.SessionPolicy;
import io.novaordis.gld.api.jms.operation.JmsOperation;
import io.novaordis.gld.api.service.ServiceBase;
import io.novaordis.gld.api.service.ServiceType;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public abstract class JmsServiceBase extends ServiceBase implements JmsService {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private javax.jms.ConnectionFactory connectionFactory;
    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsService implementation ---------------------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return ServiceType.jms;
    }

    @Override
    public Session checkOut(JmsOperation jmsOperation) throws Exception {

        //
        // look at connection policy and at the session policy
        //

        Session session;

        if (SessionPolicy.SESSION_PER_OPERATION.equals(sessionPolicy)) {

            //
            // we need a new session
            //

            Connection c = getConnection();

            session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        else if (SessionPolicy.SESSION_PER_THREAD.equals(sessionPolicy)) {

            //
            // get the session from the thread, and if not available, create one
            //

            throw new RuntimeException("NOT YET IMPLEMENTED");
        }
        else {

            throw new RuntimeException(sessionPolicy + " SUPPORT NOT YET IMPLEMENTED");
        }

        return session;
    }

    @Override
    public void checkIn(Session session) throws Exception {

        //
        // look at the session policy and handle accordingly
        //

        if (SessionPolicy.SESSION_PER_OPERATION.equals(sessionPolicy)) {

            //
            // done with it, close
            //

            session.close();
        }
        else if (SessionPolicy.SESSION_PER_THREAD.equals(sessionPolicy)) {

            //
            // get the session from the thread, and if not available, create one
            //

            throw new RuntimeException("NOT YET IMPLEMENTED");
        }
        else {

            throw new RuntimeException(sessionPolicy + " SUPPORT NOT YET IMPLEMENTED");
        }
    }

    /**
     * We intercept the method that installs the load strategy to get some configuration from it.
     */
    @Override
    public void setLoadStrategy(LoadStrategy s) {

        if (!(s instanceof JmsLoadStrategy)) {

            throw new IllegalArgumentException("invalid load strategy " + s);
        }

        super.setLoadStrategy(s);

        JmsLoadStrategy jmsLoadStrategy = (JmsLoadStrategy)s;

        this.connectionPolicy = jmsLoadStrategy.getConnectionPolicy();
        this.sessionPolicy = jmsLoadStrategy.getSessionPolicy();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    Connection getConnection() throws Exception {

        return connectionFactory.createConnection();
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConnectionFactory(javax.jms.ConnectionFactory cf) {

        this.connectionFactory = cf;
    }

    protected javax.jms.ConnectionFactory getConnectionFactory() {

        return connectionFactory;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
