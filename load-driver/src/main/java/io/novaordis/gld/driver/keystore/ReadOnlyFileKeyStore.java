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

package io.novaordis.gld.driver.keystore;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.LoadDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation reads the entire key space in memory on startup and then keeps cycling through it.
 */

public class ReadOnlyFileKeyStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;
    private String fileName;

    private List<String> keys;

    private int currentKey;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ReadOnlyFileKeyStore(String fileName) throws Exception
    {
        this(fileName, new ArrayList<>());
    }

    /**
     * For testing only.
     */
    public ReadOnlyFileKeyStore(List<String> keys) throws Exception
    {
        this(null, keys);
    }

    private ReadOnlyFileKeyStore(String fileName, List<String> keys) throws Exception
    {
        this.fileName = fileName;
        this.keys = keys;
        currentKey = 0;
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void store(String key) throws Exception
    {
        throw new IllegalStateException("this is a read-only keystore, cannot store");
    }

//    @Override
    public String get()
    {
        if (keys.isEmpty())
        {
            return null;
        }

        synchronized (this)
        {
            String s = keys.get(currentKey);

            currentKey = (currentKey + 1) % keys.size();

            return s;
        }
    }

    @Override
    public void start() throws Exception
    {
        if (fileName == null && keys.size() > 0)
        {
            // already preloaded
            started = true;
        }
        else
        {
            File keyFile = new File(fileName);

            BufferedReader br = null;

            try
            {
                br = new BufferedReader(new FileReader(keyFile));

                String line;

                while ((line = br.readLine()) != null)
                {
                    keys.add(line);
                }

                currentKey = 0;

                System.out.println(keys.size() + " keys loaded in memory");

                started = true;
            }
            finally
            {
                if (br != null)
                {
                    br.close();
                }
            }
        }
    }

    @Override
    public void stop() throws Exception
    {
        if (started)
        {
            keys.clear();
            started = false;
            currentKey = 0;
        }
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return fileName + " (" + (started ? keys.size() + " keys" : "NOT STARTED") + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
