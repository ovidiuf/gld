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

package com.novaordis.gld.command;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

public class GenerateKeys extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int keySize;
    private int keyCount;
    private String fileName;

    // Constructors ----------------------------------------------------------------------------------------------------

    public GenerateKeys(Configuration c)
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception
    {
        keyCount = getKeyCount();

        Configuration c = getConfiguration();

        keySize = c.getKeySize();
        fileName = c.getKeyStoreFile();

        if (fileName == null)
        {
            throw new UserErrorException("a key store file name is required, use --key-store-file");
        }
    }

    @Override
    public boolean isInitialized()
    {
        return fileName != null;
    }

    @Override
    public boolean isRemote()
    {
        return false;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();

        System.out.println("generating " + keyCount + " " + keySize + "-character keys into " + fileName);

        Random random = new Random(System.currentTimeMillis());

        BufferedWriter bw = null;

        try
        {
            bw = new BufferedWriter(new FileWriter(fileName));

            for(int i = 0; i < keyCount; i ++)
            {
                bw.write(Util.getRandomKey(random, keySize));
                bw.newLine();
            }
        }
        finally
        {
            if (bw != null)
            {
                bw.close();
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    int getKeyCount() throws Exception
    {
        List<String> args = getArguments();

        if (args.isEmpty())
        {
            throw new UserErrorException("number of keys required");
        }

        String skc = args.get(0);

        try
        {
            return Integer.parseInt(skc);
        }
        catch(Exception e)
        {
            throw new UserErrorException("'" + skc + "' not a valid key count", e);
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
