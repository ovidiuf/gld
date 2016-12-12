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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.configuration.ServiceConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class MockServiceConfiguration implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final String CONTENT_KEY = "CONFIGURATION_MAP_FRAGMENT";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String loadStrategyName;
    private String implementation;
    private ServiceType serviceType;

    private Map<String, Object> rawConfigurationMaps;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockServiceConfiguration() {

        this.serviceType = ServiceType.mock;
        this.rawConfigurationMaps = new HashMap<>();
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return serviceType;
    }

    @Override
    public String getImplementation() {

        return implementation;
    }

    @Override
    public String getLoadStrategyName() {

        return loadStrategyName;
    }

    @Override
    public Map<String, Object> getMap(String... path) {

        Map<String, Object> current = rawConfigurationMaps;

        for(int i = 0; i < path.length; i ++) {

            String pathElement = path[i];

            //noinspection unchecked
            Map<String, Object> m = (Map<String, Object>)current.get(pathElement);

            if (m == null) {

                return Collections.emptyMap();
            }

            if (i == path.length - 1) {

                //
                // the last path element, attempt to next the content
                //

                //noinspection unchecked
                Map<String, Object> content = (Map<String, Object>)m.get(CONTENT_KEY);

                if (content == null) {

                    content = Collections.emptyMap();
                }

                return content;
            }
            else {

                current = m;
            }
        }

        return Collections.emptyMap();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setLoadStrategyName(String s) {

        this.loadStrategyName = s;
    }

    public void setImplementation(String s) {

        this.implementation = s;
    }

    public void setServiceType(ServiceType t) {

        this.serviceType = t;
    }

    public void setMap(Map<String, Object> rawConfigurationMap, String ... path) {

        Map<String, Object> current = rawConfigurationMaps;

        for(int i = 0; i < path.length; i ++) {

            String pathElement = path[i];

            //noinspection unchecked
            Map<String, Object> m = (Map<String, Object>)current.get(pathElement);

            if (m == null) {

                m = new HashMap<>();
                current.put(pathElement, m);
            }

            if (i == path.length - 1) {

                //
                // the last path element
                //

                m.put(CONTENT_KEY, rawConfigurationMap);
            }
            else {

                current = m;
            }
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
