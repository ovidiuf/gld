/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api;

import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LoadStrategyFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    Logger log = LoggerFactory.getLogger(LoadStrategyFactory.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * A static method that builds the specialized factory dynamically and passes the control to it. We do it this way
     * because we want to be able to add arbitrary service types in the future without modifying the code of this layer
     * of the factory.
     *
     * @param sc the associated service configuration instance.
     *
     * @return a LoadStrategy instance
     *
     * @exception io.novaordis.utilities.UserErrorException if the strategy instance cannot be instantiated because
     * of an user error (improperly specified service type, load strategy name, etc).
     *
     * @exception IllegalArgumentException on null service type
     */
    static LoadStrategy build(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        ServiceType t = sc.getType();

        if (t == null) {
            throw new IllegalArgumentException("null service type");
        }

        //
        // we instantiate the load strategy factory corresponding to a specific service type dynamically. This
        // is to allow adding new service types without changing this method.
        //

        String serviceTypeName = t.name();

        //
        // we expect the factory class name to match the following pattern:
        //
        // <this-package>.<service-type>.load.<service-type-with-first-letter-capitalized>LoadFactory
        //

        String className = LoadStrategyFactory.class.getPackage().getName();

        className += "." + serviceTypeName;
        className += ".load.";
        className += Character.toUpperCase(serviceTypeName.charAt(0)) + serviceTypeName.substring(1);
        className += "LoadStrategyFactory";

        log.debug("attempting to use load strategy factory class " + className);

        LoadStrategyFactory f;

        try {

            Class c = Class.forName(className);
            f = (LoadStrategyFactory) c.newInstance();

        } catch (Exception e) {

            throw new UserErrorException(
                    "failed to instantiate a load strategy factory corresponding to a service of type " + t, e);
        }

        LoadStrategy ls = f.buildInstance(sc, lc);

        //
        // don't init(), the factory instance is supposed to do it
        //

        return ls;
    }

    /**
     * @param loadStrategyName the load strategy name, as read from the configuration file.
     *
     * @exception UserErrorException with a human readable messages if encountering difficulties.
     */
    static String inferFullyQualifiedLoadStrategyClassName(ServiceType serviceType, String loadStrategyName)
            throws UserErrorException {

        //
        // we attempt to find the implementation in <this-package>.<service-type>.load.<load-strategy-name>LoadStrategy
        //

        String thisPackage = LoadStrategyFactory.class.getPackage().getName();

        String fqcn = thisPackage + "." + serviceType.name() + ".load";

        String className = inferSimpleClassName(loadStrategyName);

        fqcn = fqcn + "." + className;

        return fqcn;
    }

    /**
     * @param loadStrategyName the load strategy name, as read from the configuration file.
     *
     * @return a LoadStrategy implementation simple class name candidate. Most commonly, this is obtained by upping the
     * case of the first character, turning into camel case and appending LoadStrategy.
     *
     * @exception UserErrorException with a human readable messages if encountering difficulties.
     */
    static String inferSimpleClassName(String loadStrategyName) throws UserErrorException {

        String result = "";
        boolean up = true;

        for(int i = 0; i < loadStrategyName.length(); i ++) {

            char c = loadStrategyName.charAt(i);

            if (c == '-') {

                up = true;
                continue;
            }

            if (up) {

                up = false;
                result += Character.toUpperCase(c);
            }
            else {
                result += c;
            }
        }

        if (!result.endsWith("LoadStrategy")) {

            result += "LoadStrategy";
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @param sc the associated ServiceConfiguration instance.
     *
     * @exception UserErrorException with a human readable messages if encountering difficulties.
     */
    LoadStrategy buildInstance(ServiceConfiguration sc, LoadConfiguration lc) throws Exception;

    /**
     * @return the service type the load strategies built by this factory are associated with.
     */
    ServiceType getServiceType();

}
