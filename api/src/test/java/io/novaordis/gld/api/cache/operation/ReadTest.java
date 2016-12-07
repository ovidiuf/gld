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
public class ReadTest extends CacheOperationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ReadTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle_hit() throws Exception {

        Read r = getOperationToTest("test-key");

        assertFalse(r.wasPerformed());
        assertFalse(r.wasSuccessful());
        assertNull(r.getValue());

        MockCacheService ms = new MockCacheService();
        ms.put("test-key", "test-value");

        r.perform(ms);

        assertTrue(r.wasPerformed());
        assertTrue(r.wasSuccessful());
        assertEquals("test-value", r.getValue());

        log.debug(".");
    }

    @Test
    public void lifecycle_miss() throws Exception {

        Read r = getOperationToTest("test-key");

        assertFalse(r.wasPerformed());
        assertFalse(r.wasSuccessful());
        assertNull(r.getValue());

        MockCacheService ms = new MockCacheService();

        r.perform(ms);

        assertTrue(r.wasPerformed());
        assertTrue(r.wasSuccessful());
        assertNull(r.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Read getOperationToTest(String key) throws Exception {

        return new Read(key);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
