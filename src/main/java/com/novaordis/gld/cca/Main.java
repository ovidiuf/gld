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

package com.novaordis.gld.cca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception
    {
        Map<String, String> pairs = new HashMap<>();
        File f = new File(args[0]);
        BufferedReader br = null;
        int counter = 0;

        try
        {
            br = new BufferedReader(new FileReader(f));

            String line;
            StringBuilder sb = null;
            String key = null;

            while((line = br.readLine()) != null)
            {
                if (line.matches("^.*=\\{"))
                {
                    counter ++;

                    if (sb != null)
                    {
                        // wrap up previous value
                        pairs.put(key, sb.toString());

                    }

                    int i = line.indexOf("=");

                    if (line.indexOf("=", i + 1) != -1)
                    {
                        throw new Exception("two '=' on line " + line);
                    }

                    key = line.substring(0, i);
                    sb = new StringBuilder();
                    sb.append(line.substring(i + 1));
                }
                else if (sb != null)
                {
                    sb.append(line).append("\n");
                }
                else
                {
                    System.out.println("WARN: line not preceded by a key: " + line);
                }
            }

            if (sb != null)
            {
                // wrap up the last key/value pair
                pairs.put(key, sb.toString());
            }
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
        }

        System.out.println(pairs.size());


        f = new File("result.csv");
        PrintWriter pw = null;

        try
        {
            pw = new PrintWriter(new FileWriter(f));
            pw.println("key, key size, value size");

            for(String key: pairs.keySet())
            {
                String value = pairs.get(key);
                pw.println(key + ", " + key.length() + ", " + value.length());
            }
        }
        finally
        {
            if (pw != null)
            {
                pw.close();
            }
        }


    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
