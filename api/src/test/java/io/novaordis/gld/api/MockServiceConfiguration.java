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

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class MockServiceConfiguration implements ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String loadStrategyName;
    private String implementation;
    private ServiceType serviceType;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockServiceConfiguration() {

        this.serviceType = ServiceType.mock;
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
        throw new RuntimeException("getMap() NOT YET IMPLEMENTED");
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
