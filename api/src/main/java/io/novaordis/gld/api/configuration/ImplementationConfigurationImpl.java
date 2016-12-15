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

import java.io.File;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class ImplementationConfigurationImpl extends LowLevelConfigurationBase
        implements ImplementationConfiguration, LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param rawMap the raw map as extracted from the YAML file from the section corresponding to this type of
     *            configuration.
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     */
    public ImplementationConfigurationImpl(Map<String, Object> rawMap, File configurationDirectory)
            throws UserErrorException {

        super(rawMap, configurationDirectory);

        //
        // consistency checks
        //

        nameAndClassPreconditions();
    }

    // ImplementationConfiguration implementation ----------------------------------------------------------------------

    @Override
    public String getExtensionName() throws UserErrorException {

        String name;

        try {

            name = get(String.class, EXTENSION_NAME_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException(e);
        }

        if (name != null) {

            return name;
        }

        //
        // make sure that at least class is available
        //

        String c = get(String.class, EXTENSION_CLASS_LABEL);

        if (c == null) {

            throw new UserErrorException("neither implementation name or class are present");
        }

        return null;
    }

    @Override
    public String getExtensionClass() throws UserErrorException {

        String c;

        try {

            c = get(String.class, EXTENSION_CLASS_LABEL);
        }
        catch(IllegalStateException e) {

            throw new UserErrorException(e);
        }

        if (c != null) {

            return c;
        }

        //
        // make sure that at least name is available
        //

        String n = get(String.class, EXTENSION_NAME_LABEL);

        if (n == null) {

            throw new UserErrorException("neither implementation name or class are present");
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void nameAndClassPreconditions() throws UserErrorException {

        Object name = get(Object.class, EXTENSION_NAME_LABEL);
        Object c = get(Object.class, EXTENSION_CLASS_LABEL);

        if (name != null && c != null) {

            throw new UserErrorException("mutually exclusive implementation name and class are both present");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
