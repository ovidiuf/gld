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

import io.novaordis.gld.api.mockpackage.WinningStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Assert;
import org.junit.Test;

public class ClassLoadingUtilitiesTest extends Assert {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ClassLoadingUtilitiesTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // getInstance() tests ---------------------------------------------------------------------------------------------

    @Test
    public void getInstance_WrongPackageName() throws Exception {

        try {

            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "com.novaordis.gld.mock.nosuchpackate", "Winning", "Strategy");

            fail("should have failed with IllegalArgumentException - wrong package name");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassNotFoundException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongBaseName() throws Exception {

        try {

            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "io.novaordis.gld.api.mockpackage", "winning", "Strategy");

            fail("should have failed with IllegalArgumentException - wrong base name");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue((t instanceof NoClassDefFoundError) || (t instanceof ClassNotFoundException));

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongSuffix() throws Exception {

        try {

            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "io.novaordis.gld.api.mockpackage", "Winning", "Trickery");

            fail("should have failed with IllegalArgumentException - wrong suffix");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassNotFoundException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongType() throws Exception {

        try
        {
            ClassLoadingUtilities.getInstance(
                    MockInterface2.class, "io.novaordis.gld.api.mockpackage", "Winning", "Strategy");

            fail("should have failed with IllegalArgumentException - wrong suffix");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassCastException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_NoNoArgumentConstructor() throws Exception {

        try
        {
            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "io.novaordis.gld.api.mockpackage", "NoNoArgConstructor", "Strategy");

            fail("should have failed with IllegalArgumentException - no no-argument constructor");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof InstantiationException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_PrivateNoArgumentConstructor() throws Exception {

        try {
            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "io.novaordis.gld.api.mockpackage", "PrivateNoArgConstructor", "Strategy");

            fail("should have failed with IllegalArgumentException - no no-argument constructor");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof IllegalAccessException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance() throws Exception {

        MockInterface o =
                ClassLoadingUtilities.getInstance(
                        MockInterface.class, "io.novaordis.gld.api.mockpackage", "Winning", "Strategy");

        WinningStrategy ws = (WinningStrategy)o;

        assertNotNull(ws);
    }

    @Test
    public void getInstance_EmptySuffix() throws Exception {

        MockInterface o =
                ClassLoadingUtilities.getInstance(
                        MockInterface.class, "io.novaordis.gld.api.mockpackage", "WinningStrategy", "");

        WinningStrategy ws = (WinningStrategy)o;

        assertNotNull(ws);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
