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

import io.novaordis.gld.api.LoadStrategyFactoryTest;
import io.novaordis.gld.api.jms.JMSServiceConfiguration;
import io.novaordis.gld.api.jms.MockJMSServiceConfiguration;
import io.novaordis.gld.api.service.ServiceType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class JMSLoadStrategyFactoryTest extends LoadStrategyFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JMSLoadStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void serviceType() throws Exception {

        JMSLoadStrategyFactory f = getLoadStrategyFactoryToTest();
        assertEquals(ServiceType.jms, f.getServiceType());
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected JMSLoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception {

        return new JMSLoadStrategyFactory();
    }

    @Override
    protected JMSServiceConfiguration getCorrespondingConfigurationToTest() throws Exception {

        MockJMSServiceConfiguration c = new MockJMSServiceConfiguration();
        c.setLoadStrategyName("mock");
        log.debug("returning " + c);
        return c;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
