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

package io.novaordis.gld.driver.keystore;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.store.HierarchicalStorageStrategy;

import java.util.Iterator;

/**
 * Experimental - interacting with a HierarchicalStorageStrategy. Must refactor.

 */
@Deprecated
public class ExperimentalKeyStore implements KeyStore
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private HierarchicalStorageStrategy hss;
    private Iterator<String> keyIterator;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ExperimentalKeyStore(HierarchicalStorageStrategy hss) throws Exception
    {
        this.hss = hss;
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void store(String key) throws Exception
    {
        throw new IllegalStateException("this is a read-only keystore, cannot store");
    }

    @Override
    public long getKeyCount() {
        throw new RuntimeException("getKeyCount() NOT YET IMPLEMENTED");
    }

    //    @Override
    public synchronized String get()
    {
        if (keyIterator.hasNext())
        {
            return keyIterator.next();
        }

        return null;
    }

    @Override
    public void start() throws Exception
    {
        // read all the keys from storage
        keyIterator = hss.getKeys().iterator();
    }

    @Override
    public void stop() throws Exception
    {
        keyIterator = null;
    }

    @Override
    public boolean isStarted()
    {
        return keyIterator != null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getValue(String key) throws Exception
    {
        return hss.retrieve(key);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
