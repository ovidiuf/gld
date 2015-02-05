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

package com.novaordis.gld.util;

import com.novaordis.gld.mock.MockMultiThreadRunner;
import com.novaordis.gld.mock.MockStatistics;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandLineConsoleTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CommandLineConsoleTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void stopTheMultiThreadRunnerOnQ() throws Exception
    {
        MockMultiThreadRunner mmtr = new MockMultiThreadRunner();
        CommandLineConsole commandLineConsole = new CommandLineConsole(mmtr);

        byte[] buffer = new byte[10];
        buffer[0] = 'q';
        buffer[1] = '\n';
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        commandLineConsole.setIn(bais);

        commandLineConsole.start();

        mmtr.waitToBeStopped();
    }

    @Test
    public void stopTheConsoleButNotTheMultiRunner() throws Exception
    {
        MockMultiThreadRunner mmtr = new MockMultiThreadRunner();
        assertTrue(mmtr.isRunning());

        CommandLineConsole commandLineConsole = new CommandLineConsole(mmtr);

        byte[] buffer = new byte[10];
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        commandLineConsole.setIn(bais);

        commandLineConsole.start();

        commandLineConsole.stop();

        // makes sure the runner is not stopped

        assertTrue(mmtr.isRunning());
    }

    @Test
    public void stopTheConsoleBySendingBgOnTheInputStream() throws Exception
    {
        MockStatistics ms = new MockStatistics();
        MockMultiThreadRunner mmtr = new MockMultiThreadRunner(ms);
        assertTrue(mmtr.isRunning());

        CommandLineConsole commandLineConsole = new CommandLineConsole(mmtr);

        byte[] buffer = new byte[10];


        // send something innocuous, will end up as a comment in statistics

        buffer[0] = 'b';
        buffer[1] = 'l';
        buffer[2] = 'a';
        buffer[3] = 'h';
        buffer[4] = '\n';
        buffer[5] = 'b';
        buffer[6] = 'g';
        buffer[7] = '\n';

        // then send to background

        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        commandLineConsole.setIn(bais);

        commandLineConsole.start();

        // busy loop until it stops
        while(true)
        {
            if (!commandLineConsole.isRunning())
            {
                break;
            }

            Thread.sleep(1L);
        }

        // makes sure the runner is not stopped

        assertTrue(mmtr.isRunning());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
