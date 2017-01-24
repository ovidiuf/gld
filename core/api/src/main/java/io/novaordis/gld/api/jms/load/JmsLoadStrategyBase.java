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

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.service.ServiceType;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public abstract class JmsLoadStrategyBase extends LoadStrategyBase implements JmsLoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;
    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JmsLoadStrategyBase() {

        setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {

        return ServiceType.jms;
    }

    /**
     * This part processes common configuration elements, <b>must</b> be invoked by sub-classes with super.init(...)
     * at the beginning of their own implementation.
     */
    @Override
    protected void init(ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc)
            throws Exception {

        //
        // process common elements
        //

        throw new RuntimeException("init() NOT YET IMPLEMENTED");

//        /**
//         * Parse context-relevant command line arguments and removes them from the list. If not finding the arguments
//         * we need in the list, try the configuration second. It's not sensitive to null, null is fine and it is ignored.
//         */
//        private void processContextRelevantArguments(List<String> arguments, int from) throws UserErrorException
//        {
//            String queueName = Util.extractString("--queue", arguments, from);
//
//            if (queueName == null)
//            {
//                Properties p = getConfiguration().getConfigurationFileContent();
//                queueName = p == null ? null : p.getProperty("queue");
//            }
//
//            String topicName = Util.extractString("--topic", arguments, from);
//
//            if (topicName == null)
//            {
//                Properties p = getConfiguration().getConfigurationFileContent();
//                topicName = p == null ? null : p.getProperty("topic");
//            }
//
//            if (queueName == null && topicName == null)
//            {
//                throw new UserErrorException("a destination is required; use --queue|--topic <name>");
//            }
//
//            if (queueName != null && topicName != null)
//            {
//                throw new UserErrorException("both --queue and --topic used; only one must be specified");
//            }
//
//            if (queueName != null)
//            {
//                setDestination(new Queue(queueName));
//            }
//            else
//            {
//                setDestination(new Topic(topicName));
//            }
//
//            String endpointPolicy = Util.extractString("--endpoint-policy", arguments, from);
//
//            if (endpointPolicy != null)
//            {
//                try
//                {
//                    this.endpointPolicy = EndpointPolicy.valueOf(endpointPolicy);
//                }
//                catch(Exception e)
//                {
//                    throw new UserErrorException(
//                            "invalid --endpoint-policy value \"" + endpointPolicy + "\"; valid options: " +
//                                    Arrays.asList(EndpointPolicy.values()), e);
//                }
//            }
//
//            // TODO this is fishy, refactor both here and in ReadThenWriteOnMissLoadStrategy
//            Load load = (Load)getConfiguration().getCommand();
//
//            if (load != null)
//            {
//                Long maxOperations = load.getMaxOperations();
//
//                if (maxOperations == null)
//                {
//                    // try the configuration file
//                    // TODO need to refactor this for a consistent command-line/configuration file approach
//                    Properties p = getConfiguration().getConfigurationFileContent();
//                    if (p != null)
//                    {
//                        String s = p.getProperty("message-count");
//                        if (s != null)
//                        {
//                            maxOperations = Long.parseLong(s);
//                        }
//                    }
//                }
//
//                if (maxOperations != null)
//                {
//                    remainingOperations = new AtomicLong(maxOperations);
//                }
//            }
//        }

//
//
//
//        setMessageSize(configuration.getValueSize());
//
//
    }

    @Override
    public Destination getDestination()
    {
        return destination;
    }

    @Override
    public ConnectionPolicy getConnectionPolicy() {

        return connectionPolicy;
    }

    @Override
    public SessionPolicy getSessionPolicy() {

        return sessionPolicy;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    void setDestination(Destination d) {

        this.destination = d;
    }

    void setConnectionPolicy(ConnectionPolicy cp) {

        this.connectionPolicy = cp;
    }

    void setSessionPolicy(SessionPolicy sp) {

        this.sessionPolicy = sp;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * Allow the strategy to provide a message payload (presumably cached) to speed the operation generation.
     */
    public String getMessagePayload() {

        throw new RuntimeException("NYE");

//        if (cachedMessagePayload == null)
//        {
//            if (messageSize <= 0)
//            {
//                cachedMessagePayload = "";
//            }
//            else
//            {
//                cachedMessagePayload = Util.getRandomString(new Random(System.currentTimeMillis()), messageSize, 5);
//            }
//        }
//
//        return cachedMessagePayload;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
