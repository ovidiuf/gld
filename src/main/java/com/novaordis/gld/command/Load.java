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
import com.novaordis.gld.ContentType;
import com.novaordis.gld.KeyStore;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.MultiThreadedRunnerImpl;
import com.novaordis.gld.Service;
import com.novaordis.gld.StorageStrategy;
import com.novaordis.gld.strategy.load.LoadStrategyFactory;
import com.novaordis.gld.strategy.load.cache.WriteThenReadLoadStrategy;
import com.novaordis.gld.strategy.load.jms.SendLoadStrategy;
import com.novaordis.gld.strategy.storage.StorageStrategyFactory;

import java.util.List;

public class Load extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final LoadStrategy DEFAULT_CACHE_LOAD_STRATEGY = new WriteThenReadLoadStrategy();
    public static final LoadStrategy DEFAULT_JMS_LOAD_STRATEGY = new SendLoadStrategy();

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

        Service service = configuration.getService();

        if (service == null)
        {
            throw new IllegalStateException("null cache service");
        }

        if (!service.isStarted())
        {
            service.start();
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

        new MultiThreadedRunnerImpl(getConfiguration()).runConcurrently();
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

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
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
            if (ContentType.KEYVALUE.equals(getConfiguration().getContentType()))
            {
                loadStrategy = DEFAULT_CACHE_LOAD_STRATEGY;
            }
            else if (ContentType.MESSAGE.equals(getConfiguration().getContentType()))
            {
                loadStrategy = DEFAULT_JMS_LOAD_STRATEGY;
            }
            else
            {
                throw new IllegalStateException("we don't know the content type, we can't initialize the load strategy");
            }

            // give the default strategy a chance to pick up configuration from command line
            loadStrategy.configure(configuration, getArguments(), 0);
        }

        configuration.setLoadStrategy(loadStrategy);

        // at this point we should not have any unaccounted for arguments
        failOnUnknownArguments();
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
