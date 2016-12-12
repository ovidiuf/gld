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

import io.novaordis.utilities.Files;
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
 * @since 12/8/16
 */
public abstract class LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LowLevelConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // get() -----------------------------------------------------------------------------------------------------------

    @Test
    public void get_NoMatch_FirstElement() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        String s = c.get(String.class, "no-such-top-element");
        assertNull(s);
    }

    @Test
    public void get_NoMatch_PartialMatch() throws Exception {

        Map<String, Object> m = new HashMap<>();
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Object> m2 = new HashMap<>();

        m.put("token1", m2);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        String s = c.get(String.class, "token1", "token2");
        assertNull(s);
    }

    @Test
    public void get_NoMatch_IntermediateElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", "a-string-not-a-map");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        String s = c.get(String.class, "token1", "token2", "token3");
        assertNull(s);
    }

    @Test
    public void get_Match_NotTheExpectedType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", m3);
        m3.put("token3", 10);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        try {
            c.get(String.class, "token1", "token2", "token3");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected token1.token2.token3 to be a String but it is a(n) Integer: \"10\"", msg);
        }
    }

    @Test
    public void get() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", m3);
        m3.put("token3", "test-value");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        String s = c.get(String.class, "token1", "token2", "token3");

        assertEquals("test-value", s);
    }

    // getFile() -------------------------------------------------------------------------------------------------------

    @Test
    public void nullConfigurationFileDirectory() throws Exception {

        try {

            new LowLevelConfigurationBase(new HashMap<>(), null);
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

            new LowLevelConfigurationBase(new HashMap<>(), new File("/I/am/sure/this/directory/does/not/exist"));
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

            new LowLevelConfigurationBase(new HashMap<>(), existingFile);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("the path " + existingFile.getPath() + " does not represent a directory", msg);
        }
    }

    @Test
    public void getFile_NoMatch() throws Exception {

        Map<String, Object> m = new HashMap<>();

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        File f = c.getFile("token1", "token2", "token3");

        assertNull(f);
    }

    @Test
    public void getFile_NoMatch2() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", "something else");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        File f = c.getFile("token1", "token2", "token3");

        assertNull(f);
    }

    @Test
    public void getFile_MatchButNoString() throws Exception {

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", new HashMap<>());

        //noinspection unchecked
        ((Map<String, Object>)((Map<String, Object>) m.get("token1")).get("token2")).put("token3", 10);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        try {
            c.getFile("token1", "token2", "token3");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected token1.token2.token3 to be a String but it is a(n) Integer: \"10\"", msg);
        }
    }

    @Test
    public void getFile_AbsolutePath() throws Exception {

        String filePath = "/I/am/sure/this/directory/does/not/exist";

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", new HashMap<>());

        //noinspection unchecked
        ((Map<String, Object>)((Map<String, Object>) m.get("token1")).get("token2")).put("token3", filePath);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        File f = c.getFile("token1", "token2", "token3");

        assertEquals(new File(filePath), f);
    }

    @Test
    public void getFile_RelativePath() throws Exception {

        // we need the configuration directory to exist
        File configurationFileDirectory = new File(System.getProperty("basedir"));
        assertTrue(configurationFileDirectory.isDirectory());

        String filePath = "I/am/sure/this/directory/does/not/exist";

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", new HashMap<>());

        //noinspection unchecked
        ((Map<String, Object>)((Map<String, Object>) m.get("token1")).get("token2")).put("token3", filePath);

        LowLevelConfiguration c = getConfigurationToTest(m, configurationFileDirectory);

        File f = c.getFile("token1", "token2", "token3");

        File file = new File(configurationFileDirectory, filePath);
        assertEquals(file, f);
    }

    @Test
    public void getFile_RelativePath2() throws Exception {

        // we need the configuration directory to exist
        File configurationFileDirectory = new File(System.getProperty("basedir"));
        assertTrue(configurationFileDirectory.isDirectory());

        String configDirectoryAsString = configurationFileDirectory.getPath();

        String filePath = "./something";

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", filePath);

        LowLevelConfiguration c = getConfigurationToTest(m, configurationFileDirectory);

        File f = c.getFile("token1", "token2");

        File file = new File(Files.normalizePath(new File(configurationFileDirectory, filePath).getPath()));
        assertEquals(file, f);

        assertEquals(file.getPath(), configDirectoryAsString + "/something");
    }

    @Test
    public void getFile_RelativePath3() throws Exception {

        // we need the configuration directory to exist
        File configurationFileDirectory = new File(System.getProperty("basedir"));
        assertTrue(configurationFileDirectory.isDirectory());

        String filePath = "../something";

        Map<String, Object> m = new HashMap<>();

        m.put("token1", new HashMap<>());
        //noinspection unchecked
        ((Map<String, Object>)m.get("token1")).put("token2", filePath);

        LowLevelConfiguration c = getConfigurationToTest(m, configurationFileDirectory);

        File f = c.getFile("token1", "token2");

        assertEquals(new File(configurationFileDirectory.getParentFile(), "something"), f);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LowLevelConfiguration getConfigurationToTest(
            Map<String, Object> raw, File configurationDirectory) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
