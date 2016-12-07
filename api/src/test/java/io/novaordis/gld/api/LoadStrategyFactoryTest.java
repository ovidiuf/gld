/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api;

import io.novaordis.gld.api.mock.load.MockLoadStrategy;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class LoadStrategyFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // inferFullyQualifiedLoadStrategyClassName() ----------------------------------------------------------------------

    @Test
    public void inferFullyQualifiedLoadStrategyClassName() throws Exception {

        String s = LoadStrategyFactory.inferFullyQualifiedLoadStrategyClassName(ServiceType.mock, "kcom");
        assertEquals("io.novaordis.gld.api.mock.load.KcomLoadStrategy", s);
    }

    // inferSimpleClassName() ------------------------------------------------------------------------------------------

    @Test
    public void inferSimpleClassName() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("write");
        assertEquals("WriteLoadStrategy", s);
    }

    @Test
    public void inferSimpleClassName_Dashes() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("read-then-write-on-miss");
        assertEquals("ReadThenWriteOnMissLoadStrategy", s);
    }

    @Test
    public void inferSimpleClassName_EndsInLoadStrategy() throws Exception {

        String s = LoadStrategyFactory.inferSimpleClassName("some-load-strategy");
        assertEquals("SomeLoadStrategy", s);
    }

    // build() static wrapper ----------------------------------------------------------------------------------

    @Test
    public void buildInstance_NullServiceType() throws Exception {

        MockServiceConfiguration msc = new MockServiceConfiguration();
        msc.setServiceType(null);

        try {

            LoadStrategyFactory.build(msc);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            assertEquals("null service type", e.getMessage());
        }
    }

    @Test
    public void buildInstance_KnownServiceType() throws Exception {

        MockServiceConfiguration msc = new MockServiceConfiguration();
        LoadStrategy s = LoadStrategyFactory.build(msc);
        assertTrue(s instanceof MockLoadStrategy);
    }

    @Test
    public void buildInstance_UnknownServiceType() throws Exception {

        MockServiceConfiguration msc = new MockServiceConfiguration();
        msc.setServiceType(ServiceType.unknown);

        try {
            LoadStrategyFactory.build(msc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "failed to instantiate a load strategy factory corresponding to a service of type unknown"));
            Throwable t = e.getCause();
            assertTrue(t instanceof ClassNotFoundException);
        }
    }

    // build() -------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance() throws Exception {

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();

        ServiceType t = f.getServiceType();

        ServiceConfiguration c = getCorrespondingConfigurationToTest();
        String loadStrategyName = c.getLoadStrategyName();
        assertNotNull(loadStrategyName);

        LoadStrategy s = f.buildInstance(c);
        ServiceType t2 = s.getServiceType();

        assertEquals(t, t2);

        //
        // make sure the name of the strategy is configured
        //
        assertEquals(loadStrategyName, s.getName());

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception;
    protected abstract ServiceConfiguration getCorrespondingConfigurationToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
