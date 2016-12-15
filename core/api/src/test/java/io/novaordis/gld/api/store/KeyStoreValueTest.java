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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class KeyStoreValueTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(KeyStoreValueTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void instance_NullEverything() throws Exception {

        KeyValuePair p = new KeyValuePair();

        assertNull(p.getKey());
        assertEquals(Null.INSTANCE, p.getValue());
    }

    @Test
    public void instance_NullValue_Default() throws Exception {

        KeyValuePair p = new KeyValuePair("test");

        assertEquals("test", p.getKey());
        assertEquals(Null.INSTANCE, p.getValue());
    }

    @Test
    public void instance_NullValue() throws Exception {

        KeyValuePair p = new KeyValuePair("test", Null.INSTANCE);

        assertEquals("test", p.getKey());
        assertEquals(Null.INSTANCE, p.getValue());
    }

    // setKey() --------------------------------------------------------------------------------------------------------

    @Test
    public void setKey_Null() throws Exception {

        KeyValuePair p = new KeyValuePair();

        try {
            p.setKey(null);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null key", msg);
        }
    }

    @Test
    public void setKey() throws Exception {

        KeyValuePair p = new KeyValuePair();

        assertNull(p.getKey());

        p.setKey("test");

        assertEquals("test", p.getKey());
    }

    // setValue() ------------------------------------------------------------------------------------------------------

    @Test
    public void setValue_Null() throws Exception {

        KeyValuePair p = new KeyValuePair();

        try {

            p.setValue(null);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null StoredValue instance", msg);
        }
    }

    @Test
    public void setValue_NotStored() throws Exception {

        KeyValuePair p = new KeyValuePair();

        assertEquals(Null.INSTANCE, p.getValue());

        p.setValue(NotStored.INSTANCE);

        assertEquals(NotStored.INSTANCE, p.getValue());
    }

    @Test
    public void setValue() throws Exception {

        KeyValuePair p = new KeyValuePair();

        assertEquals(Null.INSTANCE, p.getValue());

        p.setValue(StoredValue.getInstance("test".getBytes()));

        StoredValue v = p.getValue();

        assertEquals("test", new String(v.getBytes()));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
