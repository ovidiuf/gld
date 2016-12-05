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

package io.novaordis.gld.api;

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class LoadDriverConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadDriverConfigurationTest.class);


    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void threadCount_NotAnInteger() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put(LoadDriverConfiguration.THREAD_COUNT_LABEL, "blah");

        try {

            getLoadDriverConfigurationToTest(map);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals(msg, "'" + LoadDriverConfiguration.THREAD_COUNT_LABEL + "' not an integer: \"blah\"");
        }
    }

    @Test
    public void threadCountMissing_DefaultValue() throws Exception {

        Map<String, String> map = new HashMap<>();
        assertNull(map.get(LoadDriverConfiguration.THREAD_COUNT_LABEL));

        LoadDriverConfiguration c = getLoadDriverConfigurationToTest(map);
        assertEquals(LoadDriverConfiguration.DEFAULT_THREAD_COUNT, c.getThreadCount());
    }


    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract LoadDriverConfiguration getLoadDriverConfigurationToTest(Map map) throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
