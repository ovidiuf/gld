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

package io.novaordis.gld.api.store;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.KeyStoreTest;
import io.novaordis.gld.api.configuration.MockStoreConfiguration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class HierarchicalStoreTest extends KeyStoreTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HierarchicalStoreTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // KeyStoreFactory.build() -----------------------------------------------------------------------------------------

    @Test
    public void KeyStoreFactory_build_NoDirectoryInConfiguration() throws Exception {

        //
        // we test it here, and not in KeyStoreFactoryTest, because the logic is HierarchicalStore-specific
        //

        MockStoreConfiguration mc = new MockStoreConfiguration();
        mc.setStoreType(HierarchicalStore.STORY_TYPE_LABEL);
        assertNull(mc.get(String.class, HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL));

        try {

            KeyStoreFactory.build(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing \"" + HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL +
                    "\" hierarchical key store configuration element", msg);
        }
    }

    @Test
    public void KeyStoreFactory_build() throws Exception {

        //
        // we test it here, and not in KeyStoreFactoryTest, because the logic is HierarchicalStore-specific
        //

        MockStoreConfiguration mc = new MockStoreConfiguration();
        mc.setStoreType(HierarchicalStore.STORY_TYPE_LABEL);
        mc.setPath(HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL, "a/directory/that/does/not/exist");

        //
        // it is OK if the directory does not exist, the instance will deal with that at start() time
        //
        HierarchicalStore s = (HierarchicalStore)KeyStoreFactory.build(mc);

        File d = s.getDirectory();
        assertEquals(new File("a/directory/that/does/not/exist"), d);
    }

    // start() ---------------------------------------------------------------------------------------------------------

    @Test
    public void start_File() throws Exception {

        File f  = new File(scratchDirectory, "test");
        assertTrue(Files.write(f, "..."));

        HierarchicalStore s = new HierarchicalStore(f);

        try {

            s.start();
            fail("should have thrown exception");
        }
        catch(KeyStoreException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals(f + " is a file, it should have been a directory", msg);
        }
    }

    @Test
    public void start_ParentDoesNotExist() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertFalse(d.getParentFile().isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);

        try {

            s.start();
            fail("should have thrown exception");
        }
        catch(KeyStoreException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("parent directory " + d.getParentFile() + " does not exist", msg);
        }
    }

    @Test
    public void start_ParentDoesExist_DirectoryDoesNotExist_FailsToCreateIt() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.getParentFile().mkdir());
        assertFalse(d.isDirectory());

        // make the directory read-only
        Files.chmod(d.getParentFile(), "r-xr-xr-x");

        HierarchicalStore s = new HierarchicalStore(d);

        try {

            s.start();
            fail("should have thrown exception");
        }
        catch(KeyStoreException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("failed to create directory " + d, msg);
        }
    }

    @Test
    public void start_ParentDoesExist_DirectoryDoesNotExist() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.getParentFile().mkdir());
        assertFalse(d.isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);

        s.start();

        assertTrue(d.isDirectory());
    }

    @Test
    public void start_DirectoryExists_OverwriteForced() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.mkdirs());
        assertTrue(d.isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);
        s.setOverwrite(true);

        s.start();

        assertTrue(d.isDirectory());
    }

    @Test
    public void start_DirectoryExists_OverwriteNotForced() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.mkdirs());
        assertTrue(d.isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);

        assertFalse(s.isOverwrite());

        try {

            s.start();
            fail("should throw exception");
        }
        catch(KeyStoreException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("directory .* already exists and the store is not configured to overwrite it"));
        }
    }

    @Test
    public void start_createDirectory_Stop() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.getParentFile().mkdir());
        assertFalse(d.isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);

        assertFalse(d.isDirectory());

        s.start();

        assertTrue(d.isDirectory());

        s.stop();

        assertTrue(d.exists());
        assertTrue(d.isDirectory());
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void startStoreStop() throws Exception {

        File d  = new File(scratchDirectory, "parent/test-hierarchical-store");
        assertTrue(d.getParentFile().mkdir());
        assertFalse(d.isDirectory());

        HierarchicalStore s = new HierarchicalStore(d);

        s.start();

        s.store("KEY-01", "VALUE-01".getBytes());
        s.store("KEY-02", "VALUE-02".getBytes());
        s.store("KEY-03", "VALUE-03".getBytes());

        s.stop();

        // make sure the directory was not deleted

        assertTrue(d.exists());
        assertTrue(d.isDirectory());

        // make sure all the content is there

        String key = "KEY-01";
        String value = "VALUE-01";
        String fileName = HierarchicalStore.toHex(HierarchicalStore.toSha1(key)) + ".txt";
        String firstLevelDir = fileName.substring(0, 2);
        String secondLevelDir = fileName.substring(2, 4);
        String fileContent = Files.read(new File(d, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
        StringTokenizer st = new StringTokenizer(fileContent, "\n");
        assertEquals(key, st.nextToken());
        assertEquals(value, st.nextToken());

        key = "KEY-02";
        value = "VALUE-02";
        fileName = HierarchicalStore.toHex(HierarchicalStore.toSha1(key)) + ".txt";
        firstLevelDir = fileName.substring(0, 2);
        secondLevelDir = fileName.substring(2, 4);
        fileContent = Files.read(new File(d, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
        st = new StringTokenizer(fileContent, "\n");
        assertEquals(key, st.nextToken());
        assertEquals(value, st.nextToken());

        key = "KEY-03";
        value = "VALUE-03";
        fileName = HierarchicalStore.toHex(HierarchicalStore.toSha1(key)) + ".txt";
        firstLevelDir = fileName.substring(0, 2);
        secondLevelDir = fileName.substring(2, 4);
        fileContent = Files.read(new File(d, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
        st = new StringTokenizer(fileContent, "\n");
        assertEquals(key, st.nextToken());
        assertEquals(value, st.nextToken());
    }

    // writeKeyValue ---------------------------------------------------------------------------------------------------

    @Test
    public void writeKeyValue_NullKey() throws Exception {

        File target  = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(target));

        try {

            HierarchicalStore.writeKeyValue(target, null, StoredValue.getInstance("test".getBytes()));
            fail("should have failed with IllegalArgumentException, null key");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void writeKeyValue_NullValue() throws Exception {

        File target  = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(target));

        try {

            HierarchicalStore.writeKeyValue(target, "test-key", null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null value", msg);
        }
    }

    @Test
    public void writeKeyValue_NewLineInTheMiddle() throws Exception {

        String key = "blah\nblah";

        File target  = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(target));

        try {

            HierarchicalStore.writeKeyValue(target, key, StoredValue.getInstance("test".getBytes()));
            fail("should have failed with IllegalArgumentException, new line in key");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void writeKeyValue_NewLineAtTheEnd() throws Exception {

        String key = "blah\n";

        File target  = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(target));

        try {

            HierarchicalStore.writeKeyValue(target, key, StoredValue.getInstance("test".getBytes()));
            fail("should have failed with IllegalArgumentException, new line in key");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void writeKeyValue_ParentNotADirectory() throws Exception {

        String key = "blah";

        File notADir = new File(scratchDirectory, "not-a-dir");
        assertTrue(Files.write(notADir, "something"));
        File keyFile = new File(notADir, "a.txt");

        try {

            HierarchicalStore.writeKeyValue(keyFile, key, StoredValue.getInstance("test".getBytes()));
            fail("should have failed with IllegalArgumentException, parent not a directory");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void writeKeyValue_KeyValueFileAlreadyExist() throws Exception {

        String key = "blah";
        String value = "halbhalb";

        File targetDir = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(targetDir));

        String hexSha1 = HierarchicalStore.toHex(HierarchicalStore.toSha1(key));

        File file = new File(targetDir, hexSha1 + ".txt");
        assertTrue(Files.write(file, "something"));
        assertEquals("something", Files.read(file));

        HierarchicalStore.writeKeyValue(file, key, StoredValue.getInstance(value.getBytes()));

        // make sure the file was overwritten

        String content = Files.read(file);

        StringTokenizer st = new StringTokenizer(content, "\n");

        String keyFromFile = st.nextToken();

        assertEquals(key, keyFromFile);

        String valueFromFile = st.nextToken();

        assertEquals(value, valueFromFile);
    }

    @Test
    public void writeKeyValue_FileDoesNotExist() throws Exception {

        String key = "blah";
        String value = "halbhalb";

        File targetDir = new File(scratchDirectory, "test-dir");
        assertTrue(Files.mkdir(targetDir));

        String hexSha1 = HierarchicalStore.toHex(HierarchicalStore.toSha1(key));

        File candidate = new File(targetDir, hexSha1 + ".txt");

        File file = HierarchicalStore.writeKeyValue(candidate, key, StoredValue.getInstance(value.getBytes()));

        assertEquals(candidate, file);

        String content = Files.read(file);

        StringTokenizer st = new StringTokenizer(content, "\n");

        String keyFromFile = st.nextToken();

        assertEquals(key, keyFromFile);

        String valueFromFile = st.nextToken();

        assertEquals(value, valueFromFile);
    }

    // toFileContent()/fromFileContent() -------------------------------------------------------------------------------

    @Test
    public void toFileContent_fromFileContent_NullValue() throws Exception {

        byte[] content = HierarchicalStore.toFileContent("test", Null.INSTANCE);

        KeyValuePair p = HierarchicalStore.fromFileContent(content);
        assertEquals("test", p.getKey());
        assertEquals(Null.INSTANCE, p.getValue());
    }

    @Test
    public void toFileContent_fromFileContent_NotStored() throws Exception {

        byte[] content = HierarchicalStore.toFileContent("test", NotStored.INSTANCE);

        log.info(new String(content));

        KeyValuePair p = HierarchicalStore.fromFileContent(content);
        assertEquals("test", p.getKey());
        assertEquals(NotStored.INSTANCE, p.getValue());
    }

    @Test
    public void toFileContent_fromFileContent_RegularValue() throws Exception {

        StoredValue v = StoredValue.getInstance("something".getBytes());
        byte[] content = HierarchicalStore.toFileContent("test", v);

        KeyValuePair p = HierarchicalStore.fromFileContent(content);
        assertEquals("test", p.getKey());

        StoredValue v2 = p.getValue();
        assertEquals("something", new String(v2.getBytes()));
    }

    @Test
    public void toFileContent_fromFileContent_RegularValue_SameLengthAsNotStoredMarker() throws Exception {

        byte[] content = new byte[HierarchicalStore.NOT_STORED_MARKER.length];
        for(int i = 0; i < content.length; i ++) {
            content[i] = (byte)'a';
        }

        StoredValue v = StoredValue.getInstance(content);

        byte[] content2 = HierarchicalStore.toFileContent("test", v);

        KeyValuePair p = HierarchicalStore.fromFileContent(content2);
        assertEquals("test", p.getKey());

        StoredValue v2 = p.getValue();
        assertEquals(new String(content), new String(v2.getBytes()));
    }

    @Test
    public void toFileContent_fromFileContent_EmptyValue() throws Exception {

        StoredValue v = StoredValue.getInstance("".getBytes());
        byte[] content = HierarchicalStore.toFileContent("test", v);

        KeyValuePair p = HierarchicalStore.fromFileContent(content);
        assertEquals("test", p.getKey());

        StoredValue v2 = p.getValue();
        assertEquals("", new String(v2.getBytes()));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected KeyStore getKeyStoreToTest() throws Exception {

        File dir = new File(scratchDirectory, "test-hierarchical-store");

        //
        // the implementation will create the directory
        //
        assertFalse(dir.isDirectory());

        return new HierarchicalStore(dir);
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
