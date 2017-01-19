/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class JBossDatagrid6Service extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // CacheServiceBase overrides --------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {
        throw new NotYetImplementedException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public String get(String key) throws Exception {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public void put(String key, String value) throws Exception {
        throw new NotYetImplementedException("put() NOT YET IMPLEMENTED");
    }

    @Override
    public void remove(String key) throws Exception {
        throw new NotYetImplementedException("remove() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> keys() throws Exception {
        throw new NotYetImplementedException("keys() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
