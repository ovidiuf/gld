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
import com.novaordis.gld.CacheService;
import com.novaordis.gld.Operation;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShardedJedisRedisService implements CacheService
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ShardedJedisPool pool;

    // -1L means don't expire
    private long keyExpirationSecs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ShardedJedisRedisService(List<Node> nodes, String password,
                                    int maxTotal, long maxWaitMillis,
                                    long keyExpirationSecs)
    {
        JedisPoolConfig c = new JedisPoolConfig();

        c.setMaxTotal(maxTotal);
        c.setMaxIdle(12);
        c.setMaxWaitMillis(maxWaitMillis);
        c.setTestOnBorrow(false);

        this.keyExpirationSecs = keyExpirationSecs;

        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();

        for (Node n : nodes)
        {
            System.out.println("build shard info for " + n);
            JedisShardInfo jsi = new JedisShardInfo(n.getHost(), n.getPort());

            if (password != null)
            {
                jsi.setPassword(password);
            }

            shards.add(jsi);
        }

        this.pool = new ShardedJedisPool(c, shards);
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
    public void setTarget(List<Node> nodes)
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void set(String key, String value) throws Exception
    {
        ShardedJedis sj = pool.getResource();

        try
        {
            if (keyExpirationSecs < 0)
            {
                sj.set(key, value);
            }
            else
            {
                // NX -  only set the key if it does not already exist.
                // EX - seconds
                sj.set(key, value, "NX", "EX", keyExpirationSecs);
            }
        }
        finally
        {
            pool.returnResource(sj);
        }
    }

    @Override
    public String get(String key) throws Exception
    {
        ShardedJedis sj = pool.getResource();

        try
        {
            return sj.get(key);
        }
        finally
        {
            pool.returnResource(sj);
        }
    }

    @Override
    public Set<String> keys(String pattern)
    {
        ShardedJedis sj = pool.getResource();

        try
        {
            // kludge - we rely on the fact there's a single shard
            // this must be changed
            Set<String> result = sj.getShard("").keys("*");
            return result;
        }
        finally
        {
            pool.returnResource(sj);
        }
    }

    @Override
    public String delete(String key) throws Exception
    {
        throw new Exception("NOT YET IMPLEMENTED");
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



    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
