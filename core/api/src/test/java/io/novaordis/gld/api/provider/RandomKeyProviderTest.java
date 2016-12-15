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

package io.novaordis.gld.api.provider;

import io.novaordis.gld.api.KeyProviderTest;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RandomKeyProviderTest extends KeyProviderTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(RandomKeyProviderTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        int keySize = p.getKeySize();
        assertEquals(ServiceConfiguration.DEFAULT_KEY_SIZE, keySize);

        Long remainingKeyCount = p.getRemainingKeyCount();
        assertNull(remainingKeyCount);
    }

    @Test
    public void setKeySize() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        int keySize = 777;
        p.setKeySize(keySize);
        assertEquals(keySize, p.getKeySize());
    }

    @Test
    public void setKeySize_InstanceStarted() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        p.start();

        try {

            p.setKeySize(1);
            fail("should throw exception");
        }
        catch(IllegalStateException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void setKeyCount() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        long keyCount = 10;
        p.setKeyCount(keyCount);
        assertEquals(keyCount, p.getRemainingKeyCount().longValue());
    }

    @Test
    public void setKeyCount_Null() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        p.setKeyCount(null);
        assertNull(p.getRemainingKeyCount());
    }

    @Test
    public void setKeyCount_InstanceStarted() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        p.start();

        try {

            p.setKeyCount(1L);
            fail("should throw exception");
        }
        catch(IllegalStateException e) {

            log.info(e.getMessage());
        }
    }

    // next ------------------------------------------------------------------------------------------------------------

    @Test
    public void next_UnlimitedKeys() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        p.start();

        String key = p.next();
        assertNotNull(key);
        assertNull(p.getRemainingKeyCount());
    }

    @Test
    public void next_KeyLimit() throws Exception {

        RandomKeyProvider p = getKeyProviderToTest();

        p.setKeyCount(3L);

        p.start();

        String key = p.next();
        assertEquals(2, p.getRemainingKeyCount().longValue());
        assertNotNull(key);

        key = p.next();
        assertEquals(1, p.getRemainingKeyCount().longValue());
        assertNotNull(key);

        key = p.next();
        assertEquals(0, p.getRemainingKeyCount().longValue());
        assertNotNull(key);

        key = p.next();
        assertNull(key);
        assertEquals(0, p.getRemainingKeyCount().longValue());

        key = p.next();
        assertNull(key);
        assertEquals(0, p.getRemainingKeyCount().longValue());
    }

    @Test
    public void next_KeySize() throws Exception {

        int keySize = 10;
        RandomKeyProvider p = new RandomKeyProvider();
        p.setKeySize(keySize);

        p.start();

        String s = p.next();
        log.info(s);
        assertEquals(keySize, s.length());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected RandomKeyProvider getKeyProviderToTest() throws Exception {

        return new RandomKeyProvider();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
