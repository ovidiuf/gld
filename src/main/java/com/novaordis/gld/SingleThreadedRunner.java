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

import com.novaordis.gld.operations.cache.Write;
import com.novaordis.gld.sampler.Sampler;
import org.apache.log4j.Logger;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleThreadedRunner implements Runnable {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SingleThreadedRunner.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long sleepMs;
    private String name;
    private KeyStore keyStore;
    final private Thread thread;
    final private Service service;
    final private Sampler sampler;
    final private LoadStrategy loadStrategy;
    final private CyclicBarrier allSingleThreadedRunnersBarrier;

    // we want to keep this package private because we want to be able to mark the runner as "running" without
    // actually starting the thread, for testing
    volatile boolean running;

    private AtomicBoolean durationExpired;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param sampler may be null.
     *
     * @param durationExpired an AtomicBoolean that will externally set to "true" if the overall time allocated to the
     *                        run expired. If the run is not time-limited, the boolean will never become "true".
     *                        Cannot be null.
     */
    public SingleThreadedRunner(
        String name, Configuration config, LoadStrategy loadStrategy,
        Sampler sampler, CyclicBarrier barrier, AtomicBoolean durationExpired) {

        if (config == null) {
            throw new IllegalArgumentException("null configuration");
        }

        if (barrier == null) {
            throw new IllegalArgumentException("null barrier");
        }

        if (sampler == null)  {
            throw new IllegalArgumentException("null sampler");
        }

        if (durationExpired == null)  {
            throw new IllegalArgumentException("null duration boolean");
        }

        this.name = name;
        this.sampler = sampler;
        this.sleepMs = config.getSleepMs();
        this.loadStrategy = loadStrategy;
        this.service = config.getService();
        this.keyStore = loadStrategy.getKeyStore();
        this.allSingleThreadedRunnersBarrier = barrier;
        this.durationExpired = durationExpired;

        if (service == null) {
            throw new IllegalArgumentException("a service cannot be obtained from configuration " + config);
        }

        thread = new Thread(this, name + " Thread");
    }

    // Runnable implementation -------------------------------------------------------------------------------------

    public void run() {

        try {

            loopUntilStoppedOrOutOfOperationsOrDurationExpired();
        }
        catch (Exception e) {

            log.error(this + " failed", e);
        }
        finally {

            try {

                allSingleThreadedRunnersBarrier.await();
            }
            catch(Exception e) {

                e.printStackTrace();
            }

            if (keyStore != null)  {

                try {

                    keyStore.stop();
                }
                catch(Exception e) {
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

    public void start() {

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

    private void loopUntilStoppedOrOutOfOperationsOrDurationExpired() throws Exception {

        long operationCounter = 0L;
        String lastWrittenKey = null;
        Operation lastOperation = null;

        while (running && !durationExpired.get()) {

            Operation op = loadStrategy.next(lastOperation, lastWrittenKey);

            if (op == null) {

                //
                // the strategy ran out of keys, it's time to finish
                //

                log.debug(Thread.currentThread().getName() + " ran out of operations, " + operationCounter +
                    " operations processed by this thread, exiting");

                return;
            }

            lastOperation = op;
            operationCounter ++;

            long t1 = -1L;
            Exception ex = null;
            long t0 = System.nanoTime();
            long t0Ms = System.currentTimeMillis();

            try  {

                op.perform(service);

                t1 = System.nanoTime();

                if (op instanceof Write && keyStore != null && !keyStore.isReadOnly()) {
                    // the operation was successful, which mean the key was written successfully; currently we store
                    // the key locally only if the operation was successful
                    lastWrittenKey = ((Write)op).getKey();
                    keyStore.store(lastWrittenKey);
                }
            }
            catch (Exception e) {

                t1 = System.nanoTime();
                ex = e;

                log.info("operation failed: " + e.getMessage(), e);
            }
            finally {

                //
                // sampler is never null, the constructor insures that
                //
                sampler.record(t0Ms, t0, t1, op, ex);

                if (sleepMs > 0) {

                    try {

                        Thread.sleep(sleepMs);
                    }
                    catch (InterruptedException e) {
                        log.warn("interrupted while sleeping");
                    }
                }
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
