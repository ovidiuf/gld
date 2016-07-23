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

package com.novaordis.gld.mock;

import com.novaordis.gld.ExitGuard;
import com.novaordis.gld.MultiThreadedRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MockMultiThreadRunner implements MultiThreadedRunner
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private BlockingQueue<String> stopRendezvous = new ArrayBlockingQueue<>(1);

    private boolean running = true;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMultiThreadRunner()
    {
    }

    // MultiThreadRunner implementation --------------------------------------------------------------------------------

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void run()
    {
        throw new RuntimeException("run() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop()
    {
        try
        {
            stopRendezvous.put("");

            running = false;
        }
        catch(Exception e)
        {
            throw new IllegalStateException("failed to put in rendezvous", e);
        }
    }

    @Override
    public ExitGuard getExitGuard() {
        throw new RuntimeException("getExitGuard() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void waitToBeStopped() throws Exception
    {
        stopRendezvous.take();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
