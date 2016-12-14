/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.novaordis.gld.service.cache.infinispan;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.EmbeddedNode;
import com.novaordis.gld.Node;
import com.novaordis.gld.service.cache.CacheServiceTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InfinispanServiceTest extends CacheServiceTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(InfinispanServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Unfortunately we can't really start an InfinispanCache as part of a simple unit test - it would require an
     * external process, and so on, so we override the life cycle test with something simpler.
     */
//    @Test
//    public void lifeCycle() throws Exception {
//
//        Service s = getServiceToTest(new MockConfiguration(), Collections.singletonList(getTestNode()));
//
//        assertFalse(s.isStarted());
//
//        // stopping an already started stopped instance should be a noop
//
//        s.stop();
//
//        assertFalse(s.isStarted());
//    }

    // start() ---------------------------------------------------------------------------------------------------------

//    @Test
//    public void start_CacheWithTheSpecifiedNameExists() throws Exception {
//
//        String name = "test-cache";
//
//        MockRemoteCache mc = new MockRemoteCache();
//        MockRemoteCacheManager mcm = new MockRemoteCacheManager();
//        mcm.setCache(name, mc);
//        InfinispanService is = new InfinispanService();
//        is.setRemoteCacheManager(mcm);
//        is.setName(name);
//
//        assertNull(is.getCache());
//
//        is.start();
//
//        assertEquals(mc, is.getCache());
//    }
//
//    @Test
//    public void start_CacheWithTheSpecifiedNameDoesNotExist() throws Exception {
//
//        MockRemoteCacheManager mcm = new MockRemoteCacheManager();
//        InfinispanService is = new InfinispanService();
//        is.setRemoteCacheManager(mcm);
//        is.setName("pretty-sure-there-is-no-such-cache");
//
//        assertNull(is.getCache());
//
//        try {
//
//            is.start();
//            fail("should have thrown Exception");
//        }
//        catch(UserErrorException e) {
//
//            String msg = e.getMessage();
//            log.info(msg);
//            assertTrue(msg.startsWith(
//                    "cache with name 'pretty-sure-there-is-no-such-cache' not found amongst the configured caches"));
//        }
//
//        assertNull(is.getCache());
//    }

    // setTarget() -----------------------------------------------------------------------------------------------------

//    @Test
//    public void setTarget_EmbeddedNode() throws Exception {
//
//        List<Node> nodes = new ArrayList<>(Collections.singletonList((Node) new EmbeddedNode()));
//
//        InfinispanService s = new InfinispanService();
//
//        s.setTarget(nodes);
//
//        s.start();
//
//        Object o = s.getCache();
//        assertNotNull(o);
//        assertTrue(o instanceof EmbeddedCache);
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected InfinispanService getServiceToTest(Configuration configuration, List<Node> nodes) throws Exception {

        InfinispanService service = new InfinispanService();
        service.setTarget(nodes);
        return service;
    }

    @Override
    protected Node getTestNode()
    {
        return new EmbeddedNode();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
