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

package io.novaordis.gld.api.configuration;

import io.novaordis.utilities.UserErrorException;

/**
 * Typed and untyped access to an implementation configuration.
 *
 * The untyped access is offered to allow for flexibility in processing implementation-specific configuration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/15/16
 */
public interface ImplementationConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    //
    // extension name, see https://kb.novaordis.com/index.php/Gld_Concepts#Extension_Name
    //
    String EXTENSION_NAME_LABEL = "name";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * The name of the extension to use to instantiate the service implementation.
     *
     * @{linkToUrl https://kb.novaordis.com/index.php/Gld_Concepts#Extension_Name}
     *
     * @exception UserErrorException if the extension is missing.
     */
    public String getExtensionName() throws UserErrorException;

    // Public ----------------------------------------------------------------------------------------------------------

}
