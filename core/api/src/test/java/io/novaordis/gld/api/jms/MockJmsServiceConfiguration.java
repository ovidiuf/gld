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

package io.novaordis.gld.api.jms;

import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.service.ServiceType;

import java.util.HashMap;

/**
 * Contains the default configuration expected from a JMS service, plus mock mandatory elements.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class MockJMSServiceConfiguration extends MockServiceConfiguration implements JMSServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockJMSServiceConfiguration() {

        //
        // we populate the service configuration with mandatory elements, so the initialization works well
        //

        set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        set("/jms/test-queue", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        set("/jms/TestConnectionFactory",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_FACTORY_LABEL);
    }

    // ServiceConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public ServiceType getType() {

        return ServiceType.jms;
    }

    // JMSServiceConfiguration implementation --------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    public String getQueueName() {

        return get(String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
