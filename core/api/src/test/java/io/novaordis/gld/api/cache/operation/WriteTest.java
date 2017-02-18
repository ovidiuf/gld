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

package io.novaordis.gld.api.cache.operation;

import io.novaordis.gld.api.cache.MockCacheService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class WriteTest extends CacheOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(WriteTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        Write w = getOperationToTest("test-key");

        assertFalse(w.wasPerformed());
        assertFalse(w.wasSuccessful());
        assertEquals("test-value", w.getValue());

        MockCacheService ms = new MockCacheService();
        assertNull(ms.get("test-key"));

        w.perform(ms);

        assertTrue(w.wasPerformed());
        assertTrue(w.wasSuccessful());

        String value = ms.get("test-key");

        log.info(value);
        assertEquals("test-value", value);
    }

    @Test
    public void toStringTest() throws Exception {

        Write w = new Write("some_key_longer_than_12_characters", "some_value_longer_than_12_characters");
        String s = w.toString();
        log.info(s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Write getOperationToTest(String key) throws Exception {

        return new Write(key, "test-value");
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
