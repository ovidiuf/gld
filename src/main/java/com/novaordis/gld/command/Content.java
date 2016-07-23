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

package com.novaordis.gld.command;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.service.cache.CacheService;
import com.novaordis.gld.StorageStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.strategy.storage.StdoutStorageStrategy;
import com.novaordis.gld.strategy.storage.StorageStrategyFactory;

import java.util.List;
import java.util.Set;

public class Content extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean keyCountOnly;

    private CacheService cacheService;

    private StorageStrategy storageStrategy;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Content(Configuration c)
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception
    {
        processArguments();

        Configuration c = getConfiguration();
        cacheService = (CacheService)c.getService();

        if (cacheService == null)
        {
            throw new IllegalStateException("null cache service");
        }

        if (!cacheService.isStarted())
        {
            cacheService.start();
        }

        if (storageStrategy == null)
        {
            storageStrategy = new StdoutStorageStrategy();
        }

        storageStrategy.start();
    }

    @Override
    public boolean isInitialized()
    {
        return storageStrategy != null;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();

        try
        {
            System.out.println("reading keys ...");
            Set<String> keys = cacheService.keys(null);

            if (keyCountOnly)
            {
                System.out.println(keys.size() + " keys");
                return;
            }

            System.out.println(keys.size() + " keys read");

            int keyCounter = 0;

            for(String k: keys)
            {
                String v = cacheService.get(k);

                storageStrategy.store(k, v);

                keyCounter ++;

                if (keyCounter % 1000 == 0)
                {
                    System.out.println(keyCounter + " entries read");
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new UserErrorException("failed to retrieve cache content", e);
        }
        finally
        {
            storageStrategy.stop();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setKeyCountOnly(boolean b)
    {
        this.keyCountOnly = b;
    }

    public boolean isKeyCountOnly()
    {
        return keyCountOnly;
    }

    public StorageStrategy getStorageStrategy()
    {
        return storageStrategy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processArguments() throws Exception
    {
        List<String> arguments = getArguments();

        //noinspection ForLoopReplaceableByForEach
        for(int i = 0; i < arguments.size(); i ++)
        {
            String crt = arguments.get(i);

            if ("--key-count-only".equals(crt))
            {
                setKeyCountOnly(true);
            }

            if ("--storage-strategy".equals(crt))
            {
                storageStrategy = StorageStrategyFactory.fromArguments(getConfiguration(), arguments, i);
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
