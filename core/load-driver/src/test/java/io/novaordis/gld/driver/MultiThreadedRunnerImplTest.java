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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiThreadedRunnerImplTest extends MultiThreadedRunnerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MultiThreadedRunnerImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void lifecycle_ExitGuardPreConfiguredToAllowExit() throws Exception  {
//
//        int threadCount = 3;
//
//        MockService mockService = new MockService();
//        MockSampler mockSampler = new MockSampler();
//
//        int operations = 10;
//        // init the strategy to generate 10 operations and exit
//        MockLdLoadStrategy mockLoadStrategy = new MockLdLoadStrategy(operations);
//
//        MultiThreadedRunnerImpl runner = new MultiThreadedRunnerImpl(mockService, threadCount, mockSampler, false, -1L);
//
//        // allow exit in advance, so we won't block on exit
//        runner.getExitGuard().allowExit();
//
//        runner.run();
//
//        // we next here after all thread have have finished
//
//        Assert.assertEquals(0, mockLoadStrategy.getRemainingOperations());
//
//        assertTrue(mockSampler.wasStarted());
//        assertFalse(mockSampler.isStarted());
//
//        assertFalse(mockService.isStarted());
//        assertTrue(mockService.wasStarted());
//
//        log.info("the runner did exit after the threads finished because the exit guard allowed exit");
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected MultiThreadedRunnerImpl getMultiThreadedRunnerToTest(
            Service service, Sampler sampler, KeyStore keyStore, boolean background, int threadCount) throws Exception {

        return new MultiThreadedRunnerImpl(service, sampler, keyStore, threadCount, background, -1L);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
