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

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.cache.embedded.EmbeddedCacheService;
import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class ServiceFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceFactory.class);

    public static final String EMBEDDED_MARKER = "embedded";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Applies heuristics in an attempt to infer the fully qualified class name of the service implementation from
     * the extension name.
     *
     * The common pattern is to use the "io.novaordis.gld.extensions" package root, and then subsequently
     * add sub-packages based on name. Version information, if any, is appended to the name of the class.
     *
     * Several service name ("embedded-cache", "embedded-jms", and in general "embedded-<service-type>") are special
     * in that they shortcut the entire mechanism and return well-defined embedded services.
     *
     * Example: jboss-datagrid-7 generates "io.novaordis.gld.extensions.jboss.datagrid.JBossDatagrid7Service"
     */
    public static String extensionNameToExtensionServiceFullyQualifiedClassName(String extensionName)
            throws UserErrorException {

        if (extensionName.startsWith("embedded-")) {

            //
            // special services shortcut
            //

            String serviceType = extensionName.substring("embedded-".length());

            if (ServiceType.cache.name().equals(serviceType)) {

                return EmbeddedCacheService.class.getName();
            }

            throw new RuntimeException("NOT YET IMPLEMENTED embedded " + serviceType);
        }

        String packageName = "io.novaordis.gld.extensions";
        String className = "";

        StringTokenizer st = new StringTokenizer(extensionName, "-");

        while(st.hasMoreTokens()) {

            String token = st.nextToken();
            char firstChar = token.charAt(0);

            if ('0' <= firstChar && firstChar <= '9') {

                if (className.isEmpty() || st.hasMoreTokens()) {

                    //
                    // this is the first token, and a class name cannot start with a number, or is not the last token,
                    // which can be interpreted as version, hence start with a number
                    //

                    throw new UserErrorException(
                            "invalid extension name '" + extensionName +
                                    "', extension name component starts with a number");
                }

                //
                // last component, if contains numbers, do not use it as package name component, but use it
                // in the class name, after removing the dots
                //

                token = token.replace(".", "");
                className += token;
                break;
            }

            packageName += "." + token;

            //
            // capitalization exceptions
            //

            if ("jboss".equals(token.toLowerCase())) {

                token = "JBoss";
            }

            className += Character.toUpperCase(firstChar) + token.substring(1);
        }

        className += "Service";

        return packageName + "." + className;
    }

    /**
     * The implementation also establishes the Service - LoadStrategy bidirectional relationship.
     *
     * @exception UserErrorException on any instantiation exception, with human friendly error message
     */
    public static Service buildInstance(ServiceConfiguration sc, LoadStrategy loadStrategy, LoadDriver loadDriver)
            throws UserErrorException {

        ServiceType t = sc.getType();

        ImplementationConfiguration ic = sc.getImplementationConfiguration();

        String extensionName = null;
        String extensionServiceFQCN = ic.getExtensionClass();

        if (extensionServiceFQCN == null) {

            //
            // try name, as the class name is not available
            //

            extensionName = ic.getExtensionName();

            //
            // handle special extension names
            //

            if (EMBEDDED_MARKER.equals(extensionName)) {

                extensionName += "-" + t.name();
            }

            //
            // attempt to build a fully qualified class name based on the extension name, and then attempt to
            // instantiate it
            //

            extensionServiceFQCN = extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);
        }

        log.debug("attempting to load Service implementation class " + extensionServiceFQCN);

        Class c;

        try {

            c = Class.forName(extensionServiceFQCN);

        }
        catch (Exception e) {

            log.debug(extensionServiceFQCN + " loading failed", e);

            String msg = "extension class " + extensionServiceFQCN + " not found, make sure ";
            msg += extensionName == null ?
                    "the corresponding extension was installed" :
                    "'" + extensionName + "' extension was installed";
            throw new UserErrorException(msg, e);
        }

        if (!Service.class.isAssignableFrom(c)) {

            throw new UserErrorException(extensionServiceFQCN + " is not a Service implementation");
        }

        log.debug("attempting to instantiate Service implementation class " + extensionServiceFQCN);

        Object o;

        try {

            o = c.newInstance();

        }
        catch(Exception e) {

            log.debug(extensionServiceFQCN + " class instantiation failed", e);
            String msg = "extension class " + extensionServiceFQCN + " ";
            msg += extensionName != null ? "belonging to extension '" + extensionName + "' " : "";
            msg += "failed to instantiate";
            String emsg = e.getMessage();
            msg += emsg != null ? ": " + emsg : "";
            throw new UserErrorException(msg, e);
        }

        Service service = (Service) o;

        if (!t.equals(service.getType())) {

            throw new UserErrorException(extensionServiceFQCN + " is not a " + t + " Service");
        }

        //
        // install dependencies
        //

        service.setLoadStrategy(loadStrategy);
        service.setLoadDriver(loadDriver);

        //
        // establish Service - Load Strategy bidirectional relationship
        //

        loadStrategy.setService(service);

        return service;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
