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

import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RandomKeyGeneratorTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(RandomKeyGeneratorTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void store() throws Exception
    {
        RandomKeyGenerator rkg = new RandomKeyGenerator(10);

        try
        {
            rkg.store("doesnotmatter");
            fail("should fail with IllegalStateException");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void get() throws Exception
    {
        int keySize = 10;
        RandomKeyGenerator rkg = new RandomKeyGenerator(keySize);

        String s = rkg.get();
        log.info(s);
        assertEquals(keySize, s.length());
    }

    @Test
    public void get_maxKeys() throws Exception
    {
        int keySize = 10;
        long maxKeys = 3;
        RandomKeyGenerator rkg = new RandomKeyGenerator(keySize, maxKeys);

        String s = rkg.get();
        assertNotNull(s);
        assertEquals(keySize, s.length());

        s = rkg.get();
        assertNotNull(s);

        s = rkg.get();
        assertNotNull(s);

        s = rkg.get();
        assertNull(s);

        s = rkg.get();
        assertNull(s);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
