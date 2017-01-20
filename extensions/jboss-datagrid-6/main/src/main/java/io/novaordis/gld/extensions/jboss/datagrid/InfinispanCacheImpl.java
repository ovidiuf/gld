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

import io.novaordis.gld.extensions.jboss.datagrid.common.InfinispanCache;
import org.infinispan.client.hotrod.RemoteCache;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class InfinispanCacheImpl implements InfinispanCache {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private RemoteCache delegate;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param delegate must be non-null
     */
    public InfinispanCacheImpl(RemoteCache delegate) {

        if (delegate == null) {

            throw new IllegalArgumentException("null remote cache");
        }

        this.delegate = delegate;
    }

    // InfinispanCache implementation ----------------------------------------------------------------------------------

    @Override
    public String getName() {

        return delegate.getName();
    }

    public String get(String key) throws Exception {

        return (String)delegate.get(key);
    }

    public void put(String key, String value) throws Exception {

        //noinspection unchecked
        delegate.put(key, value);
    }

    public void remove(String key) throws Exception {

        delegate.remove(key);
    }

    public Set<String> keys() throws Exception {

        //noinspection unchecked
        return delegate.keySet();
    }

    @Override
    public Object getDelegate() {

        return delegate;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
