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
import static org.junit.Assert.assertNotNull;
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

    // get() Map -------------------------------------------------------------------------------------------------------

    @Test
    public void get_Map_NoMatch_FirstElement() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> m2 = c.get("no-such-top-element");
        assertNotNull(m2);
        assertTrue(m2.isEmpty());
    }

    @Test
    public void get_Map_NoMatch_PartialMatch() throws Exception {

        Map<String, Object> m = new HashMap<>();
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Object> m2 = new HashMap<>();

        m.put("token1", m2);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> m3 = c.get("token1", "token2");
        assertNotNull(m3);
        assertTrue(m3.isEmpty());
    }

    @Test
    public void get_Map_NoMatch_IntermediateElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", "a-string-not-a-map");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> m3 = c.get("token1", "token2", "token3");
        assertNotNull(m3);
        assertTrue(m3.isEmpty());
    }

    @Test
    public void get_Map_Match_NotTheExpectedType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", m3);
        m3.put("token3", 10);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        try {
            c.get("token1", "token2", "token3");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected token1.token2.token3 to be a Map but it is a(n) Integer: \"10\"", msg);
        }
    }

    @Test
    public void get_Map_NoSuchMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> nm = c.get("token1", "token2", "token3");

        assertNotNull(nm);
        assertTrue(nm.isEmpty());
    }

    @Test
    public void get_Map_NoSuchMap2() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", m2);
        m2.put("token3", null);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> nm = c.get("token1", "token2", "token3");

        assertNotNull(nm);
        assertTrue(nm.isEmpty());
    }

    @Test
    public void get_Map_Empty() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", m2);
        m2.put("token3", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> nm = c.get("token1", "token2", "token3");

        assertNotNull(nm);
        assertTrue(nm.isEmpty());
    }

    @Test
    public void get_Map_NotEmpty() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", m2);
        m2.put("token3", m3);
        m3.put("something", "something else");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> nm = c.get("token1", "token2", "token3");

        assertNotNull(nm);
        assertEquals(1, nm.size());
        assertEquals("something else", nm.get("something"));
    }

    @Test
    public void get_Map_Root() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put("token1", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        Map<String, Object> nm = c.get();

        assertNotNull(nm);
        assertEquals(1, nm.size());
        assertEquals("something", nm.get("token1"));

        //
        // actually, the documentation says that get() is guarantee to return the actual storage
        //

        assertTrue(nm == m);
    }

    // set() -----------------------------------------------------------------------------------------------------------

    @Test
    public void set_ReplaceTheWholeMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        Map<String, Object> m2 = new HashMap<>();
        base.set(m2);

        Map<String, Object> m3 = c.get();
        assertTrue(m2 == m3);
    }

    @Test
    public void set_ReplaceTheWholeMap_InvalidArgument() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {

            base.set("something");
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid attempt to replace the root map with a String", msg);
        }
    }

    @Test
    public void set_IntermediatePathElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {
            base.set(new HashMap<>(), "token1", "token2", "token3");
            fail("should have thrown an exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("token1.token2 does not match a map", msg);
        }
    }

    @Test
    public void set_ReplacementOfANonMapWithANonMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertEquals("something", c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct replacement of a non-map with a non-map
        //

        base.set(10, "token1", "token2", "token3");

        assertEquals(10, c.get(Integer.class, "token1", "token2", "token3").intValue());
    }

    @Test
    public void set_ReplacementOfAMapWithANonMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        Map<String, Object> nm = c.get("token1", "token2", "token3");
        assertTrue(nm.isEmpty());

        //
        // incorrect replacement of a map with a non-map
        //

        try {
            base.set("something", "token1", "token2", "token3");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("illegal replacement of the token1.token2.token3 map with a non-map", msg);
        }
    }

    @Test
    public void set_ReplacementOfAMapWithAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        Map<String, Object> m3 = new HashMap<>();
        m2.put("token3", m3);

        m3.put("a", "b");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertEquals("b", c.get("token1", "token2", "token3").get("a"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct replacement of a map with a map
        //

        Map<String, Object> nm = new HashMap<>();
        nm.put("c", "d");

        base.set(nm, "token1", "token2", "token3");

        Map<String, Object> nm2 = c.get("token1", "token2", "token3");
        assertEquals("d", nm2.get("c"));
    }

    @Test
    public void set_ReplacementOfANonExistentElementWithAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertNull(c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct replacement of a map with a map
        //

        Map<String, Object> nm = new HashMap<>();
        nm.put("c", "d");

        base.set(nm, "token1", "token2", "token3");

        Map<String, Object> nm2 = c.get("token1", "token2", "token3");
        assertEquals("d", nm2.get("c"));
    }

    @Test
    public void set_ReplacementOfANonExistentElementWithANonMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertNull(c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct replacement of a map with a map
        //

        base.set("something", "token1", "token2", "token3");

        String s = c.get(String.class, "token1", "token2", "token3");
        assertEquals("something", s);
    }


    @Test
    public void set_ReplacementOfNonMapWithAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        assertEquals("something", c.get(String.class, "token1", "token2", "token3"));

        //
        // incorrect replacement of a non-map with a map
        //

        try {
            base.set(new HashMap<>(), "token1", "token2", "token3");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("illegal replacement of the token1.token2.token3 non-map with a map", msg);
        }
    }

    // set(null, ...)/remove() -----------------------------------------------------------------------------------------

    @Test
    public void setNull_TheWholeMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {

            base.set(null);
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid attempt to remove the root storage", msg);
        }
    }

    @Test
    public void remove_TheWholeMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {

            base.remove();
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid attempt to remove the root storage", msg);
        }
    }

    @Test
    public void setNull_IntermediatePathElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {

            base.set(null, "token1", "token2", "token3");
            fail("should have thrown an exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("token1.token2 does not match a map", msg);
        }
    }

    @Test
    public void remove_IntermediatePathElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        m1.put("token2", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        try {

            base.remove("token1", "token2", "token3");
            fail("should have thrown an exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("token1.token2 does not match a map", msg);
        }
    }

    @Test
    public void setNull_RemoveANonMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertEquals("something", c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct removal of a non-map
        //

        base.set(null, "token1", "token2", "token3");

        assertTrue(m2.isEmpty());
    }

    @Test
    public void remove_NonMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", "something");

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertEquals("something", c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // correct removal of a non-map
        //

        base.remove("token1", "token2", "token3");

        assertTrue(m2.isEmpty());
    }

    @Test
    public void setNull_RemovalOfAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        Map<String, Object> nm = c.get("token1", "token2", "token3");
        assertTrue(nm.isEmpty());

        base.set(null, "token1", "token2", "token3");

        assertTrue(m2.isEmpty());
    }

    @Test
    public void remove_Map() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);
        m2.put("token3", new HashMap<>());

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        Map<String, Object> nm = c.get("token1", "token2", "token3");
        assertTrue(nm.isEmpty());

        base.remove("token1", "token2", "token3");

        assertTrue(m2.isEmpty());
    }

    @Test
    public void setNull_NonExistentElement() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertNull(c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // should be a noop
        //

        base.set(null, "token1", "token2", "token3");

        assertTrue(m2 == m1.get("token2"));
        assertTrue(m2.isEmpty());
    }

    @Test
    public void remove_NonExistentElement() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m1 = new HashMap<>();
        m.put("token1", m1);
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Object> m2 = new HashMap<>();
        m1.put("token2", m2);

        LowLevelConfiguration c = getConfigurationToTest(m, new File("."));

        //
        // we only test LowLevelConfigurationBases, we're a noop otherwise
        //

        if (!(c instanceof LowLevelConfigurationBase)) {

            return;
        }

        assertNull(c.get(String.class, "token1", "token2", "token3"));

        LowLevelConfigurationBase base = (LowLevelConfigurationBase)c;

        //
        // should be a noop
        //

        base.remove("token1", "token2", "token3");

        assertTrue(m2 == m1.get("token2"));
        assertTrue(m2.isEmpty());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LowLevelConfiguration getConfigurationToTest(
            Map<String, Object> raw, File configurationDirectory) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
