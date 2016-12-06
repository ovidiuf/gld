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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.ClassLoadingUtilities;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceType;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class CacheLoadStrategyFactory implements LoadStrategyFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategyFactory implementation ------------------------------------------------------------------------------

    @Override
    public LoadStrategy buildInstance(Map<String, Object> configuration) throws Exception {

        String loadStrategyName = (String)configuration.get(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL);

        //
        // loadStrategyName can't be null, because we parsed the configuration already and we would have detected
        // the problem, but check nonetheless
        //

        if (loadStrategyName == null) {
            throw new IllegalArgumentException(
                    "missing or null '" + ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL + "' map element");
        }

        //
        // infer the class name from the strategy name and attempt to instantiate
        //

        String fqcn = LoadStrategyFactory.inferFullyQualifiedLoadStrategyClassName(loadStrategyName);

        LoadStrategy s = ClassLoadingUtilities.getInstance(LoadStrategy.class, fqcn);

        throw new RuntimeException("RETURN HERE");

//        s.configure(configuration);
//
//        return s;
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
