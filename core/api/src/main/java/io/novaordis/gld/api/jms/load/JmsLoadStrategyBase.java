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
import io.novaordis.gld.api.RandomContentGenerator;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.ConnectionFactory;
import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JmsServiceConfiguration;
import io.novaordis.gld.api.jms.Queue;
import io.novaordis.gld.api.jms.Topic;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public abstract class JmsLoadStrategyBase extends LoadStrategyBase implements JmsLoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;
    private ConnectionFactory connectionFactory;
    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;
    private int messageSize;

    private volatile String cachedMessagePayload;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JmsLoadStrategyBase() {

        setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);
        this.cachedMessagePayload = null;
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

        if (!(sc instanceof JmsServiceConfiguration)) {

            throw new IllegalArgumentException(sc + " not a JmsServiceConfiguration");
        }

        JmsServiceConfiguration jmsSc = (JmsServiceConfiguration)sc;

        //
        // process common elements
        //

        //
        // required queue/topic name
        //

        String queueName = (String)loadStrategyRawConfig.remove(JmsLoadStrategy.QUEUE_LABEL);
        String topicName = (String)loadStrategyRawConfig.remove(JmsLoadStrategy.TOPIC_LABEL);

        if (queueName != null && topicName != null) {

            throw new UserErrorException("both a queue and a topic are specified, they should be mutually exclusive");
        }

        if (queueName != null) {

            this.destination = new Queue(queueName);
        }
        else {

            if (topicName == null) {

                throw new UserErrorException("required configuration element queue|topic missing");
            }

            this.destination = new Topic(topicName);
        }

        //
        // required connection factory
        //

        String connectionFactoryName = (String)loadStrategyRawConfig.remove(JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        if (connectionFactoryName == null) {

            throw new UserErrorException("required configuration element 'connection-factory' missing");
        }

        this.connectionFactory = new ConnectionFactory(connectionFactoryName);

        //
        // optional message size
        //

        Integer ms = jmsSc.get(Integer.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                JmsServiceConfiguration.MESSAGE_SIZE_LABEL);

        if (ms != null) {

            this.messageSize = ms;
        }
        else {

            this.messageSize = jmsSc.getMessageSize();
        }

        //
        // optional max messages
        //

        setOperations(lc.getOperations());

        //
        // optional connection policy
        //

        String cps = jmsSc.get(String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                JmsLoadStrategy.CONNECTION_POLICY_LABEL);

        if (cps != null) {

            connectionPolicy = ConnectionPolicy.fromString(cps);
        }

        //
        // optional session policy
        //

        String sps = jmsSc.get(String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                JmsLoadStrategy.SESSION_POLICY_LABEL);

        if (sps != null) {

            sessionPolicy = SessionPolicy.fromString(sps);
        }

        //
        // give the actual load strategy a chance to look for specific configuration elements
        //

        initInternal(jmsSc, loadStrategyRawConfig, lc);

        //
        // it is not required that the configuration is emptied at this point, the superclass will take care of this
        //
    }

    @Override
    public Destination getDestination() {

        return destination;
    }

    @Override
    public ConnectionFactory getConnectionFactory() {

        return connectionFactory;
    }

    @Override
    public ConnectionPolicy getConnectionPolicy() {

        return connectionPolicy;
    }

    @Override
    public SessionPolicy getSessionPolicy() {

        return sessionPolicy;
    }

    @Override
    public int getMessageSize() {

        return messageSize;
    }

    @Override
    public Long getMessages() {

        return getOperations();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Allow the strategy to provide a message payload (presumably cached) to speed the operation generation.
     */
    public String getMessagePayload() {

        if (cachedMessagePayload != null) {

            return cachedMessagePayload;
        }

        if (messageSize <= 0) {

            cachedMessagePayload = "";

            return cachedMessagePayload;
        }

        RandomContentGenerator valueGenerator = getValueGenerator();

        cachedMessagePayload = valueGenerator.getRandomString(ThreadLocalRandom.current(), messageSize);

        return cachedMessagePayload;
    }

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
     * Give the actual load strategy a chance to look for specific configuration elements.
     *
     * @see LoadStrategyBase#init(ServiceConfiguration, Map, LoadConfiguration)
     *
     */
    protected abstract void initInternal(
            ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
