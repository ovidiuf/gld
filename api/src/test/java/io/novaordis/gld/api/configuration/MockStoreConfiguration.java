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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class MockStoreConfiguration implements StoreConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String storeType;

    private Map<String, Object> pathContent;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockStoreConfiguration() {

        this.pathContent = new HashMap<>();
    }

    // StoreConfiguration ----------------------------------------------------------------------------------------------

    @Override
    public String getStoreType() throws UserErrorException {

        return storeType;
    }

    @Override
    public <T> T get(Class<? extends T> type, String... path) {

        String s = toDotSeparatedString(path);

        //noinspection unchecked
        return (T)pathContent.get(s);
    }

    @Override
    public File getFile(String... path) {

        return get(File.class, path);
    }

    @Override
    public Map<String, Object> get(String... path) {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public File getConfigurationDirectory() {
        throw new RuntimeException("getConfigurationDirectory() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setStoreType(String s) {

        this.storeType = s;
    }

    /**
     * Works both for Files and other objects.
     *
     * To set "configuration" that would be returned with getFile(), use:
     *
     *  setPath(..., new File(...))
     */
    public void setPath(String dotSeparatedPath, Object o) {

        pathContent.put(dotSeparatedPath, o);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private String toDotSeparatedString(String ... path) {

        String s = "";

        for (String aPath : path) {

            s += s.isEmpty() ? aPath : s + "." + aPath;
        }

        return s;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
