/*
 * Copyright (c) 2017 Nova Ordis LLC
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

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public abstract class LoadStrategyFactoryBase implements LoadStrategyFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategyFactory implementation ------------------------------------------------------------------------------

    @Override
    public LoadStrategy buildInstance(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        String loadStrategyName = sc.getLoadStrategyName();

        //
        // loadStrategyName can't be null, because we parsed the configuration already and we would have detected
        // the problem, but check nonetheless
        //

        if (loadStrategyName == null) {

            throw new IllegalArgumentException("missing or null '" + LoadStrategy.NAME_LABEL + "' map element");
        }

        //
        // infer the class name from the strategy name and attempt to instantiate
        //

        ServiceType ourServiceType = getServiceType();

        String fqcn = LoadStrategyFactory.inferFullyQualifiedLoadStrategyClassName(ourServiceType, loadStrategyName);

        LoadStrategy s = ClassLoadingUtilities.getInstance(LoadStrategy.class, fqcn);

        s.init(sc, lc);

        return s;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
