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

    private MultiThreadedRunner multiThreadedRunner;
    private BufferedReader in;
    private Thread thread;
    private volatile boolean running;
    private InputStream originalInputStream;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CommandLineConsole(MultiThreadedRunner multiThreadedRunner)
    {
        this.multiThreadedRunner = multiThreadedRunner;
        this.thread = new Thread(this, "gld command line console");
        setIn(System.in);
    }

    // Runnable implementation -----------------------------------------------------------------------------------------

    public void run()
    {
        String line;

        try
        {
            running = true;

            while (true)
            {
                System.out.print("['q'|message]> ");

                try
                {
                    line = in.readLine();
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
                else if ("bg".equals(line.toLowerCase()))
                {
                    //
                    // 'bg' - background; stops the console but leaves the runner running
                    //

                    try
                    {
                        in.close();
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
        thread.start();
    }

    public void stop()
    {
//        try
//        {
//            originalInputStream.close();
//        }
//        catch(Exception e)
//        {
//            log.debug(e);
//        }

        try
        {
            in.close();
        }
        catch(Exception e)
        {
            log.debug(e);
        }
    }

    boolean isRunning()
    {
        return running;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void setIn(InputStream is)
    {
        this.originalInputStream = is;
        in = new BufferedReader(new InputStreamReader(is));
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}