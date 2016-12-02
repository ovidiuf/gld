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

package io.novaordis.gld.driver.console;

import io.novaordis.gld.driver.MockMultiThreadRunner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CommandLineConsoleTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CommandLineConsoleTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void stopTheMultiThreadRunnerOnQ() throws Exception
    {
        MockMultiThreadRunner mmtr = new MockMultiThreadRunner();
        CommandLineConsole commandLineConsole = new CommandLineConsole(mmtr, null);

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

        CommandLineConsole commandLineConsole = new CommandLineConsole(mmtr, null);

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
        MockMultiThreadRunner mockMultiThreadRunner = new MockMultiThreadRunner();
        assertTrue(mockMultiThreadRunner.isRunning());

        CommandLineConsole commandLineConsole = new CommandLineConsole(mockMultiThreadRunner,  null);

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

        assertTrue(mockMultiThreadRunner.isRunning());
    }

    // stopping unblocks reading on stdin ------------------------------------------------------------------------------

    @Test
    public void stoppingUnblocksReadOnStdin() throws Exception
    {
        MockMultiThreadRunner mockMultiThreadRunner = new MockMultiThreadRunner();
        CommandLineConsole commandLineConsole = new CommandLineConsole(mockMultiThreadRunner, null);

        //noinspection MismatchedQueryAndUpdateOfCollection
        final BlockingQueue<Object> checkPointOne = new ArrayBlockingQueue<>(1);
        //noinspection MismatchedQueryAndUpdateOfCollection
        final BlockingQueue<Integer> checkPointTwo = new ArrayBlockingQueue<>(1024);

        InputStream mockInputStream = new InputStream()
        {
            final private AtomicInteger invocationCounter = new AtomicInteger(0);

            @Override
            public int read() throws IOException
            {
                try
                {
                    if (invocationCounter.get() == 0)
                    {
                        checkPointOne.put("Console Started To Read");
                    }

                    log.info("waiting to 'read' (invocation " +
                        invocationCounter.getAndIncrement() + ") from checkPointTwo ..." );

                    // this will block the console thread
                    int i  = checkPointTwo.take();

                    log.info("did 'read' " + (char)i  + " from checkPointTwo, sending it to the console");
                    return i;

                }
                catch(InterruptedException e)
                {
                    throw new IllegalStateException(e);
                }
            }
        };

        // install the mock input stream
        commandLineConsole.setIn(mockInputStream);

        log.info("starting console ...");

        commandLineConsole.start();

        log.info("console started");

        // the internal console thread will be started and begin reading from the console input stream

        // make sure a read attempt on the stream is made

        assertEquals("Console Started To Read", checkPointOne.take());

        // this will attempt to stop the console while the reading thread is blocked

        log.info("stopping console ...");

        commandLineConsole.stop();

        log.info("console stopped");

        // unblock the reading thread by sending a '\n', which will send content into the console

        log.info("unlocking the reading thread multiple times by putting ints in checkPointTwo ...");

        checkPointTwo.put((int)'b');
        checkPointTwo.put((int)'l');
        checkPointTwo.put((int)'a');
        checkPointTwo.put((int)'h');
        checkPointTwo.put((int) '\n');

        log.info("unlocked the reading thread");

        // make sure the console is stopped

        assertTrue(!commandLineConsole.isRunning());

        log.info("console is not running");

        // make sure no empty line was processed by the console, which was supposed to discard everyhting
        // coming from the stream

        String lastReadLine = commandLineConsole.getLastReadLine();
        assertNull(lastReadLine);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
