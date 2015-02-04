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

import com.novaordis.gld.CacheService;
import com.novaordis.gld.Configuration;
import org.infinispan.client.hotrod.RemoteCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.StringTokenizer;


public class Connect extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Connect(Configuration c)
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean isInitialized()
    {
        return true;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();

        CacheService cs = getConfiguration().getCacheService();
        cs.start();
        RemoteCache<String, String> rc = (RemoteCache<String, String>)cs.getCache();
        cliLoop(rc);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void cliLoop(RemoteCache<String, String> cache) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(true)
        {
            System.out.print("> ");

            String line = br.readLine();

            if (line.trim().length() == 0)
            {
                continue;
            }

            StringTokenizer st = new StringTokenizer(line, " ");
            String command = st.nextToken();


            if ("exit".equals(command) || "quit".equals(command))
            {
                break;
            }

            if ("size".equals(command))
            {
                int size = cache.size();
                System.out.println("> " + size);
            }
            else if ("put".equals(command))
            {
                if (!st.hasMoreTokens())
                {
                    System.err.println("need a key and a value");
                    continue;
                }
                String key = st.nextToken();
                if (!st.hasMoreTokens())
                {
                    System.err.println("need a value");
                    continue;
                }
                String value = st.nextToken();
                cache.put(key, value);
            }
            else if ("get".equals(command))
            {
                if (!st.hasMoreTokens())
                {
                    System.err.println("need a key ");
                    continue;
                }
                String key = st.nextToken();
                String value = cache.get(key);

                System.out.println("> " + value);
            }
            else if ("keys".equals(command))
            {
                Set<String> keys = cache.keySet();

                for(String k: keys)
                {
                    System.out.println(k);
                }
            }
            else
            {
                System.out.println("> unknown command");
            }
        }

        br.close();
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
