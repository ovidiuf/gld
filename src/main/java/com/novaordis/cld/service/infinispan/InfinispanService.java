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

package com.novaordis.cld.service.infinispan;

import com.novaordis.cld.CacheService;
import com.novaordis.cld.Node;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.List;
import java.util.Set;

public class InfinispanService implements CacheService
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_SO_TIMEOUT_MS = 20000;
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final boolean DEFAULT_TCP_KEEPALIVE = true;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int socketTimeout;
    private int maxRetries;
    private boolean tcpKeepAlive;

    private Node node;
    private RemoteCacheManager remoteCacheManager;
    private RemoteCache<String, String> cache;

    private String cacheName;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param cacheName - null is acceptable, it means "default cache"
     */
    public InfinispanService(List<Node> nodes, String password,
                             int maxTotal, long maxWaitMillis,
                             long keyExpirationSecs, String cacheName)
    {
        this.cacheName = cacheName;
        this.node = nodes.get(0);
        this.socketTimeout = DEFAULT_SO_TIMEOUT_MS;
        this.maxRetries = DEFAULT_MAX_RETRIES;
        this.tcpKeepAlive = DEFAULT_TCP_KEEPALIVE;

        ConfigurationBuilder clientBuilder = new ConfigurationBuilder();

        clientBuilder.addServer().
            host(node.getHost()).
            port(node.getPort()).
            socketTimeout(socketTimeout).
            maxRetries(maxRetries);

            // only in Infinispan 6.3.x
            // .tcpKeepAlive(tcpKeepAlive);

        remoteCacheManager = new RemoteCacheManager(clientBuilder.build());
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void set(String key, String value) throws Exception
    {
        cache.put(key, value);
    }

    @Override
    public String get(String key) throws Exception
    {
        return cache.get(key);
    }

    @Override
    public Set<String> keys(String pattern)
    {
        return cache.keySet();
    }

    @Override
    public Object getCache()
    {
        return cache;
    }

    @Override
    public void start() throws Exception
    {
        if (cacheName == null)
        {
            this.cache = remoteCacheManager.getCache();
        }
        else
        {
            this.cache = remoteCacheManager.getCache(cacheName);
        }
    }

    @Override
    public void stop() throws Exception
    {
        remoteCacheManager.stop();
        cache = null;
    }

    @Override
    public boolean isStarted()
    {
        return cache != null;
    }

    @Override
    public String delete(String key) throws Exception
    {
        return cache.remove(key);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "InfinispanService[" + node + "/" + cacheName + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
