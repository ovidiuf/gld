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

import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.cache.load.MockLoadStrategy;
import io.novaordis.gld.api.cache.local.LocalCacheService;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class ServiceFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ServiceFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // buildInstance() -------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance_ImplementationIsAConventionalName() throws Exception {

        MockLoadDriver md = new MockLoadDriver();
        MockLoadStrategy ms = new MockLoadStrategy();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementation("local");

        Service service = ServiceFactory.buildInstance(sc, ms, md);

        LocalCacheService lcs = (LocalCacheService)service;

        assertEquals(md, lcs.getLoadDriver());
        assertEquals(ms, lcs.getLoadStrategy());

        //
        // service - load strategy relationship
        //
        assertEquals(lcs, lcs.getLoadStrategy().getService());
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName_ImplementationIsNotAService() throws Exception {

        MockLoadDriver md = new MockLoadDriver();
        MockLoadStrategy ms = new MockLoadStrategy();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementation("io.novaordis.gld.api.MockLoadDriver");

        try {

            ServiceFactory.buildInstance(sc, ms, md);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("io.novaordis.gld.api.MockLoadDriver is not a Service implementation", msg);
        }
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName_ImplementationNotOfCorrectType()
            throws Exception {

        MockLoadDriver md = new MockLoadDriver();
        MockLoadStrategy ms = new MockLoadStrategy();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementation("io.novaordis.gld.api.MockService");

        try {

            ServiceFactory.buildInstance(sc, ms, md);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("io.novaordis.gld.api.MockService is not a " + sc.getType() +" Service", msg);
        }
    }

    @Test
    public void buildInstance_ImplementationIsAFullyQualifiedClassName() throws Exception {

        MockLoadDriver md = new MockLoadDriver();
        MockLoadStrategy ms = new MockLoadStrategy();
        MockCacheServiceConfiguration sc = new MockCacheServiceConfiguration();
        sc.setImplementation("io.novaordis.gld.api.cache.MockCacheService");

        Service service = ServiceFactory.buildInstance(sc, ms, md);

        MockService msrv = (MockService)service;

        assertEquals(md, msrv.getLoadDriver());
        assertEquals(ms, msrv.getLoadStrategy());

        //
        // service - load strategy relationship
        //
        assertEquals(msrv, msrv.getLoadStrategy().getService());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
