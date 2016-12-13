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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.RandomContentGenerator;
import io.novaordis.utilities.UserErrorException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Util {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Util.class);

    private static int UUID_STRING_SIZE = UUID.randomUUID().toString().length();

    // Static ----------------------------------------------------------------------------------------------------------

    private static final Map<Integer, String> VALUE_CACHE = new HashMap<>();

    public static String formatErrorMessage(String msg) {

        return "[error]: " + msg;
    }

    public static String formatErrorMessage(Throwable t) {

        if (t instanceof UserErrorException) {

            return t.getMessage();
        }

        String msg = "internal error: " + t.getClass().getSimpleName();
        String m = t.getMessage();
        if (m != null) {

            msg += " (" + m + "), consult the log for more details";
        }

        return msg;
    }

    public static String getRandomKey(Random random, int keySize) {

        return new RandomContentGenerator().getRandomString(random, keySize, keySize);
    }

    /**
     * Slower than getRandomString(keySize).
     */
    public static String getRandomKeyUUID(int keySize) {
        String result = "";

        int uuids = keySize / UUID_STRING_SIZE;

        for(int i = 0; i < uuids; i ++)
        {
            result += UUID.randomUUID();
        }

        int rest = keySize - (UUID_STRING_SIZE * uuids);

        if (rest > 0)
        {
            String s = UUID.randomUUID().toString();
            result += s.substring(0, rest);
        }

        return result;
    }

    public static void displayContentFromClasspath(String fileName) {
        BufferedReader br = null;

        try
        {
            InputStream is = Util.class.getClassLoader().getResourceAsStream(fileName);

            if (is != null)
            {
                br = new BufferedReader(new InputStreamReader(is));

                String line;
                while((line = br.readLine()) != null)
                {
                    System.out.println(line);
                }
            }

            return;
        }
        catch(Exception e)
        {
            // swallow for the time being
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("failed to load the '" + fileName + "' file content from classpath");
    }

    /**
     * TODO put this in NovaOrdis Utilities
     */
    public static String threadDump() {

        final StringBuilder sb = new StringBuilder();

        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo((threadMXBean.getAllThreadIds()));
        for(ThreadInfo ti: threadInfos)
        {
            sb.append('"');
            sb.append(ti.getThreadName());
            sb.append("\" ");

            final Thread.State state = ti.getThreadState();
            sb.append("\n java.lang.Thread.State: ");
            sb.append(state);

            final StackTraceElement[] stackTraceElements = ti.getStackTrace();
            for(final StackTraceElement ste: stackTraceElements)
            {
                sb.append("\n      at ");
                sb.append(ste);
            }

            sb.append("\n\n");
        }

        return sb.toString();
    }

    /**
     * TODO put this in NovaOrdis Utilities
     */
    public static void nativeThreadDump() {
        // next pid

        String s = ManagementFactory.getRuntimeMXBean().getName();

        int i = s.indexOf('@');

        String pid = s.substring(0, i);

        Runtime runtime = Runtime.getRuntime();

        try
        {
            runtime.exec("kill -3 " + pid);
        }
        catch(Exception e)
        {
            log.error("failed to take thread dump", e);
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
