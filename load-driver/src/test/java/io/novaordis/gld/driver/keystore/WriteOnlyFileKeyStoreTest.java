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

import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class WriteOnlyFileKeyStoreTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(WriteOnlyFileKeyStoreTest.class);

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
    public void getShouldFail() throws Exception
    {
        File keyFile = new File(Tests.getScratchDir(), "test-keys.txt");

        WriteOnlyFileKeyStore wofs = new WriteOnlyFileKeyStore(keyFile.getPath());

        wofs.start();

        try
        {
            wofs.get();
            fail("should fail because we cannot next from a write-only keystore");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            wofs.stop();
        }
    }

    @Test
    public void isReadOnly() throws Exception
    {
        File keyFile = new File(Tests.getScratchDir(), "test-keys.txt");
        WriteOnlyFileKeyStore wofs = new WriteOnlyFileKeyStore(keyFile.getPath());
        assertFalse(wofs.isReadOnly());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
