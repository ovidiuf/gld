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

package io.novaordis.gld.driver.sampler;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class NonBlockingFailureCounterTest extends FailureCounterTest
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

        final NonBlockingFailureCounter c = new NonBlockingFailureCounter();

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

        ImmutableFailureCounter ifc = c.getFailureCounterSnapshotAndReset();
        assertEquals(executionCount, ifc.getCount());
        assertEquals(executionCount * duration, ifc.getCumulatedDurationNano());
    }

    @Test
    public void immutableFailureCounterCreation() throws Exception
    {
        NonBlockingFailureCounter nbfc = getFailureCounterToTest();

        ImmutableFailureCounter ifc = nbfc.getFailureCounterSnapshotAndReset();

        assertEquals(0L, ifc.getCount());
        assertEquals(0L, ifc.getCumulatedDurationNano());

        nbfc.increment(1L);
        nbfc.increment(2L);
        nbfc.increment(3L);

        ImmutableFailureCounter ifc2 = nbfc.getFailureCounterSnapshotAndReset();

        assertEquals(3L, ifc2.getCount());
        assertEquals(1L + 2L + 3L, ifc2.getCumulatedDurationNano());

        ImmutableFailureCounter ifc3 = nbfc.getFailureCounterSnapshotAndReset();

        assertEquals(0L, ifc3.getCount());
        assertEquals(0L, ifc3.getCumulatedDurationNano());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected NonBlockingFailureCounter getFailureCounterToTest() throws Exception
    {
        return new NonBlockingFailureCounter();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
