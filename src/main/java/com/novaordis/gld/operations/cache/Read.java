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

package com.novaordis.gld.operations.cache;

import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.Operation;
import com.novaordis.gld.CacheService;
import com.novaordis.gld.Service;

public class Read implements Operation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String key;
    private String value;
    private volatile boolean performed;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Read(String key)
    {
        this.key = key;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see Operation#perform(com.novaordis.gld.Service)
     */
    @Override
    public void perform(Service s) throws Exception
    {
        performed = true;
        value = ((CacheService)s).get(key);
    }

    @Override
    public LoadStrategy getLoadStrategy()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null in case of cache miss.
     */
    public String getValue()
    {
        return value;
    }

    public String getKey()
    {
        return key;
    }

    public void setValue(String s)
    {
        this.value = s;
    }

    public boolean hasBeenPerformed()
    {
        return performed;
    }

    @Override
    public String toString()
    {
        return key + (!performed ? "" : (value == null ? " miss" : " hit (" + value + ")"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
