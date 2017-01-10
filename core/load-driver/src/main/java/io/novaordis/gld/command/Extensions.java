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

import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Exception;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
     * Scans the class path and extracts the extension names. It wraps those names in ExtensionInfo wrappers, but
     * aside from name, all ExtensionInfo wrapper state is null.
     *
     * @return a list of ExtensionInfo instances. May return an empty list, but never null.
     *
     * @exception IllegalArgumentException if it gets a null classpath
     */
    static Set<ExtensionInfo> extractExtensionNamesFromClasspath(String classpath) {

        log.debug("extracting extension info from classpath " + classpath);

        if (classpath == null) {
            throw new IllegalArgumentException("null classpath");
        }

        // ExtensionInfo are keyed by their name
        Map<String, ExtensionInfo> extensionInfos = new HashMap<>();

        //
        // identify the extension jars TODO does not work on windows, correct.
        //
        StringTokenizer st = new StringTokenizer(classpath, ":");
        while(st.hasMoreTokens()) {

            String cpElement = st.nextToken();

            String markerFragment = "/extensions/";

            int i = cpElement.indexOf(markerFragment);

            //
            // this is how the class path fragment looks like:
            // "/Users/ovidiu/runtime/gld/bin/../extensions/jboss-datagrid-7/jboss-datagrid-7-1.0.0-SNAPSHOT-5.jar"
            //

            if (i == -1) {

                continue;
            }

            String s = cpElement.substring(i + markerFragment.length());
            String extensionName = s.substring(0, s.indexOf('/'));

            ExtensionInfo ei = extensionInfos.get(extensionName);

            if (ei == null) {

                ei = new ExtensionInfo(extensionName);
                extensionInfos.put(extensionName, ei);
            }

        }

        return new HashSet<>(extensionInfos.values());
    }

    /**
     * If we encounter difficulties in instantiating the class and getting the version info, we'll log in debug mode
     * but not throw exception. Will return null instead.
     */
    static String inferExtensionVersion(String extensionName) {

        try {

            String className = ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);

            Class c;

            try {

                c = Class.forName(className);
            }
            catch (Exception e) {

                log.debug("no such class " + className);
                return null;
            }

            Object instance;

            try {

                instance = c.newInstance();
            }
            catch (Exception e) {

                log.debug("failed to instantiate class " + c + " using a no-argument constructor");
                return null;
            }

            Service service;

            try {

                service = (Service)instance;
            }
            catch (Exception e) {

                log.debug(c + " is not a Service implementation");
                return null;
            }

            String version = service.getVersion();
            log.debug("extension version: " + version);
            return version;
        }
        catch(Throwable t) {

            log.debug("failed to infer version for extension '" + extensionName + "'", t);
            return null;
        }
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

        Set<ExtensionInfo> extensionInfos = extractExtensionNamesFromClasspath(classPath);

        //
        // at this point the ExtensionInfo instances contain only the extension names
        //

        if (extensionInfos.isEmpty()) {

            System.out.println("no extensions installed");
            return;
        }

        //
        // attempt to infer the service class name, instantiate it and read the version; a failure will be logged
        // but will not interrupt the process
        //

        for(ExtensionInfo ei: extensionInfos) {

            String extensionVersion = inferExtensionVersion(ei.getExtensionName());
            ei.setExtensionVersion(extensionVersion);
        }

        //
        // report
        //

        for (ExtensionInfo ei : extensionInfos) {

            String s = ei.getExtensionName() + " " + ei.getExtensionVersion();
            System.out.println(s);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
