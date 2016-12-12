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

import io.novaordis.utilities.Files;

import java.io.File;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class LowLevelConfigurationImpl implements LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, Object> rawConfiguration;

    // verified to exist and be a directory
    private File configurationDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     *
     * @exception IllegalArgumentException if the configuration file directory is not null, it does not exist or
     * it is not a directory.
     */
    public LowLevelConfigurationImpl(Map<String, Object> raw, File configurationDirectory) {

        if (configurationDirectory == null) {

            throw new IllegalArgumentException("null configuration directory");
        }

        if (!configurationDirectory.exists()) {

            throw new IllegalArgumentException(
                    "configuration directory " + configurationDirectory.getPath() + " does not exist");
        }

        if (!configurationDirectory.isDirectory()) {

            throw new IllegalArgumentException(
                    "the path " + configurationDirectory.getPath() + " does not represent a directory");
        }

        this.configurationDirectory = configurationDirectory;
        this.rawConfiguration = raw;
    }

    // LowLevelConfiguration implementation ----------------------------------------------------------------------------

    @Override
    public <T> T get(Class<? extends T> type, String... path) {

        String pathAsString = "";

        Map<String, Object> current = rawConfiguration;

        for(int i = 0; i < path.length; i ++) {

            String element = path[i];

            pathAsString = pathAsString.isEmpty() ? element : pathAsString + "." + element;

            Object o = current.get(element);

            if (o == null) {

                return null;
            }

            if (i < path.length - 1) {

                //
                // we expect a map, if we get anything else, it means the path did not match, return null
                //

                if (!(o instanceof Map)) {

                    return null;
                }

                //noinspection unchecked
                current = (Map<String, Object>)o;
                continue;
            }

            //
            // at the end of the path
            //

            if (type.isAssignableFrom(o.getClass())) {

                //noinspection unchecked
                return (T) o;
            }

            throw new IllegalStateException(
                    "expected " + pathAsString + " to be a " + type.getSimpleName() + " but it is a(n) " +
                            o.getClass().getSimpleName());
        }

        return null;
    }

    @Override
    public File getFile(String... path) {

        // this throws IllegalArgumentException if we don't match a String
        String s = get(String.class, path);

        if (s == null) {

            return null;
        }

        if (s.startsWith(File.separator)) {

            //
            // absolute path, do not resolve
            //

            return new File(s);
        }

        //
        // resolve to configuration directory, which is guaranteed to be an existing directory
        //

        s = configurationDirectory.getPath() + "/" + s;
        s = Files.normalizePath(s);
        return new File(s);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
