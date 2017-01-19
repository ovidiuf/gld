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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public interface InfinispanCache {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    String getName();

    String get(String key) throws Exception;

    void put(String key, String value) throws Exception;

    void remove(String key) throws Exception;

    Set<String> keys() throws Exception;

    /**
     * Give de-typed access to the underlying delegate. Usually a RemoteCache. We don't type the access to avoid
     * declaring a dependency on client libraries. The implementation modules, which have access to the actual types,
     * will cast.
     */
    Object getDelegate();

}
