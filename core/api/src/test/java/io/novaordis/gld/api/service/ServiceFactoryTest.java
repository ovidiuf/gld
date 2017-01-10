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

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.MockService;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.cache.embedded.EmbeddedCacheService;
import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class ServiceFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // buildInstance() -------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance_ImplementationIsAConventionalName() throws Exception {

        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();

        sc.setImplementationConfigurationMap(buildImplementationConfigMap(ServiceFactory.EMBEDDED_MARKER, null));

        Service service = ServiceFactory.buildInstance(sc);

        EmbeddedCacheService lcs = (EmbeddedCacheService)service;

        assertNotNull(lcs);
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName_ClassNotFound() throws Exception {

        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementationConfigurationMap(buildImplementationConfigMap(
                null,
                "i.am.sure.there.is.no.such.Class"));

        try {

            ServiceFactory.buildInstance(sc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("extension class i.am.sure.there.is.no.such.Class not found, make sure the corresponding extension was installed",
                    msg);
        }
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName_ImplementationIsNotAService()
            throws Exception {

        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementationConfigurationMap(buildImplementationConfigMap(
                null,
                "io.novaordis.gld.api.MockLoadDriver"));

        try {

            ServiceFactory.buildInstance(sc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("io.novaordis.gld.api.MockLoadDriver is not a Service implementation", msg);
        }
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName_ImplementationNotOfCorrectType()
            throws Exception {

        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementationConfigurationMap(buildImplementationConfigMap(
                null,
                "io.novaordis.gld.api.MockService"));

        try {

            ServiceFactory.buildInstance(sc);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("io.novaordis.gld.api.MockService is not a " + sc.getType() +" Service", msg);
        }
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName() throws Exception {

        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementationConfigurationMap(buildImplementationConfigMap(
                null,
                "io.novaordis.gld.api.cache.MockCacheService"));

        Service service = ServiceFactory.buildInstance(sc);

        MockService ms = (MockService)service;
        assertNotNull(ms);
    }

    // extensionNameToExtensionServiceFullyQualifiedClassName() --------------------------------------------------------

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_EmbeddedCache() throws Exception {

        String fqcn = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("embedded-cache");
        assertEquals(EmbeddedCacheService.class.getName(), fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension() throws Exception {

        String fqcn = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid");

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagridService";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension_MajorVersion()
            throws Exception {

        String fqcn = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid-7");

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid7Service";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_RegularExtension_MajorAndMinorVersion()
            throws Exception {

        String fqcn = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("jboss-datagrid-7.8");

        String expected = "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid78Service";
        assertEquals(expected, fqcn);
    }

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName_InvalidName_StartsWithNumber()
            throws Exception {

        try {

            ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("7a");
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

            ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName("a-2b-c");
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("invalid extension name 'a-2b-c', extension name component starts with a number", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private Map<String, Object> buildImplementationConfigMap(String extensionName, String className) {

        Map<String, Object> map = new HashMap<>();

        if (extensionName != null && className != null) {
            fail("can't have both extension name and class name");
        }

        if (extensionName != null) {
            map.put(ImplementationConfiguration.EXTENSION_NAME_LABEL, extensionName);
        }

        if (className != null) {
            map.put(ImplementationConfiguration.EXTENSION_CLASS_LABEL, className);
        }

        return map;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
