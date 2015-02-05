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

import com.novaordis.gld.CacheService;
import com.novaordis.gld.Operation;
import com.novaordis.gld.Service;

public class Delete implements Operation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String key;

    private boolean successful;

    private boolean performed;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Delete(String key)
    {
        this.key = key;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.Operation#perform(com.novaordis.gld.Service)
     */
    @Override
    public void perform(Service cs) throws Exception
    {
        performed = true;

        String deleted = ((CacheService)cs).delete(key);

        if (deleted != null)
        {
            this.successful = true;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * A successful delete means we actually deleted something from the cache. An unsuccessful delete means
     * the key wasn't there to be deleted.
     */
    public boolean isSuccessful()
    {
        return successful;
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
        return key + (performed ? " (" + (successful ? "successfully deleted" : "not found in cache") + ")" : "");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
