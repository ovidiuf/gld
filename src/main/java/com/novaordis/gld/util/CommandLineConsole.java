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

import com.novaordis.gld.MultiThreadedRunner;
import com.novaordis.gld.Util;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandLineConsole implements Runnable
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final Logger log = Logger.getLogger(CommandLineConsole.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Thread thread;
    private BufferedReader inBufferedReader;
    private volatile boolean running;
    private MultiThreadedRunner multiThreadedRunner;
    private String line;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CommandLineConsole(MultiThreadedRunner multiThreadedRunner)
    {
        this.multiThreadedRunner = multiThreadedRunner;
        this.thread = new Thread(this, "gld command line console");
        thread.setDaemon(true);
        setIn(System.in);
    }

    // Runnable implementation -----------------------------------------------------------------------------------------

    public void run()
    {
        try
        {
            while (running)
            {
                System.out.print("['q'|'td'|comment]> ");

                try
                {
                    line = inBufferedReader.readLine();
                }
                catch (IOException e)
                {
                    // we force stop by closing the stream, so don't surface that

                    String msg = e.getMessage();

                    if ("Stream closed".equals(msg))
                    {
                        // we're good, noop
                        log.debug("input stream closed");
                    }
                    else
                    {
                        // other kind of console failure
                        log.error("console read failed", e);
                    }

                    return;
                }

                if (!running)
                {
                    // this means the console was closed concurrently from another thread while the reader
                    // thread was blocked in reading; since the console was closed explicitly, we are not
                    // interested in content, so we discard it
                    return;
                }

                if (line == null)
                {
                    log.error("console returned null");
                    return;
                }

                if ("q".equals(line.toLowerCase()))
                {
                    //
                    // 'q' (quit)
                    //

                    try
                    {
                        multiThreadedRunner.stop();
                    }
                    catch (Exception e)
                    {
                        log.error("failed to stop the multi-threaded runner", e);
                    }
                    return;
                }
                else if ("td".equals(line.toLowerCase()))
                {
                    //
                    // 'td' - thread dump
                    //
                    Util.nativeThreadDump();
                    return;
                }
                else if ("bg".equals(line.toLowerCase()))
                {
                    //
                    // 'bg' - background; stops the console but leaves the runner running
                    //

                    try
                    {
                        inBufferedReader.close();
                    }
                    catch (Exception e)
                    {
                        log.warn("failed to close the input stream", e);
                    }
                    return;
                }

                // only send non-empty content to the log
                line = line.trim();

                if (!line.isEmpty())
                {
                    multiThreadedRunner.getStatistics().annotate(line);
                }
            }
        }
        finally
        {
            running = false;
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void start()
    {
        running = true;
        thread.start();
        log.debug(thread + " started");
    }

    public void stop()
    {
        // if the console reading thread is attempting to read from the buffered, it acquires an internal
        // monitor that won't allow this tread to do anything, so an attempt to close the inBufferReader
        // from this thread will also block; we simply mark this console as closed, which means that
        // anything coming from the underlying input stream will be discarded, and attempt to close the underlying thread asynchronously, so stop() can
        // complete

        running = false;

        Thread closer = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    inBufferedReader.close();
                }
                catch(Exception e)
                {
                    log.debug(e);
                }
            }
        }, "gld command line console closer thread");
        closer.setDaemon(true);
        closer.start();
    }

    boolean isRunning()
    {
        return running;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void setIn(InputStream is)
    {
        inBufferedReader = new BufferedReader(new InputStreamReader(is));
    }

    /**
     * May return null
     */
    String getLastReadLine()
    {
        return line;
    }


    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
