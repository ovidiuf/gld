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

package com.novaordis.gld.service.cache;

import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/23/16
 */
public abstract class CacheServiceBase implements CacheService {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;

    // Constructors ----------------------------------------------------------------------------------------------------

    // CacheService implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException {

        this.name = Util.extractString("--cache", commandLineArguments, 0);
    }

    @Override
    public String getName() {
        return name;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @param name - null is acceptable, it means "default cache"
     */
    public void setName(String name)
    {
        this.name = name;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
