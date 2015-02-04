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

package com.novaordis.cld;

import com.novaordis.ac.Handler;

import java.io.FileWriter;
import java.io.PrintWriter;

public class SampleHandler implements Handler
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // we don't want it to be buffered, we want to go to disk as soon as possible when a line is ready, we won't
    // block anything because the work is done on a dedicated thread
    private PrintWriter pw;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * May be null, and in this case send content to stdout.
     */
    public SampleHandler(String fileName) throws Exception
    {
        this(fileName == null ? new PrintWriter(System.out) : new PrintWriter(new FileWriter(fileName)));
    }

    SampleHandler(PrintWriter pw) throws Exception
    {
        this.pw = pw;
    }

    // Handler implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean canHandle(Object o)
    {
        return o != null && (o instanceof SamplingInterval || o instanceof String);
    }

    @Override
    public void handle(long timestamp, String threadName, Object o)
    {
        if (o instanceof String)
        {
            // generate an empty CSV line
            String line = SamplingInterval.
                toCsvLine(false, timestamp, null, null, null, null, null,
                    RedisFailure.NULL_COUNTERS, null, null, null, null, null, null, (String)o);

            pw.println(line);
        }
        else
        {
            pw.println(o);
        }

        pw.flush();
    }

    @Override
    public void close()
    {
        if (pw != null)
        {
            pw.close();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
