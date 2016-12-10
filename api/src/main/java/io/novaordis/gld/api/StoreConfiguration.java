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

package io.novaordis.gld.api;

import io.novaordis.gld.api.configuration.LowLevelConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * Typed access to the store configuration. More details about the store can be found here:
 *
 * @{linktourl https://kb.novaordis.com/index.php/Gld_Concepts#Store}
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public interface StoreConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String STORE_TYPE_LABEL = "type";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Typed Access ----------------------------------------------------------------------------------------------------

    /**
     * A well-known store type ("in-memory", "hierarchical", etc.) or a fully qualified class name that can be
     * instantiated by reflection.
     */
    String getStoreType() throws UserErrorException;

    // Untyped Access --------------------------------------------------------------------------------------------------

}
