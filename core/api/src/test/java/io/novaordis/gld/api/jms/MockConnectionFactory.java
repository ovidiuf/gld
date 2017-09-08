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


import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public class MockConnectionFactory implements javax.jms.ConnectionFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String validUser;
    private String validPassword;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockConnectionFactory(String validUser, String validPassword) {

        this.validUser = validUser;
        this.validPassword = validPassword;
    }

    // ConnectionFactory implementation --------------------------------------------------------------------------------

    @Override
    public Connection createConnection() throws JMSException {

        return new MockConnection();
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {

        if (validUser == null) {

            throw new JMSException("AUTHENTICATION FAILURE");
        }

        if (validUser.equals(userName) && validPassword.equals(password)) {

            return new MockConnection();
        }

        throw new JMSException("AUTHENTICATION FAILURE");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
