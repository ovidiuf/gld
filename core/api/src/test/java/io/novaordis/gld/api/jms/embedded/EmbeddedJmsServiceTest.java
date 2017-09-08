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

import io.novaordis.gld.api.jms.JMSService;
import io.novaordis.gld.api.jms.JMSServiceTest;
import io.novaordis.gld.api.jms.Queue;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
import io.novaordis.gld.api.jms.load.MockJMSLoadStrategy;
import io.novaordis.gld.api.service.Service;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public class EmbeddedJMSServiceTest extends JMSServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JMSService getJMSServiceToTest() throws Exception {

        return new EmbeddedJMSService();
    }

    @Override
    protected JMSLoadStrategy getMatchingLoadStrategyToTest(Service s) throws Exception {

        MockJMSLoadStrategy ms = new MockJMSLoadStrategy();

        //
        // create the associated JMS objects in context
        //

        String destinationName = ms.getDestination().getName();
        createDestinationInContext((JMSService)s, destinationName);

        String connectionFactoryName = ms.getConnectionFactoryName();
        String username = ms.getUsername();
        char[] ca = ms.getPassword();
        String password = ca == null ? null : new String(ca);
        createConnectionFactoryInContext((JMSService)s, connectionFactoryName, username, password);

        return ms;
    }

    @Override
    protected void placeTextMessageInQueue(JMSService service, String text, String queueNme) {

        EmbeddedJMSService es = (EmbeddedJMSService)service;
        es.addToDestination(queueNme, true, new EmbeddedTextMessage(text));
    }

    @Override
    protected void createDestinationInContext(JMSService service, String destinationJndiName) throws Exception {

        //
        // create a queue
        //

        EmbeddedJMSService es = (EmbeddedJMSService)service;
        es.createDestination(new Queue(destinationJndiName));
    }

    @Override
    protected void removeDestinationFromContext(JMSService service, String destinationJndiName) throws Exception {

        EmbeddedJMSService es = (EmbeddedJMSService)service;
        es.removeDestination(destinationJndiName);
    }

    @Override
    protected void createConnectionFactoryInContext(
            JMSService service, String connectionFactoryJndiName, String username, String password) throws Exception {

        //
        // declare the connection factory
        //

        EmbeddedJMSService es = (EmbeddedJMSService)service;
        es.installConnectionFactory(connectionFactoryJndiName, username, password);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
