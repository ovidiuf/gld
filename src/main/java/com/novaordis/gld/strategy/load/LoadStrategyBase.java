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

package com.novaordis.gld.strategy.load;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.KeyStore;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.Operation;

import java.util.List;
import java.util.Set;

public abstract class LoadStrategyBase implements LoadStrategy
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;
    private KeyStore keyStore;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.LoadStrategy#configure(Configuration, List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        if (configuration == null)
        {
            throw new IllegalArgumentException("null configuration");
        }

        this.configuration = configuration;

        if (arguments == null)
        {
            // null arguments list means there are no more arguments
            return;
        }

        if (!arguments.isEmpty() && (from < 0 || from >= arguments.size()))
        {
            throw new ArrayIndexOutOfBoundsException("invalid array index: " + from);
        }

    }

    @Override
    public String getName()
    {
        String s = getClass().getSimpleName();

        if (s.endsWith("LoadStrategy"))
        {
            s = s.substring(0, s.length() - "LoadStrategy".length());
        }

        return s;
    }

    @Override
    public KeyStore getKeyStore()
    {
        return keyStore;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        throw new RuntimeException("getOperationTypes() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Configuration getConfiguration()
    {
        return configuration;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setKeyStore(KeyStore keyStore)
    {
        this.keyStore = keyStore;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
