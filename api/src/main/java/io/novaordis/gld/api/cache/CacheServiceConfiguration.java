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

package io.novaordis.gld.api.cache;

import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public interface CacheServiceConfiguration extends ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String KEY_SIZE_LABEL = "key-size";
    String VALUE_SIZE_LABEL = "value-size";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the default key size, in bytes. A specific load strategy may override this default value with
     * load-strategy specific defaults or variable values.
     */
    int getKeySize() throws UserErrorException;

    /**
     * @return the default value size, in bytes. A specific load strategy may override this default value with
     * load-strategy specific defaults or variable values.
     */
    int getValueSize() throws UserErrorException;

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
