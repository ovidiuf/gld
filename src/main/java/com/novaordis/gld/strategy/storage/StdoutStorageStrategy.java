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

package com.novaordis.gld.strategy.storage;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.StorageStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StdoutStorageStrategy implements StorageStrategy
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // StorageStrategy implementation ----------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.StorageStrategy#configure(Configuration, List, int)
     */
    @Override
    public void configure(Configuration conf, List arguments, int from) throws Exception
    {
        // nothing to do, noop
    }

    @Override
    public boolean isConfigured()
    {
        return true;
    }

    @Override
    public void start() throws Exception
    {
        // noop
    }

    @Override
    public void stop() throws Exception
    {
        // noop
    }

    @Override
    public boolean isStarted()
    {
        return true;
    }

    /**
     * @see com.novaordis.gld.StorageStrategy#store(String, String)
     */
    @Override
    public void store(String key, String value) throws Exception
    {
        System.out.println(key + "=" + value);
    }

    /**
     * @see com.novaordis.gld.StorageStrategy#retrieve(String)
     */
    @Override
    public String retrieve(String key) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws Exception
    {
        return Collections.emptySet();
    }

    @Override
    public boolean isRead()
    {
        return false;
    }

    @Override
    public boolean isWrite()
    {
        return true;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "StdoutStorageStrategy[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
