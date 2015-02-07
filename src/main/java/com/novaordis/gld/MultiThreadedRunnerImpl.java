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

import com.novaordis.ac.Collector;
import com.novaordis.ac.CollectorFactory;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.statistics.CollectorBasedStatistics;
import com.novaordis.gld.statistics.SampleHandler;
import com.novaordis.gld.util.CommandLineConsole;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadedRunnerImpl implements MultiThreadedRunner
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MultiThreadedRunnerImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration config;
    private CollectorBasedStatistics statistics;
    private List<SingleThreadedRunner> singleThreadedRunners;
    private volatile boolean running;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MultiThreadedRunnerImpl(Configuration config)
    {
        this.config = config;
        this.singleThreadedRunners = new ArrayList<>();

    }

    // MultiThreadRunner implementation --------------------------------------------------------------------------------

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void stop()
    {
        for (SingleThreadedRunner r : singleThreadedRunners)
        {
            r.stop();
        }
    }

    @Override
    public Statistics getStatistics()
    {
        return statistics;
    }


    // Public ----------------------------------------------------------------------------------------------------------

    public void runConcurrently() throws Exception
    {
        Collector collector = null;
        LoadStrategy loadStrategy = config.getLoadStrategy();
        Service service = config.getService();
        Load command = (Load)config.getCommand();

        running = true;

        //System.out.println(config);

        try
        {
            collector = CollectorFactory.getInstance("SAMPLE COLLECTOR", Thread.NORM_PRIORITY + 1);
            collector.registerHandler(new SampleHandler(System.getProperty("collector.file")));
            if (config.getExceptionFile() != null)
            {
                collector.registerHandler(new ThrowableHandler(config.getExceptionFile()));
            }

            statistics = new CollectorBasedStatistics(collector);

            if (command.getMaxOperations() != null)
            {
                statistics.setMaxOperations(command.getMaxOperations());
            }

            // config.getThreads() + the main thread that runs this code
            final CyclicBarrier runnerThreadsBarrier = new CyclicBarrier(config.getThreads() + 1);

            for (int i = 0; i < config.getThreads(); i++)
            {
                String name = "CLD Runner " + i;

                SingleThreadedRunner r =
                    new SingleThreadedRunner(name, config, loadStrategy, statistics, runnerThreadsBarrier);

                singleThreadedRunners.add(r);
                r.start();
            }

            final CommandLineConsole commandLineConsole = new CommandLineConsole(this);
            commandLineConsole.start();

            log.debug("waiting for " + singleThreadedRunners.size() + " SingleThreadedRunner(s) to finish ...");

            runnerThreadsBarrier.await();

            log.debug(singleThreadedRunners.size() + " SingleThreadedRunner(s) have finished");

            // no more input needed from the console
            commandLineConsole.stop();

            statistics.close();

            System.out.println(statistics.aggregatesToString());
        }
        finally
        {
            if (collector != null)
            {
                collector.dispose();
            }

            if (service != null)
            {
                service.stop();
            }

            KeyStore keyStore = loadStrategy.getKeyStore();

            if (keyStore != null)
            {
                keyStore.stop();
            }

            running = false;
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
