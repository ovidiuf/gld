/*
 * Copyright (c) 2017 Nova Ordis LLC
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

import io.novaordis.gld.extensions.jboss.datagrid.common.HotRodEndpointAddress;
import io.novaordis.gld.extensions.jboss.datagrid.common.InfinispanCache;
import io.novaordis.gld.extensions.jboss.datagrid.common.JBossDatagridServiceBase;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ServerConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class JBossDatagrid6ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagrid6ServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // fully qualified class name inference based on extension name ----------------------------------------------------

    @Test
    public void fullyQualifiedClassNameInference() throws Exception {

        String extensionName = "jboss-datagrid-6";

        String fqcn = JBossDatagridServiceBase.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);

        assertEquals(JBossDatagrid6Service.class.getName(), fqcn);
    }

    // version ---------------------------------------------------------------------------------------------------------

    @Test
    public void version() throws Exception {

        JBossDatagrid6Service s = new JBossDatagrid6Service();

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

        JBossDatagrid6Service s = new JBossDatagrid6Service();
        s.addNode(new HotRodEndpointAddress("mock-host:22333"));

        MockCacheManagerFactory mcmf = new MockCacheManagerFactory();
        s.setCacheManagerFactory(mcmf);

        InfinispanCache ic = s.configureAndStartInfinispanCache();

        MockRemoteCache mc = (MockRemoteCache)ic.getDelegate();
        assertNotNull(mc);

        assertEquals(MockRemoteCacheManager.DEFAULT_CACHE_NAME, mc.getName());

        MockRemoteCacheManager mcm = (MockRemoteCacheManager)mc.getRemoteCacheManager();
        Configuration infinispanConfiguration = mcm.getConfiguration();

        List<ServerConfiguration> scs = infinispanConfiguration.servers();
        assertEquals(1, scs.size());
        ServerConfiguration sc = scs.get(0);
        assertEquals("mock-host", sc.host());
        assertEquals(22333, sc.port());
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
