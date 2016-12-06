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

import io.novaordis.gld.api.LoadStrategyFactoryTest;
import io.novaordis.gld.api.ServiceConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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

        CacheLoadStrategyFactory f = new CacheLoadStrategyFactory();

        try {

            f.buildInstance(new HashMap<>());
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

        fail("return here");
    }

    @Test
    public void buildInstance () throws Exception {

        fail("return here");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CacheLoadStrategyFactory getLoadStrategyFactoryToTest() throws Exception {

        return new CacheLoadStrategyFactory();
    }

    @Override
    protected Map<String, Object> getCorrespondingConfigurationToTest() throws Exception {

        Map<String, Object> typicalConfiguration = new HashMap<>();
        typicalConfiguration.put(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL, "mock");
        return typicalConfiguration;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
