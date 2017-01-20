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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.gld.extensions.jboss.datagrid.common.InfinispanCache;
import io.novaordis.gld.extensions.jboss.datagrid.common.JBossDatagridServiceBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class JBossDatagrid7ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagrid7ServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // fully qualified class name inference based on extension name ----------------------------------------------------

    @Test
    public void fullyQualifiedClassNameInference() throws Exception {

        String extensionName = "jboss-datagrid-7";

        String fqcn = JBossDatagridServiceBase.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);

        assertEquals(JBossDatagrid7Service.class.getName(), fqcn);
    }

    // version ---------------------------------------------------------------------------------------------------------

    @Test
    public void version() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        String version = s.getVersion();

        log.info(version);

        assertNotNull(version);

        String mavenInjectedProjectVersion = System.getProperty("maven.injected.project.version");
        assertNotNull(mavenInjectedProjectVersion);
        assertEquals(mavenInjectedProjectVersion, version);
    }

    // configureAndStartInfinispanCache() ------------------------------------------------------------------------------

    @Test
    public void configureAndStartInfinispanCache() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        InfinispanCache ic = s.configureAndStartInfinispanCache();

        MockRemoteCache mc = (MockRemoteCache)ic.getDelegate();
        assertNotNull(mc);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
