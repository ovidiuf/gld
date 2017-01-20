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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactoryTest;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/5/16
 */
public class CacheLoadStrategyFactoryTest extends LoadStrategyFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CacheLoadStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void buildInstance_nullOrMissingLoadStrategyName() throws Exception {

        CacheLoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration mc = new MockCacheServiceConfiguration();

        try {

            f.buildInstance(mc, mlc);
            fail("should have failed");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing or null '" + ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL + "' map element", msg);
        }
    }

    @Test
    public void buildInstance_unknownCacheLoadStrategy() throws Exception {

        CacheLoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration mc = new MockCacheServiceConfiguration();

        mc.setLoadStrategyName("surely-there-is-no-such-load-strategy");

        try {

            f.buildInstance(mc, mlc);
            fail("should have failed");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("ClassNotFoundException io.novaordis.gld.api.cache.load.SurelyThereIsNoSuchLoadStrategy", msg);
        }
    }

    @Test
    public void buildInstance_DoesNotRequireConfig() throws Exception {

        //
        // we simulate a load strategy that does not require any special configuration
        //

        CacheLoadStrategyFactory f = getLoadStrategyFactoryToTest();

        MockLoadConfiguration mlc = new MockLoadConfiguration();
        MockCacheServiceConfiguration mc = new MockCacheServiceConfiguration();

        mc.setLoadStrategyName("mock");

        LoadStrategy s = f.buildInstance(mc, mlc);

        //
        // make sure the load strategy was built without any incident
        //

        assertNotNull(s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CacheLoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception {

        return new CacheLoadStrategyFactory();
    }

    @Override
    protected CacheServiceConfiguration getCorrespondingConfigurationToTest() throws Exception {

        MockCacheServiceConfiguration c = new MockCacheServiceConfiguration();
        c.setLoadStrategyName("mock");
        return c;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
