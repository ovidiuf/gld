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

package io.novaordis.gld.api.configuration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public abstract class OutputConfigurationTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(OutputConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getStatisticsLocation_Null() throws Exception {

        fail("return here");

//        Map<String, Object> rawMap = new HashMap<>();
//
//        StoreConfiguration c = getConfigurationToTest(rawMap, new File(System.getProperty("basedir")));
//
//        try {
//
//            c.getStoreType();
//            fail("should throw exception");
//        }
//        catch(UserErrorException e) {
//
//            String msg = e.getMessage();
//            log.info(msg);
//            assertEquals("missing store type", msg);
//        }
    }

    @Test
    public void getStatisticsLocation_NotAString() throws Exception {

        fail("return here");


//        Map<String, Object> rawMap = new HashMap<>();
//        rawMap.put(StoreConfiguration.STORE_TYPE_LABEL, 10);
//
//        StoreConfiguration c = getConfigurationToTest(rawMap, new File(System.getProperty("basedir")));
//
//        try {
//
//            c.getStoreType();
//            fail("should throw exception");
//        }
//        catch(IllegalStateException e) {
//
//            String msg = e.getMessage();
//            log.info(msg);
//            assertEquals(
//                    "expected " + StoreConfiguration.STORE_TYPE_LABEL + " to be a String but it is a(n) Integer: \"10\"",
//                    msg);
//        }
    }

    @Test
    public void getStatisticsLocation() throws Exception {

        fail("return here");


//        Map<String, Object> rawMap = new HashMap<>();
//        rawMap.put(StoreConfiguration.STORE_TYPE_LABEL, "test");
//
//        StoreConfiguration c = getConfigurationToTest(rawMap, new File(System.getProperty("basedir")));
//
//        assertEquals("test", c.getStoreType());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected abstract OutputConfiguration getConfigurationToTest(
            Map<String, Object> rawMap, File configurationDirectory) throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
