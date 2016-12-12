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

import io.novaordis.gld.api.cache.local.LocalCacheService;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class ServiceFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static Service buildInstance(ServiceConfiguration sc, LoadStrategy loadStrategy, LoadDriver loadDriver)
            throws Exception {

        ServiceType t = sc.getType();
        String implementation = sc.getImplementation();

        if (implementation == null) {

            throw new IllegalArgumentException("null implementation");
        }

        if (ServiceType.cache.equals(t) && "local".equals(implementation)) {

            return new LocalCacheService(loadStrategy, loadDriver);
        }

        //
        // attempt to instantiate the fully qualified class name
        //


        Class c = Class.forName(implementation);

        if (!Service.class.isAssignableFrom(c)) {

            throw new UserErrorException(implementation + " is not a Service implementation");
        }

        Object o = c.newInstance();
        Service s = (Service)o;

        if (!t.equals(s.getType())) {

            throw new UserErrorException(implementation + " is not a " + t + " Service");
        }

        //
        // install dependencies
        //

        s.setLoadStrategy(loadStrategy);
        s.setLoadDriver(loadDriver);

        return s;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
