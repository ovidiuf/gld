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

import com.novaordis.gld.CacheService;
import com.novaordis.gld.Configuration;
import com.novaordis.gld.KeyStore;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.MultiThreadedRunner;
import com.novaordis.gld.StorageStrategy;
import com.novaordis.gld.strategy.load.LoadStrategyFactory;
import com.novaordis.gld.strategy.load.WriteThenReadLoadStrategy;
import com.novaordis.gld.strategy.storage.StorageStrategyFactory;

import java.util.List;

public class Load extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final LoadStrategy DEFAULT_LOAD_STRATEGY = new WriteThenReadLoadStrategy();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;
    private LoadStrategy loadStrategy;
    private StorageStrategy storageStrategy;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Load(Configuration c)
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception
    {
        configuration = getConfiguration();

        processArguments();

        CacheService cacheService = configuration.getCacheService();

        if (cacheService == null)
        {
            throw new IllegalStateException("null cache service");
        }

        if (!cacheService.isStarted())
        {
            cacheService.start();
        }

        if (storageStrategy != null)
        {
            storageStrategy.start();
        }

        KeyStore keyStore = loadStrategy.getKeyStore();

        if (keyStore != null)
        {
            keyStore.start();
        }
    }

    @Override
    public boolean isInitialized()
    {
        return getConfiguration().getLoadStrategy() != null;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();

        new MultiThreadedRunner(getConfiguration()).runConcurrently();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String toString()
    {
        return "Load[" + (getLoadStrategy() == null ? null : getLoadStrategy().getName()) + "]";
    }

    public LoadStrategy getLoadStrategy()
    {
        return loadStrategy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void processArguments() throws Exception
    {
        List<String> arguments = getArguments();

        for(int i = 0; i < arguments.size(); i ++)
        {
            String crt = arguments.get(i);

            // "--strategy" is deprecated, use "--load-strategy" instead
            if ("--load-strategy".equals(crt) || "--strategy".equals(crt))
            {
                loadStrategy = LoadStrategyFactory.fromArguments(configuration, arguments, i --);
            }
            else if ("--storage-strategy".equals(crt))
            {
                storageStrategy = StorageStrategyFactory.fromArguments(configuration, arguments, i --);
                configuration.setStorageStrategy(storageStrategy);
            }
        }

        if (loadStrategy == null)
        {
            loadStrategy = DEFAULT_LOAD_STRATEGY;
            // give the default strategy a chance to pick up configuration from command line
            loadStrategy.configure(configuration, getArguments(), 0);
        }

        configuration.setLoadStrategy(loadStrategy);

        // at this point we should not have any unaccounted for arguments
        failOnUnknownArguments();
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
