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

import io.novaordis.gld.extensions.mock.MockService;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

    @After
    public void cleanup() {

        MockService.reset();
    }

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

    // extractExtensionNamesFromClasspath() ----------------------------------------------------------------------------

    @Test
    public void extractExtensionNamesFromClasspath_Null() throws Exception {

        try {
            Extensions.extractExtensionNamesFromClasspath(null);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null classpath", msg);
        }
    }

    @Test
    public void extractExtensionNamesFromClasspath_NoExtensions() throws Exception {

        String classpath = "no:extensions:in:this:classpath";

        Set<ExtensionInfo> ei = Extensions.extractExtensionNamesFromClasspath(classpath);

        assertTrue(ei.isEmpty());
    }

    @Test
    public void extractExtensionNamesFromClasspath() throws Exception {

        String classpath = "/Users/ovidiu/runtime/gld/bin/../lib:/Users/ovidiu/runtime/gld/bin/../lib/gld-api-1.0.0-S" +
                "NAPSHOT-24.jar:/Users/ovidiu/runtime/gld/bin/../lib/gld-load-driver-1.0.0-SNAPSHOT-24.jar:/Users/ovi" +
                "diu/runtime/gld/bin/../lib/hamcrest-core-1.3.jar:/Users/ovidiu/runtime/gld/bin/../lib/junit-4.11.jar" +
                ":/Users/ovidiu/runtime/gld/bin/../lib/log4j-1.2.16.jar:/Users/ovidiu/runtime/gld/bin/../lib/novaordi" +
                "s-utilities-4.4.0-SNAPSHOT-5.jar:/Users/ovidiu/runtime/gld/bin/../lib/slf4j-api-1.7.6.jar:/Users/ovi" +
                "diu/runtime/gld/bin/../lib/slf4j-log4j12-1.6.3.jar:/Users/ovidiu/runtime/gld/bin/../lib/snakeyaml-1." +
                "17.jar:/Users/ovidiu/runtime/gld/bin/../extensions/jboss-datagrid-7/jboss-datagrid-7-1.0.0-SNAPSHOT-" +
                "5.jar";

        Set<ExtensionInfo> ei = Extensions.extractExtensionNamesFromClasspath(classpath);

        assertEquals(1, ei.size());

        ExtensionInfo i = ei.iterator().next();

        assertEquals("jboss-datagrid-7", i.getExtensionName());
    }

    // inferExtensionVersion() -----------------------------------------------------------------------------------------

    @Test
    public void inferExtensionVersion_NoSuchExtension() throws Exception {

        String v = Extensions.inferExtensionVersion("no-such-extension");
        assertNull(v);
    }

    @Test
    public void inferExtensionVersion_ClassFailsToInstantiate() throws Exception {

        MockService.configureToFailToInstantiate();

        String v = Extensions.inferExtensionVersion("mock");
        assertNull(v);
    }

    @Test
    public void inferExtensionVersion_InstanceNotAService() throws Exception {

        MockService.configureToFailToInstantiate();

        String v = Extensions.inferExtensionVersion("notaservice");
        assertNull(v);
    }

    @Test
    public void inferExtensionVersion() throws Exception {

        MockService.setVersion("7.7");
        String v = Extensions.inferExtensionVersion("mock");
        assertEquals("7.7", v);
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
