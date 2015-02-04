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
import java.util.List;

abstract class StorageStrategyBase implements StorageStrategy
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;
    private boolean canRead;
    private boolean canWrite;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected StorageStrategyBase()
    {
        this.canRead = true;
        this.canWrite = true;
    }

    // StorageStrategy implementation ----------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.StorageStrategy#configure(Configuration, List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        if (configuration == null)
        {
            throw new IllegalArgumentException("null configuration");
        }

        if (arguments == null)
        {
            throw new IllegalArgumentException("null argument list");
        }

        if (!arguments.isEmpty() && (from < 0 || from >= arguments.size()))
        {
            throw new ArrayIndexOutOfBoundsException("invalid array index: " + from);
        }

        this.configuration = configuration;
    }

    @Override
    public boolean isRead()
    {
        return canRead;
    }

    @Override
    public boolean isWrite()
    {
        return canWrite;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setRead(boolean b)
    {
        this.canRead = b;
    }

    public void setWrite(boolean b)
    {
        this.canWrite = b;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected Configuration getConfiguration()
    {
        return configuration;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------


}
