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

import io.novaordis.gld.api.service.ServiceFactory;
import io.novaordis.utilities.UserErrorException;
import org.infinispan.client.hotrod.RemoteCache;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        String fqcn = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);

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

    // identity --------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();
        assertTrue(s.getNodes().isEmpty());
    }

    // configure -------------------------------------------------------------------------------------------------------

    @Test
    public void configure_NoNodes() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        MockServiceConfiguration mc = new MockServiceConfiguration();

        try {

            s.configure(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("at least one JDG node must be specified", msg);
        }
    }

    @Test
    public void configure_InvalidContent() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        MockServiceConfiguration mc = new MockServiceConfiguration();

        MockImplementationConfiguration mic = mc.getImplementationConfiguration();

        List<Object> content = new ArrayList<>();
        content.add("somehost:12345");
        content.add(1);

        mic.setNodes(content);

        try {

            s.configure(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("'nodes' should be a String list, but it was found to contain Integers", msg);
        }
    }

    @Test
    public void configure_OneNode() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        MockServiceConfiguration mc = new MockServiceConfiguration();
        MockImplementationConfiguration mic = mc.getImplementationConfiguration();

        mic.setNodes(Collections.singletonList("somehost:12345"));

        s.configure(mc);

        List<HotRodEndpointAddress> nodes = s.getNodes();
        assertEquals(1, nodes.size());
        assertEquals("somehost", nodes.get(0).getHost());
        assertEquals(12345, nodes.get(0).getPort());
    }

    @Test
    public void configure_TwoNodes() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();

        MockServiceConfiguration mc = new MockServiceConfiguration();
        MockImplementationConfiguration mic = mc.getImplementationConfiguration();

        mic.setNodes(Arrays.asList("somehost:12345", "somehost2:12346"));

        s.configure(mc);

        List<HotRodEndpointAddress> nodes = s.getNodes();
        assertEquals(2, nodes.size());
        assertEquals("somehost", nodes.get(0).getHost());
        assertEquals(12345, nodes.get(0).getPort());
        assertEquals("somehost2", nodes.get(1).getHost());
        assertEquals(12346, nodes.get(1).getPort());
    }

    // start -----------------------------------------------------------------------------------------------------------

    @Test
    public void start_NoNodes() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();
        s.setLoadStrategy(new MockLoadStrategy());

        try {

            s.start();
            fail("should throw IllegalStateException, service not configured");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("unconfigured jboss datagrid 7 service: no nodes", msg);
        }
    }

    @Test
    public void start_GenericFailure() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();
        s.setLoadStrategy(new MockLoadStrategy());
        s.addNode(new HotRodEndpointAddress("mock-host"));

        final MockRemoteCacheManager mcb = new MockRemoteCacheManager();
        s.setCacheManagerFactory(infinispanConfiguration -> mcb);

        //
        // make the mock configuration builder break with a random failure
        //

        RuntimeException re = new RuntimeException("SYNTHETIC");

        mcb.makeFail("getCache", re);

        try {

            s.start();
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("failed to start jboss datagrid 7 service: RuntimeException SYNTHETIC", msg);
            Throwable cause = e.getOriginalCause();
            assertEquals(re, cause);
        }
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        JBossDatagrid7Service s = new JBossDatagrid7Service();
        MockLoadStrategy ms = new MockLoadStrategy();
        s.setLoadStrategy(ms);
        s.addNode(new HotRodEndpointAddress("mock-host"));

        //
        // mocks insure that all goes smoothly
        //

        final MockRemoteCacheManager mcb = new MockRemoteCacheManager();
        s.setCacheManagerFactory(infinispanConfiguration -> mcb);

        assertFalse(ms.isStarted());

        assertFalse(s.isStarted());

        s.start();

        assertTrue(s.isStarted());

        //
        // test idempotency
        //
        s.start();

        RemoteCache c = s.getCache();

        assertNotNull(c);

        assertTrue(c instanceof MockRemoteCache);

        assertTrue(ms.isStarted());

        s.stop();

        assertFalse(s.isStarted());

        //
        // test idempotency
        //
        s.stop();

        assertNull(s.getCache());

        assertFalse(ms.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
