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

package io.novaordis.gld.api.cache.operation;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.cache.CacheService;

public class Write extends CacheOperationBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Write(String key, String value) {

        super(key);
        setValue(value);
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see Operation#perform(Service)
     */
    @Override
    public void perform(Service s) throws Exception {

        CacheService cs = insureCacheService(s);

        String key = getKey();
        String value = getValue();

        try {

            setPerformed(true);

            cs.put(key, value);

            setSuccessful(true);
        }
        catch(Throwable t) {

            throw new RuntimeException("NOT YET IMPLEMENTED: we did not decide yet how to handle service failures");
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return getKey() + "=" + getValue();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
