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

import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.cache.embedded.EmbeddedCacheService;
import io.novaordis.utilities.UserErrorException;

import java.util.StringTokenizer;

/**
 * Typed and untyped access to an implementation configuration.
 *
 * The untyped access is offered to allow for flexibility in processing implementation-specific configuration.
 *
 * The constructor must insure that the configuration constraints listed below are enforced, or fail with
 * UserErrorException otherwise:
 *
 * 1. Either 'name' or 'class' are present, but not both.
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
    String EXTENSION_CLASS_LABEL = "class";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Applies heuristics in an attempt to infer the fully qualified class name of the service implementation from
     * the extension name.
     *
     * The common pattern is to use the "io.novaordis.gld.extensions" package root, and then subsequently
     * add sub-packages based on name. Version information, if any, is appended to the name of the class.
     *
     * Example: jboss-datagrid-7 generates "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid7Service"
     */
    static String extensionNameToExtensionServiceFullyQualifiedClassName(
            String extensionName, ServiceType serviceType) throws UserErrorException {

        if ("embedded".equals(extensionName)) {

            if (ServiceType.cache.equals(serviceType)) {

                return EmbeddedCacheService.class.getName();
            }

            throw new RuntimeException("NOT YET IMPLEMENTED " + serviceType);
        }

        String packageName = "io.novaordis.gld.extensions";
        String className = "";

        StringTokenizer st = new StringTokenizer(extensionName, "-");

        while(st.hasMoreTokens()) {

            String token = st.nextToken();
            char firstChar = token.charAt(0);

            if ('0' <= firstChar && firstChar <= '9') {

                if (className.isEmpty() || st.hasMoreTokens()) {

                    //
                    // this is the first token, and a class name cannot start with a number, or is not the last token,
                    // which can be interpreted as version, hence start with a number
                    //

                    throw new UserErrorException(
                            "invalid extension name '" + extensionName +
                                    "', extension name component starts with a number");
                }

                //
                // last component, if contains numbers, do not use it as package name component, but use it
                // in the class name, after removing the dots
                //

                token = token.replace(".", "");
                className += token;
                break;
            }

            packageName += "." + token;

            //
            // capitalization exceptions
            //

            if ("jboss".equals(token.toLowerCase())) {

                token = "JBoss";
            }

            className += Character.toUpperCase(firstChar) + token.substring(1);
        }

        className += "Service";

        return packageName + "." + className;
    }

    /**
     * The name of the extension to use to instantiate the service implementation.
     *
     * @{linkToUrl https://kb.novaordis.com/index.php/Gld_Concepts#Extension_Name}
     *
     * May return null if the implementation is specified by its class.
     *
     * @exception UserErrorException if the value is not a string.
     */
    String getExtensionName() throws UserErrorException;

    /**
     * The fully qualified class name to be used to instantiate the extension service implementation.
     *
     * @{linkToUrl https://kb.novaordis.com/index.php/Gld_Concepts#Extension_Name}
     *
     * May return null if the implementation is specified by its class.
     *
     * @exception UserErrorException if the value is not a string.
     */
    String getExtensionClass() throws UserErrorException;


    // Public ----------------------------------------------------------------------------------------------------------

}
