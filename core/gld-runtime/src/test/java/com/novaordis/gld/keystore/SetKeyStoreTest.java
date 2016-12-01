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

package com.novaordis.gld.keystore;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SetKeyStoreTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SetKeyStoreTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void lifeCycle() throws Exception
    {
        Set<String> keys = new HashSet<>();
        keys.add("KEY1");
        keys.add("KEY2");
        keys.add("KEY3");

        Set<String> expected = new HashSet<>(keys);

        SetKeyStore sks = new SetKeyStore(keys);

        assertTrue(sks.isReadOnly());
        assertTrue(sks.isStarted());

        assertEquals(3, sks.size());

        try
        {
            sks.store("blah");
            fail("should throw IllegalStateException, we're a read only key store");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        String key = sks.get();

        assertTrue(expected.contains(key));
        expected.remove(key);
        assertEquals(2, sks.size());

        key = sks.get();

        assertTrue(expected.contains(key));
        expected.remove(key);
        assertEquals(1, sks.size());

        key = sks.get();

        assertTrue(expected.contains(key));
        expected.remove(key);
        assertEquals(0, sks.size());

        assertNull(sks.get());

        assertEquals(0, sks.size());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
