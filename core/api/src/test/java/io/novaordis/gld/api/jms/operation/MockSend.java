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

package io.novaordis.gld.api.jms.operation;

import io.novaordis.gld.api.jms.load.JmsLoadStrategy;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/17
 */
public class MockSend extends Send {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockSend(JmsLoadStrategy loadStrategy) {

        super(loadStrategy);
    }

    // JmsOperation implementation -------------------------------------------------------------------------------------

//    @Override
//    public String getKey() {
//        throw new RuntimeException("getKey() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void perform(Service s) throws Exception {
//        throw new RuntimeException("perform() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean wasPerformed() {
//        throw new RuntimeException("wasPerformed() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public boolean wasSuccessful() {
//        throw new RuntimeException("wasSuccessful() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public JmsLoadStrategy getLoadStrategy() {
//        throw new RuntimeException("getLoadStrategy() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public String getPayload() {
//        throw new RuntimeException("getPayload() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public Destination getDestination() {
//
//        return destination;
//    }
//
//    @Override
//    public String getId() {
//        throw new RuntimeException("getId() NOT YET IMPLEMENTED");
//    }
//
//    @Override
//    public void perform(JmsEndpoint endpoint) throws Exception {
//        throw new RuntimeException("perform() NOT YET IMPLEMENTED");
//    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
