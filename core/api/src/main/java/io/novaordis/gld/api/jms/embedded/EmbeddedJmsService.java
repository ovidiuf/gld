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

package io.novaordis.gld.api.jms.embedded;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.JmsServiceBase;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.utilities.UserErrorException;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/25/17
 */
public class EmbeddedJmsService extends JmsServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsService implementation ---------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        String connectionFactoryName = serviceConfiguration.get(
                String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        if (connectionFactoryName == null) {

            throw new IllegalArgumentException("missing connection factory name");
        }

        setConnectionFactory(new EmbeddedConnectionFactory());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return an empty list if there were no message sent, or the queue does not exist.
     */
    public List<Message> getMessagesSentToDestination(String destinationName, boolean queue) throws Exception {

        List<Message> result = new ArrayList<>();

        EmbeddedConnectionFactory cf = (EmbeddedConnectionFactory)getConnectionFactory();
        result.addAll(cf.getMessagesSentToDestination(destinationName, queue));

        return result;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
