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

package com.novaordis.gld.service.cache.infinispan;

import com.novaordis.gld.service.cache.EmbeddedCache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/23/16
 */
public class EmbeddedRemoteCacheManager extends RemoteCacheManager {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private EmbeddedCache defaultCache;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedRemoteCacheManager() {

        //
        // we don't want any of the underlying default initialization to take place
        //
        super(false);

        defaultCache = new EmbeddedCache();
    }

    // Overrides -------------------------------------------------------------------------------------------------------


    @Override
    public <K, V> RemoteCache<K, V> getCache() {

        return defaultCache;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
