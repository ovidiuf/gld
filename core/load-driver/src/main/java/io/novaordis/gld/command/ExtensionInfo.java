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

package io.novaordis.gld.command;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/21/16
 */
public class ExtensionInfo {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String extensionName;
    private String extensionVersion;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @exception IllegalArgumentException on null extension name
     */
    public ExtensionInfo(String extensionName) {

        if (extensionName == null) {

            throw new IllegalArgumentException("null extension name");
        }

        this.extensionName = extensionName;
        this.extensionVersion = null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Never return null.
     */
    public String getExtensionName() {

        return extensionName;
    }

    public String getExtensionVersion() {

        return extensionVersion;
    }

    public void setExtensionVersion(String s) {

        this.extensionVersion = s;
    }

    @Override
    public String toString() {

        return getExtensionName();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
