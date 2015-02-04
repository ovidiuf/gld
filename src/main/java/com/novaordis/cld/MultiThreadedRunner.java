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

package com.novaordis.cld;

import com.novaordis.ac.Collector;
import com.novaordis.ac.CollectorFactory;
import com.novaordis.cld.util.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadedRunner
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration config;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MultiThreadedRunner(Configuration config)
    {
        this.config = config;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void runConcurrently() throws Exception
    {
        Collector collector = null;
        CollectorBasedStatistics statistics;
        final Console console = new Console();
        LoadStrategy loadStrategy = config.getLoadStrategy();
        CacheService cacheService = config.getCacheService();
        List<SingleThreadedRunner> runners = new ArrayList<>();

        System.out.println(config);

        try
        {
            collector = CollectorFactory.getInstance("SAMPLE COLLECTOR", Thread.NORM_PRIORITY + 1);
            collector.registerHandler(new SampleHandler(System.getProperty("collector.file")));
            if (config.getExceptionFile() != null)
            {
                collector.registerHandler(new ThrowableHandler(config.getExceptionFile()));
            }

            statistics = new CollectorBasedStatistics(collector);

            if (config.getMaxOperations() > 0)
            {
                statistics.setMaxOperations(config.getMaxOperations());
            }

            final CyclicBarrier barrier = new CyclicBarrier(config.getThreads() + 1);

            for (int i = 0; i < config.getThreads(); i++)
            {
                String name = "CLD Runner " + i;
                SingleThreadedRunner r = new SingleThreadedRunner(name, config, loadStrategy, statistics, barrier);

                runners.add(r);
                r.start();
            }

            // ... if there are threads ... (we need this because in some unit test scenario, the number of threads
            // is (legally) 0.

            if (config.getThreads() > 0)
            {
                String input = null;

                while(true)
                {
                    System.out.print("['q'|message]> ");

                    input = console.getInput();

                    if (input == null)
                    {
                        throw new IllegalStateException("got null from the console");
                    }

                    if ("q".equals(input.toLowerCase()))
                    {
                        break;
                    }

                    // only send non-empty content to the log
                    input = input.trim();

                    if (!input.isEmpty())
                    {
                        collector.handOver(input);
                    }
                }
            }

            for (SingleThreadedRunner r : runners)
            {
                r.stop();
            }

            barrier.await();

            // all runner threads are done; flush the collector to make sure all the information is displayed
            // TODO_FLUSH_COLLECTOR

            System.out.println(statistics.aggregatesToString());
        }
        finally
        {
            if (collector != null)
            {
                collector.dispose();
            }

            if (cacheService != null)
            {
                cacheService.stop();
            }

            KeyStore keyStore = loadStrategy.getKeyStore();

            if (keyStore != null)
            {
                keyStore.stop();
            }
        }
    }

    @Override
    public String toString()
    {
        return "MultiThreadedRunner[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
