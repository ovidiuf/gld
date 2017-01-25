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

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.embedded.EmbeddedJmsService;
import io.novaordis.gld.api.jms.load.JmsLoadStrategy;
import io.novaordis.gld.api.jms.load.ReceiveLoadStrategy;
import io.novaordis.gld.api.jms.load.SendLoadStrategy;
import io.novaordis.gld.api.service.ServiceTest;
import io.novaordis.gld.api.service.ServiceType;
import org.junit.Test;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/20/17
 */
public abstract class JmsServiceTest extends ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getServiceType() throws Exception {

        JmsService s = getServiceToTest();
        assertEquals(ServiceType.jms, s.getType());
    }


    @Test
    public void sendEndToEnd() throws Exception {

        //
        // this is how the load driver initializes the service and the load strategy
        //

        MockJmsServiceConfiguration msc = new MockJmsServiceConfiguration();
        msc.set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(SendLoadStrategy.NAME, ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        msc.set("test-queue", ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.QUEUE_LABEL);
        msc.set("test-connection-factory",
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, JmsLoadStrategy.CONNECTION_FACTORY_LABEL);

        MockLoadConfiguration mlc = new MockLoadConfiguration();

        JmsService service = getServiceToTest();

        service.configure(msc);

        JmsLoadStrategy loadStrategy = new SendLoadStrategy();
        loadStrategy.init(msc, mlc);

        service.setLoadStrategy(loadStrategy);
        loadStrategy.setService(service);

        service.start();

        //
        // this is how the load driver handles an operation
        //

        Operation operation = loadStrategy.next(null, null, false);

        operation.perform(service);

        service.stop();

        //
        // test whether the message made it through
        //

        if (service instanceof EmbeddedJmsService) {

            EmbeddedJmsService es = (EmbeddedJmsService)service;

            List<Message> messages = es.getMessagesSentToDestination("test-queue", true);
            assertEquals(1, messages.size());
            assertEquals(loadStrategy.getReusedValue(), ((TextMessage)messages.get(0)).getText());
        }


    }

    @Test
    public void receiveEndToEnd() throws Exception {

        //
        // this is how the load driver initializes the service and the load strategy
        //

        JmsService service = getServiceToTest();

        JmsLoadStrategy loadStrategy = new ReceiveLoadStrategy();

        service.setLoadStrategy(loadStrategy);
        loadStrategy.setService(service);

        service.start();

        //
        // this is how the load driver handles an operation
        //

        Operation operation = loadStrategy.next(null, null, false);

        operation.perform(service);

        service.stop();

        fail("Return here");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected abstract JmsService getServiceToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
