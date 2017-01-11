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

package io.novaordis.gld.wrapper;

import io.novaordis.utilities.Files;
import io.novaordis.utilities.os.NativeExecutionException;
import io.novaordis.utilities.os.NativeExecutionResult;
import io.novaordis.utilities.os.OS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class ShellWrapperTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ShellWrapperTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    protected static File scratchDirectory;

    private static File getShlibFile() throws Exception {

        String basedir = System.getProperty("basedir");
        File d = new File(basedir);
        assertTrue(d.isDirectory());

        File f = new File(d, "src/main/bash/gld.shlib");
        assertTrue(f.isFile());
        return f;
    }

    /**
     * The function dynamically builds a shell wrapper that executes the function, and executes it from
     * scratchDirectory.
     *
     * @throws NativeExecutionException if the function execution fails with a non-zero exit code. The exception's
     * message contains the stderr content.
     */
    private static String executeShellFunction(String functionName, String... args)  throws Exception {

        File shlib = getShlibFile();

        String wrapperContent =
                "#/bin/bash\n\n" +
                        ". " + shlib.getPath() + "\n\n" +
                        functionName + " ";

        for (String arg : args) {

            wrapperContent += arg + " ";
        }

        wrapperContent += "\n\n";

        File wrapperFile = new File(scratchDirectory, "test.sh");
        assertTrue(Files.write(wrapperFile, wrapperContent));
        assertTrue(Files.chmod(wrapperFile, "r-xr-xr-x"));

        NativeExecutionResult r = OS.getInstance().execute(scratchDirectory, "bash ./test.sh");

        if (r.isFailure()) {

            throw new NativeExecutionException(r.getStderr());

        }
        return r.getStdout();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //

        assertTrue(io.novaordis.utilities.Files.rmdir(scratchDirectory, false));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // get-extension-jars ----------------------------------------------------------------------------------------------

    @Test
    public void getExtensionJars_NoExtensionDirectory() throws Exception {

        try {
            executeShellFunction("get-extension-jars");
            fail("should have thrown exception");
        }
        catch(NativeExecutionException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("extension directory not provided"));
        }
    }

    @Test
    public void getExtensionJars_NotADirectory() throws Exception {

        assertTrue(Files.write(new File(scratchDirectory, "pseudo-extension-dir"), "..."));

        try {

            executeShellFunction("get-extension-jars", "pseudo-extension-dir");
            fail("should have thrown exception");
        }
        catch(NativeExecutionException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("invalid extension directory pseudo-extension-dir"));
        }
    }

    @Test
    public void getExtensionJars_ExtensionWithNoClientLibraries() throws Exception {

        File extensionDir = new File(scratchDirectory, "mock-extension");
        assertTrue(extensionDir.mkdir());

        File mockExtensionJar = new File(extensionDir, "mock-extension-logic.jar");
        assertTrue(Files.write(mockExtensionJar, "SYNTHETIC"));

        String s = executeShellFunction("get-extension-jars", "mock-extension");

        assertEquals("mock-extension/mock-extension-logic.jar\n", s);
    }

    @Test
    public void getExtensionJars_ExtensionWithOneClientLibrary() throws Exception {

        File extensionDir = new File(scratchDirectory, "mock-extension");
        assertTrue(extensionDir.mkdir());

        File mockExtensionJar = new File(extensionDir, "mock-extension-logic.jar");
        assertTrue(Files.write(mockExtensionJar, "SYNTHETIC"));

        File clientLibrariesRoot = new File(extensionDir, "1.0.0");
        assertTrue(clientLibrariesRoot.mkdir());

        File mockClientLibraryJar = new File(clientLibrariesRoot, "mock-client-library-1.jar");
        assertTrue(Files.write(mockClientLibraryJar, "SYNTHETIC"));

        String s = executeShellFunction("get-extension-jars", "mock-extension");

        assertEquals("mock-extension/1.0.0/mock-client-library-1.jar:mock-extension/mock-extension-logic.jar\n", s);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
