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

import com.novaordis.gld.statistics.CollectorBasedCsvStatistics;
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

    private Configuration configuration;
    private List<SingleThreadedRunner> singleThreadedRunners;
    private volatile boolean running;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MultiThreadedRunnerImpl(Configuration configuration)
    {
        this.configuration = configuration;
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

    // Public ----------------------------------------------------------------------------------------------------------

    public void runConcurrently() throws Exception
    {
        running = true;
        Service service = configuration.getService();
        LoadStrategy loadStrategy = configuration.getLoadStrategy();
        Statistics statistics = configuration.getStatistics();
        CommandLineConsole commandLineConsole = null;

        if (!configuration.inBackground() && statistics != null)
        {
            commandLineConsole = new CommandLineConsole(configuration, this);
        }

        try
        {
            // configuration.getThreads() + the main thread that runs this code
            final CyclicBarrier runnerThreadsBarrier = new CyclicBarrier(configuration.getThreads() + 1);

            for (int i = 0; i < configuration.getThreads(); i++)
            {
                String name = "CLD Runner " + i;

                SingleThreadedRunner r =
                    new SingleThreadedRunner(name, configuration, loadStrategy, statistics, runnerThreadsBarrier);

                singleThreadedRunners.add(r);
                r.start();
            }

            if (commandLineConsole != null)
            {
                commandLineConsole.start();
            }

            log.debug("waiting for " + singleThreadedRunners.size() + " SingleThreadedRunner(s) to finish ...");

            runnerThreadsBarrier.await();

            log.debug(singleThreadedRunners.size() + " SingleThreadedRunner(s) have finished");

            if (commandLineConsole != null)
            {
                if (configuration.waitForConsoleQuit())
                {
                    log.debug("waiting for console to issue quit ...");
                    commandLineConsole.waitForExplicitQuit();
                    log.debug("console issued quit");
                }
                else
                {
                    commandLineConsole.stop(); // no more input needed from the console so dispose of it
                }
            }
        }
        finally
        {
            if (statistics != null)
            {
                statistics.close();
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

        if (statistics != null)
        {
            System.out.println(((CollectorBasedCsvStatistics)statistics).aggregatesToString());
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
