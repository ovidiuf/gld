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

package com.novaordis.gld.operations.cache;

import com.novaordis.gld.mock.MockCacheService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReadTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ReadTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void perform_noKey() throws Exception
    {
        Read read = new Read("a");

        MockCacheService mcs = new MockCacheService();

        read.perform(mcs);

        String key = read.getKey();
        String value = read.getValue();

        assertEquals("a", key);
        assertNull(value);
        assertTrue(read.hasBeenPerformed());

        log.debug(".");
    }

    @Test
    public void perform_someKey() throws Exception
    {
        Read read = new Read("a");

        MockCacheService mcs = new MockCacheService();

        mcs.set("a", "b");

        read.perform(mcs);

        read.perform(mcs);

        String key = read.getKey();
        String value = read.getValue();

        assertEquals("a", key);
        assertEquals("b", value);
        assertTrue(read.hasBeenPerformed());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
