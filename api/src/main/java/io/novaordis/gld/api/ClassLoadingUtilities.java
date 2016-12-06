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

import io.novaordis.utilities.UserErrorException;

public class ClassLoadingUtilities {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Generic static utility that looks specific interface implementations on the classpath and tries to find those
     * that match the name. If found, they are instantiated using a no-argument constructor.
     *
     * @param interfaceType - the interface to be implemented by the returned class.
     * @param fullyQualifiedClassName - the fully qualified class name
     * @param <T> - the interface to be implemented by the returned class.
     *
     * @exception io.novaordis.utilities.UserErrorException with a human readable message and embedded cause.
     */
    public static <T> T getInstance(Class<T> interfaceType, String fullyQualifiedClassName) throws Exception {

        Class<T> c;

        try {

            //noinspection unchecked
            c = (Class<T>)Class.forName(fullyQualifiedClassName);
        }
        catch(Throwable t) {

            throw new UserErrorException("class " + fullyQualifiedClassName + " not found", t);
        }

        T result;

        try {

            result = c.newInstance();
        }
        catch(Throwable t) {

            throw new UserErrorException(
                    "class '" + fullyQualifiedClassName +
                            "' failed to instantiate, most likely the class has no no-argument constructor or a private no-argument constructor: " +
                            t.getMessage(), t);
        }

        if (!interfaceType.isAssignableFrom(c)) {

            throw new UserErrorException(fullyQualifiedClassName + " does not implement " + interfaceType,
                new ClassCastException(interfaceType.getName()));
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
