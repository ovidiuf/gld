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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public abstract class JBossDatagridServiceBaseTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagridServiceBaseTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // identity --------------------------------------------------------------------------------------------------------

    @Test
    public void identity() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        assertTrue(s.getNodes().isEmpty());
    }

    // configure -------------------------------------------------------------------------------------------------------

    @Test
    public void configure_NoNodes() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

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

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

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

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

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

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

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

    @Test
    public void configure_NoCacheName() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

        MockServiceConfiguration mc = new MockServiceConfiguration();
        MockImplementationConfiguration mic = mc.getImplementationConfiguration();
        mic.setNodes(Collections.singletonList("somehost:12345"));

        s.configure(mc);

        //
        // the service will use the default cache, as defined by Infinispan
        //
        assertNull(s.getCacheName());
    }

    @Test
    public void configure_SomeCacheName() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

        MockServiceConfiguration mc = new MockServiceConfiguration();
        MockImplementationConfiguration mic = mc.getImplementationConfiguration();
        mic.setNodes(Collections.singletonList("somehost:12345"));

        mic.setCacheName("Something");

        s.configure(mc);

        assertEquals("Something", s.getCacheName());
    }

    @Test
    public void configure_InvalidCacheName() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();

        MockServiceConfiguration mc = new MockServiceConfiguration();
        MockImplementationConfiguration mic = mc.getImplementationConfiguration();
        mic.setNodes(Collections.singletonList("somehost:12345"));

        mic.setCacheName(4);

        try {

            s.configure(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("IllegalStateException expected cache to be a String but it is a(n) Integer: \"4\"", msg);
        }
    }

    // start -----------------------------------------------------------------------------------------------------------

    @Test
    public void start_NoNodes() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
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

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.setLoadStrategy(new MockLoadStrategy());
        s.addNode(new HotRodEndpointAddress("mock-host"));

        //
        // simulate random failure of the underlying Infinispan code
        //

        RuntimeException re = new RuntimeException("SYNTHETIC");

        ((GenericJBossDatagridService)s).makeConfigureAndStartInfinispanCacheFail(re);

        try {

            s.start();
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("failed to start jboss datagrid service: RuntimeException SYNTHETIC", msg);
            Throwable cause = e.getOriginalCause();
            assertEquals(re, cause);
        }
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception {

        MockLoadStrategy ms = new MockLoadStrategy();
        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.setLoadStrategy(ms);

        s.addNode(new HotRodEndpointAddress("mock-host"));

        //
        // mocks insure that all goes smoothly
        //

        //
        // no cache name is specified, we assume default cache
        //

        assertFalse(ms.isStarted());

        assertFalse(s.isStarted());

        assertNull(s.getCacheName());

        s.start();

        assertTrue(s.isStarted());

        //
        // test idempotency
        //
        s.start();

        InfinispanCache c = s.getCache();

        assertNotNull(c);

        assertNotNull(c.getDelegate());

        assertEquals(GenericJBossDatagridService.DEFAULT_CACHE_NAME, s.getCacheName());

        assertTrue(ms.isStarted());

        //noinspection unchecked
        c.put("test-key", "test-value");

        assertEquals("test-value", c.get("test-key"));

        s.stop();

        assertFalse(s.isStarted());

        //
        // test idempotency
        //
        s.stop();

        assertNull(s.getCache());

        assertFalse(ms.isStarted());
    }

    @Test
    public void lifecycle_SpecificCacheName() throws Exception {

        String cacheName = "test-cache";

        MockLoadStrategy ms = new MockLoadStrategy();
        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));
        s.setLoadStrategy(ms);
        s.setCacheName(cacheName);

        //
        // mocks insure that all goes smoothly
        //

        //
        // no cache name is specified, we assume default cache
        //

        assertFalse(ms.isStarted());

        assertFalse(s.isStarted());

        assertEquals("test-cache", s.getCacheName());

        s.start();

        assertTrue(s.isStarted());

        //
        // test idempotency
        //
        s.start();

        InfinispanCache c = s.getCache();

        assertNotNull(c);

        assertNotNull(c.getDelegate());

        assertTrue(ms.isStarted());

        assertEquals("test-cache", s.getCacheName());

        s.stop();

        assertFalse(s.isStarted());

        //
        // test idempotency
        //
        s.stop();

        assertNull(s.getCache());

        assertFalse(ms.isStarted());
    }

    // cache operations ------------------------------------------------------------------------------------------------

    @Test
    public void get_NotStarted() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));

        assertFalse(s.isStarted());

        try {

            s.get("test-key");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
        }
    }

    @Test
    public void put_NotStarted() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));

        assertFalse(s.isStarted());

        try {

            s.put("test-key", "test-value");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
        }
    }

    @Test
    public void keys_NotStarted() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));

        assertFalse(s.isStarted());

        try {

            s.keys();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
        }
    }

    @Test
    public void remove_NotStarted() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));

        assertFalse(s.isStarted());

        try {

            s.remove("test");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
        }
    }

    @Test
    public void cacheOperations() throws Exception {

        JBossDatagridServiceBase s = getJBossDatagridServiceBaseToTest();
        s.addNode(new HotRodEndpointAddress("mock-host"));
        s.setLoadStrategy(new MockLoadStrategy());

        s.start();

        String value = s.get("test-key");
        assertNull(value);

        s.put("test-key", "test-value");
        assertEquals("test-value", s.get("test-key"));

        Set<String> keys = s.keys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key"));

        //
        // put idempotence
        //

        s.put("test-key", "test-value");
        assertEquals("test-value", s.get("test-key"));

        keys = s.keys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key"));

        s.put("test-key-2", "test-value-2");
        assertEquals("test-value-2", s.get("test-key-2"));

        keys = s.keys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("test-key-2"));

        s.remove("test-key");

        keys = s.keys();
        assertEquals(1, keys.size());
        assertTrue(keys.contains("test-key-2"));

        s.remove("test-key-2");

        keys = s.keys();
        assertEquals(0, keys.size());

        s.stop();
    }

    // extensionNameToExtensionServiceFullyQualifiedClassName() --------------------------------------------------------

    @Test
    public void extensionNameToExtensionServiceFullyQualifiedClassName() throws Exception {

        String s = JBossDatagridServiceBase.extensionNameToExtensionServiceFullyQualifiedClassName("blah-blah-blah");
        assertEquals("io.novaordis.gld.extensions.blah.blah.blah.BlahBlahBlahService", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract JBossDatagridServiceBase getJBossDatagridServiceBaseToTest() throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
