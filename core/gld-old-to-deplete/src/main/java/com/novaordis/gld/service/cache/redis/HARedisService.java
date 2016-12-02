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

package com.novaordis.gld.service.cache.redis;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.ContentType;
import com.novaordis.gld.Node;
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.service.cache.CacheServiceBase;

import java.util.List;
import java.util.Set;

public class HARedisService extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

//    private Redis client;
    private long keyExpirationSecs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HARedisService(List<Node> proxies, long keyExpirationSecs, int maxTotal) throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");
//        client = new RedisImpl();
//
//        RedisNodeFactory rnf = new RedisNodeFactory();
//        HADomainConfiguration conf = new HADomainConfigurationImpl(client, rnf, new RoundRobin());
//        conf.setMaxConnectionsPerNode(maxTotal);
//        HADomain d = HADomainFactory.getInstance(conf);
//
//        for (Node n : proxies)
//        {
//            d.create(new RedisNodeConfigurationImpl(n.getHost(), n.getPort()));
//        }
//
//        this.keyExpirationSecs = keyExpirationSecs;
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
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void perform(Operation o) throws Exception {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void set(String key, String value) throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");

//        if (keyExpirationSecs <= 0)
//        {
//            client.set(key, value);
//        }
//        else
//        {
//            client.set(key, value, (int)keyExpirationSecs);
//        }
    }

    @Override
    public String get(String key) throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");

//        return client.get(key);
    }

    @Override
    public Set<String> keys(String pattern)
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
//        Set<String> result = client.keys("*");
//        return result;
    }

    @Override
    public Object getCache()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void stop() throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");
    }

    @Override
    public String delete(String key) throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
