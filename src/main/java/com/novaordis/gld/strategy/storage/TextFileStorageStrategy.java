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

package com.novaordis.gld.strategy.storage;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.UserErrorException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class TextFileStorageStrategy extends StorageStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final int BUFFER_SIZE = 10240;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String fileName;

    private PrintWriter pw;

    // Constructors ----------------------------------------------------------------------------------------------------

    // StorageStrategy implementation ----------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.StorageStrategy#configure(Configuration, List, int)
     */
    @Override
    public void configure(Configuration config, List<String> arguments, int from) throws Exception
    {
        super.configure(config, arguments, from);

        for(int i = from; i < arguments.size(); i ++)
        {
            String crt = arguments.get(i);

            if ("--output".equals(crt))
            {
                arguments.remove(crt);

                if (i >= arguments.size())
                {
                    throw new UserErrorException("a file name must follow --output");
                }

                fileName = arguments.remove(i);
            }
        }

        if (fileName == null)
        {
            throw new UserErrorException("TextFile storage strategy invalid configuration: missing output file name, use --output <file-name>");
        }
    }

    @Override
    public boolean isConfigured()
    {
        return fileName != null;
    }

    /**
     * @see com.novaordis.gld.StorageStrategy#start()
     */
    @Override
    public void start() throws Exception
    {
        if (!isConfigured())
        {
            throw new IllegalStateException(this + " not configured");
        }

        pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName), BUFFER_SIZE));
    }

    @Override
    public void stop() throws Exception
    {
        if (pw != null)
        {
            pw.close();
            pw = null;
        }
    }

    @Override
    public boolean isStarted()
    {
        return pw != null;
    }

    /**
     * @see com.novaordis.gld.StorageStrategy#store(String, String)
     */
    @Override
    public void store(String key, String value) throws Exception
    {
        pw.println(key + "=" + value);
    }

    /**
     * @see com.novaordis.gld.StorageStrategy#retrieve(String)
     */
    @Override
    public String retrieve(String key) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Set<String> getKeys() throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String toString()
    {
        return "TextFileStorageStrategy[" + fileName + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------


}
