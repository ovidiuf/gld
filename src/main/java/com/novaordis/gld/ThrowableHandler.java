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

import com.novaordis.ac.Handler;

import java.io.FileWriter;
import java.io.PrintWriter;

public class ThrowableHandler implements Handler
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private PrintWriter pw;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ThrowableHandler(String exceptionFileName) throws Exception
    {
        pw = new PrintWriter(new FileWriter(exceptionFileName));
    }

    // Handler implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean canHandle(Object o)
    {
        return o != null && (Throwable.class.isAssignableFrom(o.getClass()));
    }

    @Override
    public void handle(long timestamp, String threadName, Object o)
    {
        if (pw == null)
        {
            return;
        }

        Throwable t = (Throwable)o;

        try
        {
            pw.print(CollectorBasedStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) + ", " + threadName + ": ");
            t.printStackTrace(pw);
            pw.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void close()
    {
        if (pw != null)
        {
            try
            {
                pw.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
