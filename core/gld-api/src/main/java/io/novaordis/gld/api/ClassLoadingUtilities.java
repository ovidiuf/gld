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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoadingUtilities {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ClassLoadingUtilities.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Generic utility class that looks up classes on the classpath and tries to find
     * <package>.<nameRoot><suffix> that implements the given interfaceType and then instantiates it using a no-argument
     * constructor.
     *
     * @param interfaceType - the interface to be implemented by the returned class.
     * @param packageName - the package name (dot separated components) - must NOT end in dot.
     * @param nameRoot - the name root - capitalization matters.
     * @param suffix - the suffix - capitalization matters.
     * @param <T> - the interface to be implemented by the returned class.
     *
     * @exception IllegalArgumentException (with cause) when the class is not found or it cannot be instantiated.
     */
    public static <T> T getInstance(Class<T> interfaceType, String packageName, String nameRoot, String suffix)
        throws Exception {

        String fullyQualifiedClassName = packageName + "." + nameRoot + suffix;

        ClassLoader cl = ClassLoadingUtilities.class.getClassLoader();

        Class<T> c;

        try
        {
            //noinspection unchecked
            c = (Class<T>)cl.loadClass(fullyQualifiedClassName);
        }
        catch(Throwable t)
        {
            throw new IllegalArgumentException("cannot find class " + t.getMessage(), t);
        }

        T result;

        try
        {
            result = c.newInstance();
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("class '" + fullyQualifiedClassName + "' failed to instantiate, most likely the class has no no-argument constructor or a private no-argument constructor: " + e.getMessage(), e);
        }

        if (!interfaceType.isAssignableFrom(c))
        {
            throw new IllegalArgumentException(fullyQualifiedClassName + " does not implement " + interfaceType,
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
