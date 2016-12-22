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
import com.novaordis.gld.ContentType;
import com.novaordis.gld.EmbeddedNode;
import com.novaordis.gld.Node;
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.service.cache.CacheServiceBase;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InfinispanService extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_SO_TIMEOUT_MS = 20000;
    public static final int DEFAULT_MAX_RETRIES = 3;

    public static final boolean DEFAULT_TCP_KEEPALIVE = true;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Node> nodes;
    private RemoteCacheManager remoteCacheManager;
    private RemoteCache<String, Object> cache;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @see InfinispanService#setTarget(List)
     */
    public InfinispanService() {
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public ContentType getContentType() {
        return ContentType.KEYVALUE;
    }

    @Override
    public void setConfiguration(Configuration c) {

        //
        // noop
        //
    }

    @Override
    public void setTarget(List<Node> nodes) {

        this.nodes = nodes;

        if (nodes != null && !nodes.isEmpty()) {

            ConfigurationBuilder clientBuilder = null;

            // Add all the nodes to the configuration builder
            for (Node node : nodes) {

                if (node instanceof EmbeddedNode) {

                    //
                    // support for "embedded" mode
                    //

                    setRemoteCacheManager(new EmbeddedRemoteCacheManager());

                    if (nodes.size() > 1) {
                        throw new IllegalArgumentException(
                                "when using embedded nodes, just one node is sufficient to configure the instance");
                    }

                    return;
                }

                if (clientBuilder == null) {
                    clientBuilder = new ConfigurationBuilder();
                }

                clientBuilder.addServer().
                        host(node.getHost()).
                        port(node.getPort());
            }

            //noinspection unused
            boolean tcpKeepAlive = DEFAULT_TCP_KEEPALIVE;
            int socketTimeout = DEFAULT_SO_TIMEOUT_MS;
            int maxRetries = DEFAULT_MAX_RETRIES;

            //
            // clientBuilder will never be null here, but make the compiler happy
            //
            if (clientBuilder == null) {
                throw new IllegalStateException("null clientBuilder");
            }
            clientBuilder.socketTimeout(socketTimeout).maxRetries(maxRetries);
            // only in Infinispan 6.3.x .tcpKeepAlive(tcpKeepAlive);
            RemoteCacheManager rcm = new RemoteCacheManager(clientBuilder.build());
            setRemoteCacheManager(rcm);
        }
    }

    @Override
    public void perform(Operation o) throws Exception {

        insureStarted();
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception {

        String name = getName();

        if (name == null) {

            this.cache = remoteCacheManager.getCache();
        }
        else {

            RemoteCache<String, Object> cache = remoteCacheManager.getCache(name);

            if (cache == null) {

                //
                // no cache with such a name
                //

                throw new UserErrorException(
                        "cache with name '" + name + "' not found amongst the configured caches on " + nodes);
            }

            setCache(cache);
        }
    }

    @Override
    public void stop() throws Exception {

        remoteCacheManager.stop();
        cache = null;
    }

    @Override
    public boolean isStarted()
    {
        return cache != null;
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void set(String key, String value) throws Exception
    {
        insureStarted();
        cache.put(key, value);
    }

    @Override
    public String get(String key) throws Exception
    {
        insureStarted();
        return (String)cache.get(key);
    }

    @Override
    public Set<String> keys(String pattern)
    {
        insureStarted();
        return cache.keySet();
    }

    @Override
    public String delete(String key) throws Exception
    {
        insureStarted();
        return (String)cache.remove(key);
    }

    @Override
    public Object getCache()
    {
        return cache;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("InfinispanService[");

        if (nodes == null) {
            sb.append("null");
        }
        else {
            for (Iterator<Node> i = nodes.iterator(); i.hasNext(); ) {
                sb.append(i.next());
                if (i.hasNext()) {
                    sb.append(',');
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            String name = getName();
            sb.append("/").append(name == null ? "DEFAULT" : name);
        }

        sb.append("]");
        return sb.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Needed for testing.
     */
    void setRemoteCacheManager(RemoteCacheManager rcm) {

        this.remoteCacheManager = rcm;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setCache(RemoteCache<String, Object> cache) {
        this.cache = cache;
    }


}
