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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/21/16
 */
public class Extensions implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String LITERAL = "extensions";

    private static final Logger log = LoggerFactory.getLogger(Extensions.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Package protected status ----------------------------------------------------------------------------------------

    /**
     * Scans the class path and extracts as much extension information as it can.
     *
     * @return a list of ExtensionInfo instances. May return an empty list, but never null.
     *
     * @exception IllegalArgumentException if it gets a null classpath
     */
    static List<ExtensionInfo> extractExtensionInfoFromClasspath(String classpath) {

        log.debug("extracting extension info from classpath " + classpath);

        if (classpath == null) {
            throw new IllegalArgumentException("null classpath");
        }

        List<ExtensionInfo> result = new ArrayList<>();

        //
        // identify the extension jars
        //

        //
        // infer the implementation class
        //

        //
        // instantiate it
        //

        //
        // read version
        //

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void execute() {

        //
        // read the classpath
        //

        String javaClassPathPropertyName = "java.class.path";
        String classPath = System.getProperty(javaClassPathPropertyName);

        if (classPath == null) {

            throw new IllegalStateException("could not find '" + javaClassPathPropertyName + "' property");
        }

        //
        // identify the extension jars
        //

        List<ExtensionInfo> extensionInfos = extractExtensionInfoFromClasspath(classPath);

        if (extensionInfos.isEmpty()) {

            System.out.println("no extensions installed");
        }
        else {
            for (ExtensionInfo ei : extensionInfos) {

                String s = ei.getExtensionName() + " " + ei.getExtensionVersion();
                System.out.println(s);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
