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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public abstract class KeyProviderTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(KeyProviderTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        KeyProvider p = getKeyProviderToTest();

        assertFalse(p.isStarted());

        p.start();

        assertTrue(p.isStarted());

        //
        // start() should be idempotent
        //

        p.start();

        assertTrue(p.isStarted());

        p.stop();

        assertFalse(p.isStarted());

        //
        // stop() should be idempotent
        //

        p.stop();

        assertFalse(p.isStarted());
    }

    @Test
    public void instanceNotStarted() throws Exception {

        KeyProvider p = getKeyProviderToTest();

        assertFalse(p.isStarted());

        try {

            p.next();
            fail("should throw exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract KeyProvider getKeyProviderToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
