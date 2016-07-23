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
import com.novaordis.gld.service.cache.CacheService;
import com.novaordis.gld.Service;

public class Write implements Operation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String key;
    private String value;

    private boolean successful;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Write(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see Operation#perform(com.novaordis.gld.Service)
     */
    @Override
    public void perform(Service s) throws Exception
    {
        ((CacheService)s).set(key, value);
        this.successful = true;
    }

    @Override
    public LoadStrategy getLoadStrategy()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public boolean isSuccessful()
    {
        return successful;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * May return null if the instance was not initialized.
     */
    public String getKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return key + "=" + value;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
