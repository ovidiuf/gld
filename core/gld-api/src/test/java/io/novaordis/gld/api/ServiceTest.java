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

package io.novaordis.gld.api;

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void lifeCycle() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        assertFalse(s.isStarted());
//
//        s.start();
//
//        assertTrue(s.isStarted());
//
//        // starting an already started service instance should throw IllegalStateException
//
//        try {
//
//            s.start();
//            fail("should fail with IllegalStateException");
//        }
//        catch(IllegalStateException e) {
//            log.info(e.getMessage());
//        }
//
//        assertTrue(s.isStarted());
//
//        s.stop();
//
//        assertFalse(s.isStarted());
//
//        // stopping an already started stopped instance should be a noop
//
//        s.stop();
//
//        assertFalse(s.isStarted());
//    }

//    @Test
//    public void cannotPerformIfNotStarted() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        assertFalse(s.isStarted());
//
//        try {
//
//            s.perform(new MockOperation());
//            fail("should fail with IllegalStateException because the service is not started");
//        }
//        catch(IllegalStateException e) {
//            log.info(e.getMessage());
//        }
//    }

//    @Test
//    public void configureIgnoresArgumentsThatDoNotBelongToService() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        List<String> arguments = Arrays.asList(
//            "--this-argument-surely-is-not-interesting-to-the-service",
//            "apples",
//            "--this-argument-is-also-not-interesting-to-the-service",
//            "oranges"
//            );
//
//        s.configure(arguments);
//
//        // make sure no arguments were removed from list
//        assertEquals(4, arguments.size());
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Service getServiceToTest(Configuration configuration, List<Node> nodes) throws Exception;

    protected abstract Node getTestNode();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}