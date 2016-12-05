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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.utilities.UserErrorException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockService implements Service {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean verbose;
    private boolean started;
    private boolean wasStarted;

    private Map<Thread, Integer> perThreadInvocationCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockService() {

        perThreadInvocationCount = new ConcurrentHashMap<>();
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public void setConfiguration(Configuration c)
    {
        throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes)
    {
        throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException
    {
        throw new RuntimeException("init() NOT YET IMPLEMENTED");
    }

    @Override
    public ContentType getContentType()
    {
        throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadDriver getLoadDriver() {
        throw new RuntimeException("getLoadDriver() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadStrategy getLoadStrategy() {
        throw new RuntimeException("getLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception {
        started = true;
        wasStarted = true;
        log.info(this + " started");
    }

    @Override
    public void stop() throws Exception {
        started = false;
        log.info(this + " stopped");
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void perform(Operation o) throws Exception {

        //
        // may be executing concurrently on multiple threads
        //
        if (verbose) { log.info(this + " performing " + o); }

        Thread currentThread = Thread.currentThread();

        Integer invocationCountPerThread = perThreadInvocationCount.get(currentThread);
        if (invocationCountPerThread == null) {

            invocationCountPerThread = 1;
            perThreadInvocationCount.put(currentThread, invocationCountPerThread);
        }
        else {

            perThreadInvocationCount.put(currentThread, invocationCountPerThread + 1);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if start() method was called at least once
     */
    public boolean wasStarted()
    {
        return wasStarted;
    }

    public Map<Thread, Integer> getPerThreadInvocationCountMap() {
        return perThreadInvocationCount;
    }

    /**
     * We need to explicitly set the instance as verbose in order to get log.info(), otherwise the high concurrency
     * tests are too noisy.
     */
    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    @Override
    public String toString() {

        return "MockService[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
