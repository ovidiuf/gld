/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.cache.operation;

import io.novaordis.gld.api.OperationBase;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.cache.CacheService;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public abstract class CacheOperationBase extends OperationBase implements CacheOperation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String value;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected CacheOperationBase(String key) {

        super(key);
    }

    // CacheOperation implementation -----------------------------------------------------------------------------------

    @Override
    public String getValue() {

        return value;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setValue(String s) {

        this.value = s;
    }

    protected CacheService insureCacheService(Service s) {

        if (!(s instanceof CacheService)) {

            throw new IllegalArgumentException(s + " not a CacheService");
        }

        return (CacheService)s;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
