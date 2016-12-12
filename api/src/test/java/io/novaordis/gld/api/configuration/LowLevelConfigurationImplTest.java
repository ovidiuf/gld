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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class LowLevelConfigurationImplTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LowLevelConfigurationImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void nullConfigurationFileDirectory() throws Exception {

        try {

            new LowLevelConfigurationImpl(new HashMap<>(), null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null configuration directory", msg);
        }
    }

    @Test
    public void configurationFileDirectoryDoesNotExist() throws Exception {

        try {

            new LowLevelConfigurationImpl(new HashMap<>(), new File("/I/am/sure/this/directory/does/not/exist"));
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("configuration directory /I/am/sure/this/directory/does/not/exist does not exist", msg);
        }
    }

    @Test
    public void configurationFileDirectoryIsAFile() throws Exception {

        File existingFile = new File(System.getProperty("basedir"), "pom.xml");
        assertTrue(existingFile.isFile());
        assertTrue(existingFile.exists());

        try {

            new LowLevelConfigurationImpl(new HashMap<>(), existingFile);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("the path " + existingFile.getPath() + " does not represent a directory", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected LowLevelConfigurationImpl getLowLevelConfigurationToTest(
            Map<String, Object> raw, File configurationDirectory) throws Exception {

        return new LowLevelConfigurationImpl(raw, configurationDirectory);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
