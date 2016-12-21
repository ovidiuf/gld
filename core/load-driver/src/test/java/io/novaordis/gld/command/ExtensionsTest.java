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

package io.novaordis.gld.command;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/21/16
 */
public class ExtensionsTest extends CommandTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ExtensionsTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Test ------------------------------------------------------------------------------------------------------------

    @Test
    public void toCommand() throws Exception {

        Extensions e = (Extensions)Command.toCommand(Collections.singletonList("extensions"));
        assertNotNull(e);
    }

    @Test
    public void lifecycle() throws Exception {

        Extensions v = getCommandToTest();
        v.execute();
    }

    // extractExtensionInfoFromClasspath() --------------------------------------------------------------------------------

    @Test
    public void extractExtensionInfoFromClasspath_Null() throws Exception {

        try {
            Extensions.extractExtensionInfoFromClasspath(null);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null classpath", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected Extensions getCommandToTest() throws Exception {

        return new Extensions();
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
