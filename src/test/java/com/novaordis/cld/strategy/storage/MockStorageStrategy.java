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

package com.novaordis.cld.strategy.storage;

import com.novaordis.cld.Configuration;

import java.util.List;
import java.util.Set;

/**
 * We keep this class in this package ("com.novaordis.cld.strategy.storage") and not in the mock package
 * com.novaordis.cld.mock because, among other things, we test reflection-based instantiation, and for that we need
 * to be in certain packages.
 */
public class MockStorageStrategy extends StorageStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // introduced by "--mock-argument"
    private String mockArgument;

    // introduced by "--mock-storage-argument"
    private String mockStorageArgument;

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // StorageStrategy implementation ----------------------------------------------------------------------------------

    @Override
    public void configure(Configuration config, List<String> arguments, int from) throws Exception
    {
        super.configure(config, arguments, from);

        for(int i = from; i < arguments.size(); i ++)
        {
            if ("--mock-argument".equals(arguments.get(i)))
            {
                arguments.remove(i);
                mockArgument = arguments.remove(i --);
            }
            else if ("--mock-storage-argument".equals(arguments.get(i)))
            {
                arguments.remove(i);
                mockStorageArgument = arguments.remove(i --);
            }
        }
    }

    @Override
    public boolean isConfigured()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void store(String key, String value) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public String retrieve(String key) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception
    {
        started = true;
    }

    @Override
    public void stop() throws Exception
    {
        started = false;
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }


    // Public ----------------------------------------------------------------------------------------------------------

    public String getMockArgument()
    {
        return mockArgument;
    }

    public String getMockStorageArgument()
    {
        return mockStorageArgument;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
