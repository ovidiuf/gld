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

package io.novaordis.gld.extensions.jboss.datagrid;


import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.utilities.version.VersionUtilities;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class JBossDatagrid7Service extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    //
    // will be filtered by Maven and replaced with the current version
    //
    public static final String VERSION = "${project.version}";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    public String getVersion() {

        return VERSION;
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    public String get(String key) throws Exception {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    public void put(String key, String value) throws Exception {
        throw new RuntimeException("put() NOT YET IMPLEMENTED");
    }

    public void remove(String key) throws Exception {
        throw new RuntimeException("remove() NOT YET IMPLEMENTED");
    }

    public Set<String> keys() throws Exception {
        throw new RuntimeException("keys() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
