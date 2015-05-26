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


import org.junit.Test;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ImmutableFailureCounterTest extends FailureCounterTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void happyPath_miniStress() throws Exception
    {
        int threadCount = 130;
        int executionCount = 479;
        final long duration = 7L;

        final NonBlockingFailureCounter c = new NonBlockingFailureCounter(SocketException.class);

        final AtomicInteger counter = new AtomicInteger();

        ExecutorService es = Executors.newFixedThreadPool(threadCount);

        for(int i = 0; i < executionCount; i ++)
        {
            es.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    c.increment(duration);
                    counter.incrementAndGet();
                }
            });
        }

        // wait until we count 'executionCount' executions

        while(counter.get() < executionCount)
        {
            Thread.sleep(50L);
        }

        es.shutdown();

        assertEquals(SocketException.class, c.getFailureType());
        long[] counters = c.getCountersAndReset();
        assertEquals(2, counters.length);


        long failureCount = counters[0];
        long cumulatedDuration = counters[1];
        assertEquals(executionCount, failureCount);
        assertEquals(executionCount * duration, cumulatedDuration);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected NonBlockingFailureCounter getFailureCounterToTest(Class<? extends Throwable> failureType) throws Exception
    {
        return new NonBlockingFailureCounter(failureType);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
