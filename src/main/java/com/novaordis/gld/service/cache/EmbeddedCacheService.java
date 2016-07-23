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

package com.novaordis.gld.service.cache;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.ContentType;
import com.novaordis.gld.Node;
import com.novaordis.gld.Operation;
import com.novaordis.gld.Util;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EmbeddedCacheService extends CacheServiceBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(EmbeddedCacheService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> embeddedCache;
    private boolean started;
    private Configuration configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedCacheService()
    {
        this(0);
    }

    public EmbeddedCacheService(int initialPopulation)
    {
        embeddedCache = new ConcurrentHashMap<>();

        Random random = new Random(System.currentTimeMillis() * 17L);

        if (initialPopulation > 0)
        {
            // generate random keys and values
            for(int i = 0; i < initialPopulation; i ++)
            {
                String key = Util.getRandomString(random, 10, 10);
                String value = Util.getRandomString(random, 10, 10);;
                embeddedCache.put(key, value);
            }
        }
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
        log.info("setting target to " + nodes + " is a noop");
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception
    {
        this.started = true;
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    @Override
    public void stop() throws Exception
    {
        this.started = false;
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void set(String key, String value) throws Exception
    {
        embeddedCache.put(key, value);
    }

    @Override
    public String get(String key) throws Exception
    {
        return embeddedCache.get(key);
    }

    @Override
    public Set<String> keys(String pattern)
    {
        return embeddedCache.keySet();
    }

    @Override
    public Object getCache()
    {
        return embeddedCache;
    }

    @Override
    public String delete(String key) throws Exception
    {
        return embeddedCache.remove(key);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "EmbeddedCache[" + embeddedCache.size() + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
