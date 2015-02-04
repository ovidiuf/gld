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

package com.novaordis.gld.service;

import com.novaordis.gld.CacheService;
import com.novaordis.gld.Util;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EmbeddedCacheService implements CacheService
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> embeddedCache;
    private boolean started;

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
