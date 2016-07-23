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

package com.novaordis.gld;

import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.mock.MockSampler;
import com.novaordis.gld.strategy.load.cache.MockLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiThreadedRunnerImplTest extends MultiThreadedRunnerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MultiThreadedRunnerImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle_ExitGuardPreConfiguredToAllowExit() throws Exception  {

        MockConfiguration mockConfiguration = new MockConfiguration();

        int threads = 3;
        mockConfiguration.setThreads(threads);

        MockService mockService = new MockService();
        mockConfiguration.setService(mockService);

        MockSampler mockSampler = new MockSampler();
        mockConfiguration.setSampler(mockSampler);

        int operations = 10;
        // configure the strategy to generate 10 operations and exit
        MockLoadStrategy mockLoadStrategy = new MockLoadStrategy(operations);
        mockConfiguration.setLoadStrategy(mockLoadStrategy);

        MultiThreadedRunnerImpl runner = new MultiThreadedRunnerImpl(mockConfiguration);

        // allow exit in advance, so we won't block on exit
        runner.getExitGuard().allowExit();

        runner.run();

        // we get here after all thread have have finished

        assertEquals(0, mockLoadStrategy.getRemainingOperations());

        assertTrue(mockSampler.wasStarted());
        assertFalse(mockSampler.isStarted());

        assertFalse(mockService.isStarted());
        assertTrue(mockService.wasStarted());

        log.info("the runner did exit after the threads finished because the exit guard allowed exit");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected MultiThreadedRunnerImpl getMultiThreadedRunnerToTest(Configuration c)  {
        return new MultiThreadedRunnerImpl(c);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
