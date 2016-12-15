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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.cache.embedded.EmbeddedCacheService;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public abstract class ImplementationConfigurationTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ImplementationConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // extensionNameToExtensionServiceFullyQualifiedClassName() --------------------------------------------------------

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_EmbeddedCache() throws Exception {

        String fqcn = ImplementationConfiguration.
                extensionNameToExtensionServiceFullyQualifiedClassName("embedded", ServiceType.cache);

        assertEquals(EmbeddedCacheService.class.getName(), fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension() throws Exception {

        String fqcn = ImplementationConfiguration.
                extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid", null);

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagridService";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension_MajorVersion()
            throws Exception {

        String fqcn = ImplementationConfiguration.
                extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid-7", null);

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid7Service";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension_MajorAndMinorVersion()
            throws Exception {

        String fqcn = ImplementationConfiguration.
                extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid-7.8", null);

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid78Service";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_InvalidName_StartsWithNumber()
            throws Exception {

        try {

            ImplementationConfiguration.extensionNameToExtensionServiceFullyQualifiedClassName("7a", null);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid extension name '7a', extension name component starts with a number", msg);
        }
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_InvalidName_StartsWithNumber2()
            throws Exception {

        try {

            ImplementationConfiguration.extensionNameToExtensionServiceFullyQualifiedClassName("a-2b-c", null);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid extension name 'a-2b-c', extension name component starts with a number", msg);
        }
    }

    // configuration constraints ---------------------------------------------------------------------------------------

    @Test
    public void bothNameAndClassArePresent() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ImplementationConfiguration.EXTENSION_NAME_LABEL, "test");
        m.put(ImplementationConfiguration.EXTENSION_CLASS_LABEL, "test");

        try {

            getConfigurationToTest(m, new File("."));
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("mutually exclusive implementation name and class are both present", msg);
        }
    }

    // typed access ----------------------------------------------------------------------------------------------------

    @Test
    public void getExtensionName() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ImplementationConfiguration.EXTENSION_NAME_LABEL, "test");
        ImplementationConfiguration c = getConfigurationToTest(m, new File("."));

        assertEquals("test", c.getExtensionName());
    }

    @Test
    public void getExtensionName_InvalidType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ImplementationConfiguration.EXTENSION_NAME_LABEL, 2);
        ImplementationConfiguration c = getConfigurationToTest(m, new File("."));

        try {

            c.getExtensionName();
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected name to be a String but it is a(n) Integer: \"2\"", msg);

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            assertNotNull(e2);
        }
    }

    @Test
    public void getExtensionClass() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ImplementationConfiguration.EXTENSION_CLASS_LABEL, "test");
        ImplementationConfiguration c = getConfigurationToTest(m, new File("."));

        assertEquals("test", c.getExtensionClass());
    }

    @Test
    public void getExtensionClass_InvalidType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        m.put(ImplementationConfiguration.EXTENSION_CLASS_LABEL, 2);
        ImplementationConfiguration c = getConfigurationToTest(m, new File("."));

        try {

            c.getExtensionClass();
            fail("should have thrown exception");

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected class to be a String but it is a(n) Integer: \"2\"", msg);

            IllegalStateException e2 = (IllegalStateException)e.getCause();
            assertNotNull(e2);
        }
    }

    @Test
    public void neitherNameOrClassArePresent() throws Exception {

        Map<String, Object> m = new HashMap<>();
        ImplementationConfiguration c = getConfigurationToTest(m, new File("."));

        try {

            c.getExtensionName();
            fail("should have thrown exception");

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("neither implementation name or class are present", msg);
        }

        try {

            c.getExtensionClass();
            fail("should have thrown exception");

        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("neither implementation name or class are present", msg);
        }
    }

    // Untyped Access --------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract ImplementationConfiguration getConfigurationToTest(
            Map<String, Object> rawConfigurationMap, File configurationDirectory) throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
