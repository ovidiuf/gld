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

package com.novaordis.cld.strategy.load;

import com.novaordis.cld.CacheService;
import com.novaordis.cld.Configuration;
import com.novaordis.cld.KeyStore;
import com.novaordis.cld.Operation;
import com.novaordis.cld.keystore.SetKeyStore;
import com.novaordis.cld.operations.Delete;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A load strategy that generate delete operations based on keys read from an external storage.
 */
public class DeleteLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    /**
     * Just one key to limit the damage in case this load strategy is used improperly.
     */
    public static final int DEFAULT_KEY_COUNT = 1;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int keyCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public DeleteLoadStrategy(int keyCount)
    {
        super();
        this.keyCount = keyCount;
    }

    public DeleteLoadStrategy()
    {
        this(DEFAULT_KEY_COUNT);
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);

        // we assume cache installed and running

        CacheService cs = configuration.getCacheService();

        if (!cs.isStarted())
        {
            throw new IllegalStateException("cache service " + cs + " not started");
        }

        // TODO this is a potentially costly operation

        Set<String> keys = cs.keys(null);

        Set<String> keysToDelete = new HashSet<>();
        int counter = 0;

        for(String k: keys)
        {
            keysToDelete.add(k);

            if (++counter == keyCount)
            {
                break;
            }
        }

        KeyStore ks = new SetKeyStore(keysToDelete);
        setKeyStore(ks);
    }

    /**
     * @see com.novaordis.cld.LoadStrategy#next(Operation, String)
     */
    @Override
    public Operation next(Operation lastOperation, String lastWrittenKey)
    {
        KeyStore keyStore = getKeyStore();

        if (keyStore == null)
        {
            throw new IllegalStateException(this + " not configured");
        }

        String key = keyStore.get();

        if (key == null)
        {
            return null;
        }

        return new Delete(key);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getKeyCount()
    {
        return keyCount;
    }

    @Override
    public String toString()
    {
        return "Delete[count=" + keyCount + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
