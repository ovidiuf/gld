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

package io.novaordis.gld;

import io.novaordis.gld.api.Configuration;
import io.novaordis.gld.api.configuration.YamlBasedConfiguration;
import io.novaordis.gld.api.store.HierarchicalStore;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.env.EnvironmentVariableProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MainTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private Logger log = LoggerFactory.getLogger(MainTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private File scratchDirectory;
    private File baseDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());

        baseDirectory = new File(System.getProperty("basedir"));
        assertTrue(baseDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //
        assertTrue(io.novaordis.utilities.Files.rmdir(scratchDirectory, false));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // extractConfigurationFile() --------------------------------------------------------------------------------------

    @Test
    public void extractConfigurationFile() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f, "# something"));

        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "something-else", "-c", f.getPath(), "does-not-matter"));

        File f2 = Main.extractConfigurationFile(args);

        assertTrue(f2.isFile());
        assertTrue(f2.canRead());

        assertEquals(f, f2);

        assertEquals(3, args.size());
        assertEquals("something", args.get(0));
        assertEquals("something-else", args.get(1));
        assertEquals("does-not-matter", args.get(2));
    }

    @Test
    public void extractConfigurationFile_MissingFileName() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("something", "something-else", "-c"));

        try {
            Main.extractConfigurationFile(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("a configuration file name must follow -c", msg);
        }

    }

    @Test
    public void extractConfigurationFile_FileDoesNotExist() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "something-else", "-c", "/I/am/sure/there/is/no/such/file.yml", "does-not-matter"));

        try {
            Main.extractConfigurationFile(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("the configuration file /I/am/sure/there/is/no/such/file.yml does not exist", msg);
        }
    }

    @Test
    public void extractConfigurationFile_FileCannotBeRead() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f, "# something"));
        assertTrue(Files.chmod(f, "-w--w--w-"));
        assertFalse(f.canRead());

        List<String> args = new ArrayList<>(Arrays.asList(
                "something", "something-else", "-c", f.getPath(), "does-not-matter"));

        try {
            Main.extractConfigurationFile(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("the configuration file .* cannot be read"));
        }
    }

    @Test
    public void extractConfigurationFile_NoFileSpecified_NoEnvironmentVariable() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("something", "something-else", "does-not-matter"));

        try {

            Main.extractConfigurationFile(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals(
                    "no configuration file specified on command line with -c and no " +
                            Main.CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME + " environment variable defined", msg);
        }
    }

    @Test
    public void extractConfigurationFile_NoFileSpecified_EnvironmentVariable_NoSuchFile() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("something", "something-else", "does-not-matter"));

        try {

            System.setProperty(
                    EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY,
                    MockEnvironmentVariableProvider.class.getName());

            EnvironmentVariableProvider.reset();

            MockEnvironmentVariableProvider p =
                    (MockEnvironmentVariableProvider)EnvironmentVariableProvider.getInstance();

            p.setenv(Main.CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME, "/I/am/sure/there/is/no/such/file.yml");

            Main.extractConfigurationFile(args);

            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("the configuration file /I/am/sure/there/is/no/such/file.yml does not exist", msg);
        }
        finally {

            System.clearProperty(EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY);
            EnvironmentVariableProvider.reset();
        }
    }

    @Test
    public void extractConfigurationFile_ValidEnvironmentVariable() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f, "# something"));

        List<String> args = new ArrayList<>(Arrays.asList("something", "something-else", "does-not-matter"));

        try {

            System.setProperty(
                    EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY,
                    MockEnvironmentVariableProvider.class.getName());

            EnvironmentVariableProvider.reset();

            MockEnvironmentVariableProvider p =
                    (MockEnvironmentVariableProvider)EnvironmentVariableProvider.getInstance();

            p.setenv(Main.CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME, f.getPath());

            File f2 = Main.extractConfigurationFile(args);

            assertTrue(f2.isFile());
            assertTrue(f2.canRead());

            assertEquals(3, args.size());
            assertEquals("something", args.get(0));
            assertEquals("something-else", args.get(1));
            assertEquals("does-not-matter", args.get(2));
        }
        finally {

            System.clearProperty(EnvironmentVariableProvider.ENVIRONMENT_VARIABLE_PROVIDER_CLASS_NAME_SYSTEM_PROPERTY);
            EnvironmentVariableProvider.reset();
        }
    }

    // extractBackgroundSetting() --------------------------------------------------------------------------------------

    @Test
    public void extractBackgroundSetting_Default() throws Exception {

        List<String> args = new ArrayList<>();
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
    }

    @Test
    public void extractBackgroundSetting_Default2() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("something", "something-else"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
        assertEquals(2, args.size());
    }

    @Test
    public void extractBackgroundSetting_ExplicitBackground() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--background"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_ExplicitBackground2() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--background=true"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_ExplicitBackground3() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--foreground=false"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_ExplicitForeground() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--foreground"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertFalse(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_ExplicitForeground2() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--foreground=true"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertFalse(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_ExplicitForeground3() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--foreground=false"));
        boolean b =  Main.extractBackgroundSetting(args);
        assertTrue(b);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractBackgroundSetting_Conflict() throws Exception {

        List<String> args = new ArrayList<>(Arrays.asList("--foreground", "--background"));

        try {

            Main.extractBackgroundSetting(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("conflicting background configuration settings", msg);
        }
    }

    @Test
    public void extractBackgroundSetting_Invalid() throws Exception {

        List<String> args = new ArrayList<>(Collections.singletonList("--background=blah"));

        try {

            Main.extractBackgroundSetting(args);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid --background value: \"blah\"", msg);
        }
    }

    // loadDriverLifeCycle -------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        File f = new File(baseDirectory, "../api/src/test/resources/data/reference-configuration.yml");
        assertTrue(f.isFile());

        List<String> args = new ArrayList<>(Arrays.asList("-c", f.getPath()));

        int exitCode = Main.loadDriverLifeCycle(args);

        assertEquals(0, exitCode);

        //
        // make sure the all the keys are on disk
        //

        Configuration c = new YamlBasedConfiguration(f);
        String storageDirAsString = c.getStoreConfiguration().get(String.class, "directory");
        File storageDir = new File(storageDirAsString);
        assertTrue(storageDir.isDirectory());

        HierarchicalStore hs = new HierarchicalStore(storageDir);

        long expected = c.getLoadConfiguration().getOperations();
        long actual = hs.getKeyCount();
        assertEquals(expected, actual);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
