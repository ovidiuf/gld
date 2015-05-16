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

import com.novaordis.gld.CacheService;
import com.novaordis.gld.Configuration;
import com.novaordis.gld.ContentType;
import com.novaordis.gld.Node;
import com.novaordis.gld.Operation;
import com.novaordis.gld.UserErrorException;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.Iterator;
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

    private List<Node> nodes;
    private RemoteCacheManager remoteCacheManager;
    private RemoteCache<String, String> cache;

    private String cacheName;

    private Configuration configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @see InfinispanService#setTarget(List)
     * @see InfinispanService#setCacheName(String)
     */
    public InfinispanService()
    {
    }

    public InfinispanService(List<Node> nodes, String cacheName)
    {
        setTarget(nodes);
        setCacheName(cacheName);
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public ContentType getContentType()
    {
        return ContentType.KEYVALUE;
    }

    @Override
    public void setConfiguration(Configuration c)
    {
        this.configuration = c;
    }

    @Override
    public void setTarget(List<Node> nodes)
    {
        this.nodes = nodes;

        int socketTimeout = DEFAULT_SO_TIMEOUT_MS;
        int maxRetries = DEFAULT_MAX_RETRIES;
        boolean tcpKeepAlive = DEFAULT_TCP_KEEPALIVE;

        ConfigurationBuilder clientBuilder = new ConfigurationBuilder();

        // Add all the nodes to the configuration builder
        for (Node node : nodes)
        {
            clientBuilder.addServer().
                host(node.getHost()).
                port(node.getPort());
        }

        clientBuilder.
            socketTimeout(socketTimeout).
            maxRetries(maxRetries);

        // only in Infinispan 6.3.x
        // .tcpKeepAlive(tcpKeepAlive);

        remoteCacheManager = new RemoteCacheManager(clientBuilder.build());
    }

    /**
     * @see com.novaordis.gld.Service#configure(List)
     */
    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException
    {
        // noop
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        insureStarted();

        throw new RuntimeException("NOT YET IMPLEMENTED");
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
        return cache.get(key);
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
        return cache.remove(key);
    }

    @Override
    public Object getCache()
    {
        return cache;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @param cacheName - null is acceptable, it means "default cache"
     */
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("InfinispanService[");

        for(Iterator<Node> i = nodes.iterator(); i.hasNext(); )
        {
            sb.append(i.next());

            if (i.hasNext())
            {
                sb.append(',');
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("/").append(cacheName).append("]");

        return sb.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void insureStarted() throws IllegalStateException
    {
        if (!isStarted())
        {
            throw new IllegalStateException(this + " not started");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
