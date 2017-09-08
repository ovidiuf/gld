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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public class MockJNDIBasedJMSServiceTest extends JNDIBasedJMSServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JNDIBasedJMSService getJNDIBasedJMSServiceToTest() {

        MockJNDIBasedJMSService service = new MockJNDIBasedJMSService();

        //
        // configure it minimally so it can be started with the correct LoadStrategy
        //

        service.setJndiUrl("mock://mock-jndi-server");
        service.setNamingInitialContextFactoryClassName(MockInitialContextFactory.class.getName());

        return service;
    }

    @Override
    protected void placeTextMessageInQueue(JMSService service, String text, String queueNme) {

        MockQueue queue = (MockQueue)MockInitialContextFactory.getJndiSpace().get(queueNme);
        if (queue == null) {

            queue = new MockQueue();
            MockInitialContextFactory.getJndiSpace().put(queueNme, queue);
        }

        queue.addMessage(new MockTextMessage(text));
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
