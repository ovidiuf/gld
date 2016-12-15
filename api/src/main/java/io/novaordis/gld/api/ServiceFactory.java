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

import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class ServiceFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceFactory.class);

    // Static ----------------------------------------------------------------------------------------------------------

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
            // try name, as the class name is not avaialbe
            //

            extensionName = ic.getExtensionName();

            //
            // attempt to build a fully qualified class name based on the extension name, and then attempt to instantiate it
            //

            extensionServiceFQCN = ImplementationConfiguration.
                    extensionNameToExtensionServiceFullyQualifiedClassName(extensionName, t);
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
