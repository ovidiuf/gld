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

import java.util.Random;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class Util {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static Throwable getRoot(Throwable t) {

        if (t == null) {

            throw new IllegalArgumentException("null throwable");
        }

        Throwable root = t;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        return root;
    }

    /**
     * If the string is longer than charCount, it displays the first characters in a log friendly format.
     */
    public static String firstCharacters(String s, int charCount) {

        if (s == null) {

            return null;
        }

        if (s.length() <= charCount) {

            return s;
        }

        return s.substring(0, charCount) + "...";
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private Util() {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
