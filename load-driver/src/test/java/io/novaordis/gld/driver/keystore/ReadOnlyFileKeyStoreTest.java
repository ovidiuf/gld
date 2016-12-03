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

package io.novaordis.gld.driver.keystore;

import io.novaordis.utilities.Files;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;


public class ReadOnlyFileKeyStoreTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReadOnlyFileKeyStoreTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    @Test
    public void writeShouldFail() throws Exception
    {
        File keyFile = new File(Tests.getScratchDir(), "test-keys.txt");

        ReadOnlyFileKeyStore rofs = new ReadOnlyFileKeyStore(keyFile.getPath());

        Files.write(keyFile, "something");

        rofs.start();

        try
        {
            rofs.store("something");
            fail("should fail because we cannot store into a write-only keystore");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            rofs.stop();
        }
    }

    @Test
    public void get() throws Exception
    {
        File keyFile = new File(Tests.getScratchDir(), "test-keys.txt");

        Files.write(keyFile,
            "key-01\n" +
            "key-02\n" +
                "key-03"
        );

        ReadOnlyFileKeyStore rofs = new ReadOnlyFileKeyStore(keyFile.getPath());

        rofs.start();

        assertEquals("key-01", rofs.get());
        assertEquals("key-02", rofs.get());
        assertEquals("key-03", rofs.get());
        assertEquals("key-01", rofs.get());
        assertEquals("key-02", rofs.get());
        assertEquals("key-03", rofs.get());
    }

    @Test
    public void isReadOnly() throws Exception
    {
        File keyFile = new File(Tests.getScratchDir(), "test-keys.txt");
        ReadOnlyFileKeyStore rofs = new ReadOnlyFileKeyStore(keyFile.getPath());
        assertTrue(rofs.isReadOnly());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
