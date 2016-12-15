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
import io.novaordis.utilities.UserErrorException;
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

            ClassLoadingUtilities.
                    getInstance(MockInterface.class, "io.novaordis.gld.api.nosuchpackage.WinningStrategy");

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("class io\\.novaordis\\.gld\\.api\\.nosuchpackage\\.WinningStrategy not found"));

            Throwable t = e.getCause();
            assertTrue(t instanceof ClassNotFoundException);
        }
    }

    @Test
    public void getInstance_WrongType() throws Exception {

        try
        {
            ClassLoadingUtilities.
                    getInstance(MockInterface2.class, "io.novaordis.gld.api.mockpackage.WinningStrategy");

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "io\\.novaordis\\.gld\\.api\\.mockpackage\\.WinningStrategy does not implement interface io.novaordis.gld.api.MockInterface2"));

            Throwable t = e.getCause();
            assertTrue(t instanceof ClassCastException);
        }
    }

    @Test
    public void getInstance_NoNoArgumentConstructor() throws Exception {

        try {
            ClassLoadingUtilities.getInstance(
                    MockInterface.class, "io.novaordis.gld.api.mockpackage.NoNoArgConstructorStrategy");

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "class '.*' failed to instantiate, most likely the class has no no-argument constructor or a private no-argument constructor: .*"));

            Throwable t = e.getCause();
            assertTrue(t instanceof InstantiationException);
        }
    }

    @Test
    public void getInstance_PrivateNoArgumentConstructor() throws Exception {

        try {
            ClassLoadingUtilities.
                    getInstance(MockInterface.class, "io.novaordis.gld.api.mockpackage.PrivateNoArgConstructorStrategy");

            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "class '.*' failed to instantiate, most likely the class has no no-argument constructor or a private no-argument constructor: .*"));

            Throwable t = e.getCause();
            assertTrue(t instanceof IllegalAccessException);
        }
    }

    @Test
    public void getInstance() throws Exception {

        MockInterface o = ClassLoadingUtilities.
                getInstance(MockInterface.class, "io.novaordis.gld.api.mockpackage.WinningStrategy");

        WinningStrategy ws = (WinningStrategy)o;

        assertNotNull(ws);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
