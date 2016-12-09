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

package io.novaordis.gld.api;

import io.novaordis.gld.api.store.StoredValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/1/16
 */
public abstract class KeyStoreTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(KeyStoreTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    protected File scratchDirectory;

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

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        KeyStore s = getKeyStoreToTest();

        assertFalse(s.isStarted());

        s.start();

        assertTrue(s.isStarted());

        // idempotence

        s.start();

        assertTrue(s.isStarted());

        s.stop();

        assertFalse(s.isStarted());

        // idempotence

        s.stop();

        assertFalse(s.isStarted());
    }

    // getKeyCount() ---------------------------------------------------------------------------------------------------

    @Test
    public void getKeyCount() throws Exception {

        KeyStore s = getKeyStoreToTest();

        s.start();

        s.store("key1");

        assertEquals(1, s.getKeyCount());

        s.store("key2");

        assertEquals(2, s.getKeyCount());
    }

    // store() and retrieve() ------------------------------------------------------------------------------------------

    @Test
    public void store_MoreThanOneValue() throws Exception {

        KeyStore s = getKeyStoreToTest();

        s.start();

        assertTrue(s.isStarted());

        try {

            s.store("test", new byte[1], new byte[1]);
            fail("should throw exception");
        }
        catch (IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid multiple arguments", msg);
        }
        finally {

            s.stop();
        }
    }

    @Test
    public void storeAndRetrieve_NonNullValue() throws Exception {

        KeyStore s = getKeyStoreToTest();

        s.start();

        s.store("test-key", "test-value".getBytes("utf8"));

        Set<String> keys = s.getKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key"));

        assertEquals(1, s.getKeyCount());

        StoredValue value = s.retrieve("test-key");

        assertFalse(value.isNull());
        assertFalse(value.notStored());

        byte[] v = value.getBytes();
        assertEquals("test-value", new String(v));

        s.stop();
    }

    @Test
    public void storeAndRetrieve_NullValue() throws Exception {

        KeyStore s = getKeyStoreToTest();

        s.start();

        //noinspection NullArgumentToVariableArgMethod
        s.store("test-key", null);

        Set<String> keys = s.getKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key"));

        assertEquals(1, s.getKeyCount());

        StoredValue value = s.retrieve("test-key");

        assertTrue(value.isNull());
        assertFalse(value.notStored());
        assertNull(value.getBytes());

        s.stop();
    }

    @Test
    public void storeAndRetrieve_ChooseNotToStoreValue() throws Exception {

        KeyStore s = getKeyStoreToTest();

        s.start();

        s.store("test-key");

        Set<String> keys = s.getKeys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key"));

        assertEquals(1, s.getKeyCount());

        StoredValue value = s.retrieve("test-key");

        assertFalse(value.isNull());
        assertTrue(value.notStored());
        assertNull(value.getBytes());

        s.stop();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract KeyStore getKeyStoreToTest() throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
