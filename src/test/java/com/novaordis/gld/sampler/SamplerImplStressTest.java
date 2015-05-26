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

import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;

public class SamplerImplStressTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerImplStressTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void severalSamplingCycles() throws Exception
    {
        final SamplerImpl s = new SamplerImpl(250L, 1000L);

        s.registerOperation(MockOperation.class);
        long interval = 1000L;
        s.setSamplingIntervalMs(interval);

        final List<SamplingInterval> samples = new ArrayList<>();

        s.registerConsumer(new SamplingConsumer()
        {
            @Override
            public void consume(SamplingInterval... si)
            {
                samples.addAll(Arrays.asList(si));
            }
        });

        s.start();

        int threads = 300;
        final int operationsPerThread = 1200000;
        final CyclicBarrier barrier = new CyclicBarrier(threads + 1);
        for(int i = 0; i < threads; i ++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    for(int j = 0; j < operationsPerThread; j ++)
                    {
                        long t0Nano = System.nanoTime();
                        MockOperation mo = new MockOperation();
                        long t1Nano = System.nanoTime();
                        s.record(System.currentTimeMillis(), t0Nano, t1Nano, mo);
                    }

                    log.info(Thread.currentThread().getName() + " finished");

                    try
                    {
                        barrier.await();

                        log.info(Thread.currentThread().getName() + " released from barrier");

                    }
                    catch(Exception e)
                    {
                        throw new IllegalStateException(e);
                    }

                }
            }, "Pump " + i).start();
        }

        // wait for all pump threads to finish
        barrier.await();
        log.info(Thread.currentThread().getName() + " released from barrier");


        log.info("sleeping for " + (interval / 1000L) + " seconds ...");
        Thread.sleep(interval);

        log.info("stopping the sampler ...");

        s.stop();

        log.info("sampler stopped");

        log.info(samples.size() + " samples collected");

        long successful = 0;

        for(SamplingInterval si: samples)
        {
            successful += si.getCounterValues(MockOperation.class).getSuccessCount();
        }

        log.info(successful + " successful operations");
        assertEquals(successful, threads * operationsPerThread);

        // make sure the interval duration is constant and equal with the sampling interval

        for(SamplingInterval si: samples)
        {
            assertEquals(
                "the interval duration " + si.getDurationMs() + " ms is not equal to " + interval + " ms for " + si,
                interval, si.getDurationMs());
        }

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
