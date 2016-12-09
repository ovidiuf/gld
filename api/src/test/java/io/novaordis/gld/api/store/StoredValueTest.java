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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class StoredValueTest {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final Logger log = LoggerFactory.getLogger(StoredValueTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // null value ------------------------------------------------------------------------------------------------------

    @Test
    public void nullValue() {

        //noinspection NullArgumentToVariableArgMethod
        StoredValue v = StoredValue.getInstance(null);

        assertTrue(v.isNull());
        assertFalse(v.notStored());
        assertNull(v.getBytes());

        assertEquals(Null.INSTANCE, v);
    }

    @Test
    public void regularValue_Empty() {

        String s = "";
        StoredValue v = StoredValue.getInstance(s.getBytes());

        assertFalse(v.isNull());
        assertFalse(v.notStored());

        byte[] b = v.getBytes();

        assertEquals("", new String(b));
    }

    @Test
    public void regularValue() {

        String s = "something";
        StoredValue v = StoredValue.getInstance(s.getBytes());

        assertFalse(v.isNull());
        assertFalse(v.notStored());

        byte[] b = v.getBytes();

        assertEquals("something", new String(b));
    }

    @Test
    public void moreThanOneArgument() {

        String s = "something";

        try {

            StoredValue.getInstance(s.getBytes(), s.getBytes());
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid multiple arguments", msg);
        }
    }

    @Test
    public void notStored() {

        StoredValue v = StoredValue.getInstance();

        assertFalse(v.isNull());
        assertTrue(v.notStored());
        assertNull(v.getBytes());

        assertEquals(NotStored.INSTANCE, v);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
