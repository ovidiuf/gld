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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

    // buildInstance() static wrapper ----------------------------------------------------------------------------------

    @Test
    public void buildInstance_NullServiceType() throws Exception {

        try {

            LoadStrategyFactory.buildInstance(null, new HashMap<>());
            fail("should throw exception");
        }
        catch(IllegalArgumentException e) {

            assertEquals("null service type", e.getMessage());
        }
    }

    @Test
    public void buildInstance_KnownServiceType() throws Exception {

        LoadStrategy s = LoadStrategyFactory.buildInstance(ServiceType.mock, new HashMap<>());
        assertTrue(s instanceof MockLoadStrategy);
    }

    @Test
    public void buildInstance_UnknownServiceType() throws Exception {

        try {
            LoadStrategyFactory.buildInstance(ServiceType.unknown, new HashMap<>());
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

    // buildInstance() -------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance() throws Exception {

        LoadStrategyFactory f = getLoadStrategyFactoryToTest();
        ServiceType t = f.getServiceType();

        Map<String, Object> c = getCorrespondingConfigurationToTest();
        LoadStrategy s = f.buildInstance(c);
        ServiceType t2 = s.getServiceType();

        assertEquals(t, t2);
    }

    // inferFullyQualifiedLoadStrategyClassName() ----------------------------------------------------------------------

    @Test
    public void inferFullyQualifiedLoadStrategyClassName() throws Exception {

        fail("return here");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract LoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception;
    protected abstract Map<String, Object> getCorrespondingConfigurationToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
