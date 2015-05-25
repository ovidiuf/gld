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

package com.novaordis.gld.sampler;

import com.novaordis.gld.Operation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class CounterTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CounterTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void t1PrecedesT0() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        try
        {
            c.update(0L, 10L, 9L);
            fail("should fail because t1 precedes t0");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void happyPath_Success() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        c.update(0L, 10L, 11L);
        c.update(1L, 10L, 12L);
        c.update(2L, 10L, 13L);

        assertEquals(MockOperation.class, c.getOperationType());

        CounterValues v0 = c.getCounterValuesAndReset();

        long sc0 = v0.getSuccessCount();
        long sct0 = v0.getSuccessCumulatedTime();

        assertEquals(3, sc0);
        assertEquals(1L + 2L + 3L, sct0);

        // TODO
        // v0.getFailureCount(Exception.class);

        c.update(3L, 10L, 14L);

        CounterValues v1 = c.getCounterValuesAndReset();

        long sc1 = v1.getSuccessCount();
        long sct1 = v1.getSuccessCumulatedTime();

        assertEquals(1, sc1);
        assertEquals(4L, sct1);

        CounterValues v2 = c.getCounterValuesAndReset();

        long sc2 = v2.getSuccessCount();
        long sct2 = v2.getSuccessCumulatedTime();

        assertEquals(0, sc2);
        assertEquals(0L, sct2);
    }

    @Test
    public void happyPath_Failures() throws Exception
    {
        fail("return here");
    }

    @Test
    public void multiThreadedMiniStress_SuccessAndFailures() throws Exception
    {
        fail("return here");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Counter getCounterToTest(Class<? extends Operation> operationType) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
