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

import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.util.CommandLineConsole;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadedRunnerImpl implements MultiThreadedRunner
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MultiThreadedRunnerImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration conf;

    private Service service;
    private LoadStrategy loadStrategy;
    private List<SingleThreadedRunner> singleThreadedRunners;
    private CyclicBarrier barrier;

    private CommandLineConsole commandLineConsole;

    private Sampler sampler;

    private int threadCount;
    private volatile boolean running;

    private ExitGuard exitGuard;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MultiThreadedRunnerImpl(Configuration conf)
    {
        this.running = false;
        this.conf = conf;
        this.threadCount = conf.getThreads();
        this.singleThreadedRunners = new ArrayList<>(threadCount);
        this.exitGuard = new ExitGuard();
    }

    // MultiThreadRunner implementation --------------------------------------------------------------------------------

    @Override
    public boolean isRunning()
    {
        return running;
    }

    /**
     * Initializes dependencies, starts multiple SingleThreadedRunners in parallel, wait until those finish, shut
     * down dependencies and return.
     *
     * @see com.novaordis.gld.MultiThreadedRunner#run()
     */
    @Override
    public void run() throws Exception
    {
        running = true;

        try
        {
            initializeDependencies();

            for (int i = 0; i < threadCount; i++)
            {
                String name = "CLD Runner " + i;

                SingleThreadedRunner r = new SingleThreadedRunner(name, conf, loadStrategy, sampler, barrier);

                singleThreadedRunners.add(r);

                r.start();
            }

            log.debug("waiting for " + singleThreadedRunners.size() + " SingleThreadedRunner(s) to finish ...");

            barrier.await();

            log.debug(singleThreadedRunners.size() + " SingleThreadedRunner(s) have finished");

            if (commandLineConsole != null)
            {
                if (conf.waitForConsoleQuit())
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

            exitGuard.waitUntilExitIsAllowed();

        }
        finally
        {
            if (service != null)
            {
                service.stop();
            }

            KeyStore keyStore = loadStrategy.getKeyStore();

            if (keyStore != null)
            {
                keyStore.stop();
            }

            if (sampler != null)
            {
                sampler.stop();
            }

            running = false;
        }
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

    public ExitGuard getExitGuard()
    {
        return exitGuard;
    }

    @Override
    public String toString()
    {
        return "MultiThreadedRunner[" + Integer.toHexString(System.identityHashCode(this)) + "](" + threadCount + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void initializeDependencies() throws Exception
    {
        this.service = conf.getService();
        this.sampler = conf.getSampler();
        this.loadStrategy = conf.getLoadStrategy();

        if (service == null)
        {
            throw new IllegalStateException("null service");
        }

        if (!service.isStarted())
        {
            service.start();
        }

        if (sampler != null)
        {
            // initialize the sampler operations
            Set<Class<? extends Operation>> operationTypes = loadStrategy.getOperationTypes();
            for(Class<? extends Operation> ot: operationTypes)
            {
                sampler.registerOperation(ot);
            }

            sampler.start();
        }

        if (!conf.inBackground())
        {
            commandLineConsole = new CommandLineConsole(conf, this);
            commandLineConsole.start();
        }

        // threadCount + the main thread that runs this code
        barrier = new CyclicBarrier(threadCount + 1);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
