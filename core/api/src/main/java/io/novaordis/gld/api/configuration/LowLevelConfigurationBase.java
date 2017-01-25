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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class LowLevelConfigurationBase implements LowLevelConfiguration {

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
    public LowLevelConfigurationBase(Map<String, Object> raw, File configurationDirectory) {

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

        return getAndPossiblyRemove(type, false, path);
    }

    @Override
    public <T> T remove(Class<? extends T> type, String... path) {

        return getAndPossiblyRemove(type, true, path);
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

    @Override
    public Map<String, Object> get(String... path) {

        //noinspection unchecked
        Map<String, Object> m = get(Map.class, path);

        if (m == null) {

            //
            // special case when we call it with a no arguments, it means we want the underlying raw configuration
            // map
            //

            if (path.length == 0) {

                return rawConfiguration;
            }

            return Collections.emptyMap();
        }

        return m;
    }

    /**
     * TODO next time I need a list of a specific type, implement
     * List<T> getList(Class<? extends T> type, String... path)
     */
    @Override
    public List<Object> getList(String... path) {

        //noinspection unchecked
        List<Object> list = get(List.class, path);

        if (list != null) {

            return list;
        }

        return Collections.emptyList();
    }

    @Override
    public File getConfigurationDirectory() {

        return configurationDirectory;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @see LowLevelConfiguration#get(Class, String...)
     * @see LowLevelConfiguration#remove(Class, String...)
     */
    <T> T getAndPossiblyRemove(Class<? extends T> type, boolean remove, String... path) {

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

                if (remove) {

                    current.remove(element);
                }

                //noinspection unchecked
                return (T) o;
            }

            throw new IllegalStateException(
                    "expected " + pathAsString + " to be a " + type.getSimpleName() + " but it is a(n) " +
                            o.getClass().getSimpleName() + ": \"" + o + "\"");
        }

        return null;
    }


    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * Replaces the underlying instance at the specified position, if the path matches. If there is not a full patch
     * match, and all the parents aren't Maps, the method throws IllegalArgumentException (no new parent maps are
     * created). However, we will create the map corresponding to the last token in path, if the last token in path
     * is not known.
     *
     * If we attempt to set a Map, this has a special significance, it means we want to replace the underlying raw
     * configuration sub-map.
     *
     * If we attempt to set to null, it means "remove" and it is semantically equivalent with remove(String ...path).
     */
    protected void set(Object instance, String ... path) {

        if (path.length == 0) {

            if (instance == null) {

                throw new IllegalArgumentException("invalid attempt to remove the root storage");
            }

            if (!(instance instanceof Map)) {

                throw new IllegalArgumentException(
                        "invalid attempt to replace the root map with a " +instance.getClass().getSimpleName());
            }

            //noinspection unchecked
            rawConfiguration = (Map<String, Object>)instance;
        }

        Map<String, Object> crtMap = rawConfiguration;
        String pathAsString = "";

        for(int i = 0; i < path.length; i ++) {

            String crtToken = path[i];

            pathAsString = pathAsString.isEmpty() ? crtToken : pathAsString + "." + crtToken;

            Object crtInstance = crtMap.get(crtToken);

            if (i < path.length - 1) {

                //
                // intermediate node, must be Map<String, Object>
                //

                if (!(crtInstance instanceof Map)) {

                    throw new IllegalArgumentException(pathAsString + " does not match a map");
                }

                //noinspection unchecked
                crtMap = (Map<String, Object>) crtInstance;

                continue;
            }

            //
            // the last token in the path
            //

            Object storedInstance = crtMap.get(crtToken);

            if (instance instanceof Map) {

                if (storedInstance != null && !(storedInstance instanceof Map)) {

                    throw new IllegalArgumentException(
                            "illegal replacement of the " + pathAsString + " non-map with a map");
                }

                //
                // map for map replacement, if the stored map is null, we'll extend the storage, but in this case
                // the instance has to be a Map<String, Object> otherwise we'll get an exception
                //

                crtMap.put(crtToken, instance);

            }
            else {

                //
                // if the replacement is null, that means "remove", and we do remove a maps and non-maps
                //

                if (instance == null) {

                    crtMap.remove(crtToken);
                }
                else {

                    //
                    // the replacement is non-null
                    //

                    if (storedInstance  != null && storedInstance instanceof Map) {

                        throw new IllegalArgumentException(
                                "illegal replacement of the " + pathAsString + " map with a non-map");
                    }

                    //
                    // non-map for non-map replacement
                    //

                    crtMap.put(crtToken, instance);
                }
            }
        }
    }

    /**
     * Removes the key/value pair associated with the given path (if any).
     */
    protected void remove(String ... path) {

        set(null, path);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
