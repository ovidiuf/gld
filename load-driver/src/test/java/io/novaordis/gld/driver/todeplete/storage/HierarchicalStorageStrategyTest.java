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

package io.novaordis.gld.driver.todeplete.storage;

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HierarchicalStorageStrategyTest extends StorageStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HierarchicalStorageStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // init() -----------------------------------------------------------------------------------------------------

//    @Test
//    public void configure_NoRootDirectoryName() throws Exception
//    {
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> emptyArgumentList = new ArrayList<>();
//
//        try
//        {
////            Configuration c = new MockConfiguration();
//            Configuration c = null;
//
//            s.configure(c, emptyArgumentList, 0);
//            fail("should fail with UserErrorException, no root directory name");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void configure_rootIsTheLastArgument() throws Exception
//    {
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>(Arrays.asList("something", "something-else", "--root"));
//
//        try
//        {
//
//            //            Configuration c = new MockConfiguration();
//            Configuration c = null;
//
//            s.configure(c, arguments, 2);
//            fail("should fail with UserErrorException, --root last in list");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void configure() throws Exception
//    {
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>(Arrays.asList(
//            "something", "something-else", "--root", "./test-root"));
//
//        //            Configuration c = new MockConfiguration();
//        Configuration c = null;
//
//        s.configure(c, arguments, 2);
//
//        assertEquals("./test-root", s.getRootDirectoryName());
//    }
//
//    // start -----------------------------------------------------------------------------------------------------------
//
//    @Test
//    public void start_rootIsAFile() throws Exception
//    {
//        File root = new File(Tests.getScratchDir(), "test-root");
//
//        assertFalse(root.exists());
//
//        assertTrue(Files.write(root, "something"));
//
//        assertTrue(root.exists());
//        assertTrue(root.isFile());
//
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>();
//        arguments.add("--root");
//        arguments.add(root.getPath());
//
//        //            Configuration c = new MockConfiguration();
//        Configuration c = null;
//
//        s.configure(c, arguments, 0);
//
//        try
//        {
//            s.start();
//            fail("should fail with UserErrorException, root is a file");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void start_rootIsAnExistingDirectory_FailToStartUnlessForced() throws Exception
//    {
//        File root = new File(Tests.getScratchDir(), "test-root");
//
//        assertTrue(Files.mkdir(root));
//        assertTrue(root.exists());
//        assertTrue(root.isDirectory());
//
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>();
//        arguments.add("--root");
//        arguments.add(root.getPath());
//
//        //            Configuration c = new MockConfiguration();
//        Configuration c = null;
//
//        s.configure(c, arguments, 0);
//
//        try
//        {
//            s.start();
//            fail("should fail with UserErrorException, root is an existing directory and we're not forcing");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void start_createRootIfItDoesNotExist() throws Exception
//    {
//        File root = new File(Tests.getScratchDir(), "test-root");
//
//        assertFalse(root.exists());
//
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>();
//        arguments.add("--root");
//        arguments.add(root.getPath());
//
//        //            Configuration c = new MockConfiguration();
//        Configuration c = null;
//
//        s.configure(c, arguments, 0);
//
//        assertFalse(root.exists());
//
//        //
//        // simulate a load strategy that writes only
//        //
//        s.setWrite(true);
//        s.setRead(false);
//
//        s.start();
//
//        assertTrue(root.exists());
//        assertTrue(root.isDirectory());
//
//        s.stop();
//
//        // make sure the directory was not deleted
//
//        assertTrue(root.exists());
//        assertTrue(root.isDirectory());
//    }
//
//    // lifecycle -------------------------------------------------------------------------------------------------------
//
//    @Test
//    public void startStoreStop() throws Exception
//    {
//        File root = new File(Tests.getScratchDir(), "test-root");
//
//        assertFalse(root.exists());
//
//        HierarchicalStorageStrategy s = getStorageStrategyToTest();
//
//        List<String> arguments = new ArrayList<>();
//        arguments.add("--root");
//        arguments.add(root.getPath());
//
//        //            Configuration c = new MockConfiguration();
//        Configuration c = null;
//
//        s.configure(c, arguments, 0);
//
//        assertFalse(root.exists());
//
//        //
//        // simulate a load strategy that writes only
//        //
//        s.setWrite(true);
//        s.setRead(false);
//
//        s.start();
//
//        assertTrue(root.exists());
//        assertTrue(root.isDirectory());
//
//        s.store("KEY-01", "VALUE-01");
//        s.store("KEY-02", "VALUE-02");
//        s.store("KEY-03", "VALUE-03");
//
//        s.stop();
//
//        // make sure the directory was not deleted
//
//        assertTrue(root.exists());
//        assertTrue(root.isDirectory());
//
//        // make sure all the content is there
//
//        String key = "KEY-01";
//        String value = "VALUE-01";
//        String fileName = HierarchicalStorageStrategy.toHex(HierarchicalStorageStrategy.toSha1(key)) + ".txt";
//        String firstLevelDir = fileName.substring(0, 2);
//        String secondLevelDir = fileName.substring(2, 4);
//        String fileContent = Files.read(new File(root, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
//        StringTokenizer st = new StringTokenizer(fileContent, "\n");
//        assertEquals(key, st.nextToken());
//        assertEquals(value, st.nextToken());
//
//        key = "KEY-02";
//        value = "VALUE-02";
//        fileName = HierarchicalStorageStrategy.toHex(HierarchicalStorageStrategy.toSha1(key)) + ".txt";
//        firstLevelDir = fileName.substring(0, 2);
//        secondLevelDir = fileName.substring(2, 4);
//        fileContent = Files.read(new File(root, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
//        st = new StringTokenizer(fileContent, "\n");
//        assertEquals(key, st.nextToken());
//        assertEquals(value, st.nextToken());
//
//        key = "KEY-03";
//        value = "VALUE-03";
//        fileName = HierarchicalStorageStrategy.toHex(HierarchicalStorageStrategy.toSha1(key)) + ".txt";
//        firstLevelDir = fileName.substring(0, 2);
//        secondLevelDir = fileName.substring(2, 4);
//        fileContent = Files.read(new File(root, firstLevelDir + "/" + secondLevelDir + "/" + fileName));
//        st = new StringTokenizer(fileContent, "\n");
//        assertEquals(key, st.nextToken());
//        assertEquals(value, st.nextToken());
//    }
//
//    // writeKeyValue ---------------------------------------------------------------------------------------------------
//
//    @Test
//    public void writeKeyValue_NullKey() throws Exception
//    {
//        File target = new File(Tests.getScratchDir(), "test-dir");
//        assertTrue(Files.mkdir(target));
//
//        try
//        {
//            HierarchicalStorageStrategy.writeKeyValue(target, null, "");
//            fail("should have failed with IllegalArgumentException, null key");
//        }
//        catch(IllegalArgumentException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeKeyValue_NewLineInTheMiddle() throws Exception
//    {
//        String key = "blah\nblah";
//
//        File target = new File(Tests.getScratchDir(), "test-dir");
//        assertTrue(Files.mkdir(target));
//
//        try
//        {
//            HierarchicalStorageStrategy.writeKeyValue(target, key, "");
//            fail("should have failed with IllegalArgumentException, new line in key");
//        }
//        catch(IllegalArgumentException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeKeyValue_NewLineAtTheEnd() throws Exception
//    {
//        String key = "blah\n";
//
//        File target = new File(Tests.getScratchDir(), "test-dir");
//        assertTrue(Files.mkdir(target));
//
//        try
//        {
//            HierarchicalStorageStrategy.writeKeyValue(target, key, "");
//            fail("should have failed with IllegalArgumentException, new line in key");
//        }
//        catch(IllegalArgumentException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeKeyValue_ParentNotADirectory() throws Exception
//    {
//        String key = "blah";
//
//        File notADir = new File(Tests.getScratchDir(), "not-a-dir");
//        assertTrue(Files.write(notADir, "something"));
//        File keyFile = new File(notADir, "a.txt");
//
//        try
//        {
//            HierarchicalStorageStrategy.writeKeyValue(keyFile, key, "");
//            fail("should have failed with IllegalArgumentException, parent not a directory");
//        }
//        catch(IllegalArgumentException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeKeyValue_KeyValueFileAlreadyExist() throws Exception
//    {
//        String key = "blah";
//        String value = "halbhalb";
//
//        File targetDir = new File(Tests.getScratchDir(), "test-dir");
//        assertTrue(Files.mkdir(targetDir));
//
//        String hexSha1 = HierarchicalStorageStrategy.toHex(HierarchicalStorageStrategy.toSha1(key));
//
//        File file = new File(targetDir, hexSha1 + ".txt");
//        assertTrue(Files.write(file, "something"));
//        assertEquals("something", Files.read(file));
//
//        HierarchicalStorageStrategy.writeKeyValue(file, key, value);
//
//        // make sure the file was overwritten
//
//        String content = Files.read(file);
//
//        StringTokenizer st = new StringTokenizer(content, "\n");
//
//        String keyFromFile = st.nextToken();
//
//        assertEquals(key, keyFromFile);
//
//        String valueFromFile = st.nextToken();
//
//        assertEquals(value, valueFromFile);
//    }
//
//    @Test
//    public void writeKeyValue_FileDoesNotExist() throws Exception
//    {
//        String key = "blah";
//        String value = "halbhalb";
//
//        File targetDir = new File(Tests.getScratchDir(), "test-dir");
//        assertTrue(Files.mkdir(targetDir));
//
//        String hexSha1 = HierarchicalStorageStrategy.toHex(HierarchicalStorageStrategy.toSha1(key));
//
//        File candidate = new File(targetDir, hexSha1 + ".txt");
//
//        File file = HierarchicalStorageStrategy.writeKeyValue(candidate, key, value);
//
//        assertEquals(candidate, file);
//
//        String content = Files.read(file);
//
//        StringTokenizer st = new StringTokenizer(content, "\n");
//
//        String keyFromFile = st.nextToken();
//
//        assertEquals(key, keyFromFile);
//
//        String valueFromFile = st.nextToken();
//
//        assertEquals(value, valueFromFile);
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HierarchicalStorageStrategy getStorageStrategyToTest() throws Exception
    {
        return new HierarchicalStorageStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
