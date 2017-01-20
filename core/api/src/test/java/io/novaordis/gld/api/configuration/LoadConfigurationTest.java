/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package io.novaordis.gld.api.configuration;

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class LoadConfigurationTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadConfigurationTest.class);


    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // thread count ----------------------------------------------------------------------------------------------------

    @Test
    public void threadCount_NotAnInteger() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.THREAD_COUNT_LABEL, "blah");
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getThreadCount();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.THREAD_COUNT_LABEL + "' not an integer.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }
    }

    @Test
    public void threadCountMissing_DefaultValue() throws Exception {

        Map<String, Object> map = new HashMap<>();
        assertNull(map.get(LoadConfiguration.THREAD_COUNT_LABEL));

        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));
        assertEquals(LoadConfiguration.DEFAULT_THREAD_COUNT, c.getThreadCount());
    }

    // getOperations()/getRequests()/getMessages() ---------------------------------------------------------------------

    @Test
    public void unlimited() throws Exception {

        //
        // the default configuration allows for unlimited load
        //

        Map<String, Object> map = new HashMap<>();
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        assertNull(c.getOperations());
        assertNull(c.getRequests());
        assertNull(c.getMessages());
    }

    @Test
    public void getOperations_OperationsNotALong() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.OPERATION_COUNT_LABEL, "blah");
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getOperations();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.OPERATION_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getRequests();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.OPERATION_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getMessages();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.OPERATION_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }
    }

    @Test
    public void getOperations_RequestsNotALong() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.REQUEST_COUNT_LABEL, "blah");
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getOperations();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.REQUEST_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getRequests();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.REQUEST_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getMessages();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.REQUEST_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }
    }

    @Test
    public void getOperations_MessagesNotALong() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.MESSAGE_COUNT_LABEL, "blah");
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        try {

            c.getOperations();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.MESSAGE_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getRequests();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.MESSAGE_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }

        try {

            c.getMessages();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("'" + LoadConfiguration.MESSAGE_COUNT_LABEL + "' not a long.*"));

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            String msg2 = e2.getMessage();
            assertTrue(msg2.contains("blah"));
        }
    }

    @Test
    public void getOperations_OperationLabel() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.OPERATION_COUNT_LABEL, 10);
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        Long l = c.getOperations();
        assertEquals(10, l.longValue());

        Long l2 = c.getRequests();
        assertEquals(10, l2.longValue());

        Long l3 = c.getMessages();
        assertEquals(10, l3.longValue());
    }

    @Test
    public void getOperations_RequestLabel() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.REQUEST_COUNT_LABEL, 10);
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        Long l = c.getOperations();
        assertEquals(10, l.longValue());

        Long l2 = c.getRequests();
        assertEquals(10, l2.longValue());

        Long l3 = c.getMessages();
        assertEquals(10, l3.longValue());
    }

    @Test
    public void getOperations_MessageLabel() throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put(LoadConfiguration.MESSAGE_COUNT_LABEL, 10);
        LoadConfiguration c = getConfigurationToTest(map, new File(System.getProperty("basedir")));

        Long l = c.getOperations();
        assertEquals(10, l.longValue());

        Long l2 = c.getRequests();
        assertEquals(10, l2.longValue());

        Long l3 = c.getMessages();
        assertEquals(10, l3.longValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected abstract LoadConfiguration getConfigurationToTest(
            Map<String, Object> rawMap, File configurationDirectory) throws Exception;


    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
