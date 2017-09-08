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
import io.novaordis.gld.api.jms.JMSServiceConfiguration;
import io.novaordis.gld.api.jms.Queue;
import io.novaordis.gld.api.jms.Topic;
import io.novaordis.gld.api.provider.NoopKeyProvider;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public abstract class JMSLoadStrategyBase extends LoadStrategyBase implements JMSLoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;
    private String connectionFactoryName;
    private String username;
    private char[] password;
    private ConnectionPolicy connectionPolicy;
    private SessionPolicy sessionPolicy;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JMSLoadStrategyBase() {

        setConnectionPolicy(ConnectionPolicy.CONNECTION_PER_RUN);
        setSessionPolicy(SessionPolicy.SESSION_PER_OPERATION);

        //
        // set the default value size, it will be overridden when init() is called
        //
        setValueSize(ServiceType.jms.getDefaultValueSize());
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

        if (!(sc instanceof JMSServiceConfiguration)) {

            throw new IllegalArgumentException(sc + " not a JMSServiceConfiguration");
        }

        JMSServiceConfiguration jmsSc = (JMSServiceConfiguration)sc;

        //
        // process common elements
        //

        //
        // required queue/topic name
        //

        String queueName = sc.remove(
                String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.QUEUE_LABEL);

        String topicName = sc.remove(
                String.class, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.TOPIC_LABEL);

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

        this.connectionFactoryName = sc.remove(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.CONNECTION_FACTORY_LABEL);

        if (connectionFactoryName == null) {

            throw new UserErrorException("required configuration element 'connection-factory' missing");
        }

        //
        // optional max messages
        //

        setOperations(lc.getOperations());

        //
        // optional connection policy
        //

        String cps = jmsSc.remove(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.CONNECTION_POLICY_LABEL);

        connectionPolicy = cps != null ? ConnectionPolicy.fromString(cps) : connectionPolicy;

        //
        // optional session policy
        //

        String sps = jmsSc.remove(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.SESSION_POLICY_LABEL);

        sessionPolicy = sps != null ? SessionPolicy.fromString(sps) : sessionPolicy;

        //
        // optional username
        //

        String s = jmsSc.remove(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.USERNAME_LABEL);

        setUsername(s);

        //
        // optional password
        //

        s = jmsSc.remove(String.class,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JMSLoadStrategy.PASSWORD_LABEL);

        if (s != null) {

            char[] ca = new char[s.length()];
            s.getChars(0, s.length(), ca, 0);
            setPassword(ca);
        }

        //
        // give the actual load strategy a chance to look for specific configuration elements
        //

        initInternal(jmsSc, loadStrategyRawConfig, lc);

        //
        // it is not required that the configuration is emptied at this point, the superclass will take care of this
        //

        //
        // the strategies are required to have a key provider, but in this case it should be a noop, because
        // the keys are generated by the JMS runtime
        //

        NoopKeyProvider keyProvider = new NoopKeyProvider();

        //
        // install the provider ...
        //
        setKeyProvider(keyProvider);

        //
        // ... and start it
        //

        keyProvider.start();
    }

    @Override
    public Destination getDestination() {

        return destination;
    }

    @Override
    public String getConnectionFactoryName() {

        return connectionFactoryName;
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
    public String getUsername() {

        return username;
    }

    @Override
    public char[] getPassword() {

        return password;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setUsername(String s) {

        this.username = s;
    }

    public void setPassword(char[] ca) {

        this.password = ca;
    }

    // Package protected -----------------------------------------------------------------------------------------------

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
