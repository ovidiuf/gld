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

import com.novaordis.gld.operations.Write;

import java.util.concurrent.CyclicBarrier;

public class SingleThreadedRunner implements Runnable
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;

    final private LoadStrategy loadStrategy;
    final private Statistics statistics;
    final private CyclicBarrier barrier;

    final private CacheService cacheService;

    private long sleep;

    final private Thread thread;

    private KeyStore keyStore;

    // we want to keep this package private because we want to be able to mark the runner as "running" without
    // actually starting the thread, for testing
    volatile boolean running;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SingleThreadedRunner(String name, Configuration config, LoadStrategy loadStrategy,
                                Statistics statistics, CyclicBarrier barrier)
    {
        if (config == null)
        {
            throw new IllegalArgumentException("null configuration");
        }

        if (statistics == null)
        {
            throw new IllegalArgumentException("null Statistics instance");
        }

        if (barrier == null)
        {
            throw new IllegalArgumentException("null barrier");
        }

        this.name = name;
        this.barrier = barrier;
        this.statistics = statistics;
        this.sleep = config.getSleep();
        this.loadStrategy = loadStrategy;
        this.keyStore = loadStrategy.getKeyStore();
        this.cacheService = config.getCacheService();

        if (cacheService == null)
        {
            throw new IllegalArgumentException("a cache service cannot be obtained from configuration " + config);
        }

        thread = new Thread(this, name + " Thread");
    }

    // Runnable implementation -------------------------------------------------------------------------------------

    public void run()
    {
        try
        {
            loopUntilStoppedOrOutOfOperations();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                barrier.await();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            if (keyStore != null)
            {
                try
                {
                    keyStore.stop();
                }
                catch(Exception e)
                {
                    e.printStackTrace();

                }
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void start()
    {
        running = true;
        thread.start();
    }

    public void stop()
    {
        running = false;
    }

    @Override
    public String toString()
    {
        return "SingleThreadedRunner[" + name + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void loopUntilStoppedOrOutOfOperations() throws Exception
    {
        String lastWrittenKey = null;
        Operation lastOperation = null;
        long operationCounter = 0L;

        while (running)
        {
            // not fully thread-safe, in the worst case we'll send N more operations than --max-operations, where
            // N is the number of threads.
            if (statistics.areWeDone())
            {
                System.out.println(Thread.currentThread().getName() + " reached the end of the counter, exiting");
                return;
            }

            Operation op = loadStrategy.next(lastOperation, lastWrittenKey);

            if (op == null)
            {
                // the strategy ran out of keys, it's time to finish
                System.out.println(Thread.currentThread().getName() + " ran out of operations, " + operationCounter + " operations processed by this thread, exiting");
                return;
            }

            operationCounter ++;
            lastOperation = op;

            long t1 = -1L;
            Exception ex = null;
            long t0 = System.nanoTime();
            long t0Ms = System.currentTimeMillis();

            try
            {
                op.perform(cacheService);
                t1 = System.nanoTime();

                if (op instanceof Write && keyStore != null && !keyStore.isReadOnly())
                {
                    // the operation was successful, which mean the key was written successfully; currently we store
                    // the key locally only if the operation was successful
                    lastWrittenKey = ((Write)op).getKey();
                    keyStore.store(lastWrittenKey);
                }
            }
            catch (Exception e)
            {
                t1 = System.nanoTime();
                ex = e;
            }
            finally
            {
                statistics.record(t0Ms, t0, t1, op, ex);

                if (sleep > 0)
                {
                    try
                    {
                        Thread.sleep(sleep);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
