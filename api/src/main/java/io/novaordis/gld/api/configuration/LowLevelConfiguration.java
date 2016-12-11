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

import java.io.File;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public interface LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Walks the internal storage following the specified path and returns the value found in the map entry that
     * matches the path. If there is no path match, returns null. If the value is not null and does not match the
     * requested type, the method throws IllegalStateException.
     *
     * @exception IllegalStateException if the value is not null and does not match the requested type.
     */
    <T> T get(Class<? extends T> type, String ... path);

    /**
     * Attempt to map the given path onto the low level configuration map managed by this instance and return the
     * value found on match as a File instance. If the value corresponds to an absolute file path, return it as such.
     * If the value corresponds to a relative file path, resolve it to an absolute path using the <b>location of the
     * configuration file</b> as root directory. Note that the method does not attempt to check whether the underlying
     * file or directory exists.
     *
     * If there is no path match, returns null.
     *
     * If the stored value value is not null and it is not a String, the method throws IllegalStateException.
     *
     * @exception IllegalStateException if the there is a path match, but the underlying value is not a String (so
     * it cannot be converted to a File path)
     */
    File getFile(String ... path);

}
