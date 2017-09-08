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

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.cache.load.MockLoadStrategy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class MockServiceBaseTest extends ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockServiceBaseTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // isStarted() -----------------------------------------------------------------------------------------------------

    @Test
    public void isStarted_MakeSureStartUsesIsStarted() throws Exception {

        MockServiceBase ms = getServiceToTest();

        MockLoadStrategy mls = new MockLoadStrategy();
        ms.setLoadStrategy(mls);

        ms.start();

        assertTrue(ms.isStarted());

        //
        // break "isStarted()" to make sure start() uses it
        //

        ms.makeFail("isStarted", new RuntimeException("SYNTHETIC"));

        try {

            ms.start();
            fail("should throw a synthetic exception as persumably it invokes isStarted()");
        }
        catch(RuntimeException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("SYNTHETIC", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected MockServiceBase getServiceToTest() throws Exception {

        return new MockServiceBase();
    }

    @Override
    protected MockLoadStrategy getMatchingLoadStrategyToTest(Service s) {

        return new MockLoadStrategy();
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
