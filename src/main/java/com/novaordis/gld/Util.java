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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Util
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static int UUID_STRING_SIZE = UUID.randomUUID().toString().length();

    // Static ----------------------------------------------------------------------------------------------------------

    private static final Map<Integer, String> VALUE_CACHE = new HashMap<>();

    /**
     * Naive implementation - come up with something smarter.
     *
     * @param totalLength - the total length of the string.
     * @param randomSectionLength - the length of the random section of the string. If randomSectionLength
     *       is smaller than totalLength, the final string consists in identical repeated sections; the section that
     *       will be repeated is 'randomSectionLength' long and it is randomly generated.
     */
    public static String getRandomString(Random random, int totalLength, int randomSectionLength)
    {
        String randomSection = "";
        int r;

        if (totalLength < randomSectionLength)
        {
            randomSectionLength = totalLength;
        }

        if (randomSectionLength <= 0)
        {
            throw new IllegalArgumentException("invalid length " + randomSectionLength);
        }

        for (int i = 0; i < randomSectionLength; i ++)
        {
            r = random.nextInt(122);

            if (r >=0 && r <= 25)
            {
                r += 65;
            }
            else if (r >=26 && r <= 47)
            {
                r += 71;
            }
            else if (r >= 58 && r <= 64)
            {
                r += 10;
            }
            else if (r >= 91 && r <= 96)
            {
                r += 10;
            }
//            else if ((r >=48 && r <=57) || (r >=65 && r <=90) || (r >=97 && r <=122))
//            {
//                // nothing, we're good
//            }

            randomSection += ((char)r);
        }

        if (totalLength == randomSectionLength)
        {
            return randomSection;
        }
        else if (totalLength > randomSectionLength)
        {
            char[] src = randomSection.toCharArray();
            int sections = totalLength / randomSectionLength;
            char[] buffer = new char[totalLength];
            for(int i = 0; i < sections; i ++)
            {
                System.arraycopy(src, 0, buffer, i * randomSectionLength, randomSectionLength);
            }
            int rest = totalLength - sections * randomSectionLength;
            System.arraycopy(src, 0, buffer, sections * randomSectionLength, rest);
            return new String(buffer);
        }

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    public static String getRandomKey(Random random, int keySize)
    {
        return getRandomString(random, keySize, keySize);
    }

    /**
     * Slower than getRandomString(keySize).
     */
    public static String getRandomKeyUUID(int keySize)
    {
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


    public static String getRandomValue(Random random, int valueSize, boolean useDifferentValues)
    {
        synchronized (Util.class)
        {
            String s;

            if (!useDifferentValues)
            {
                s = VALUE_CACHE.get(valueSize);

                if (s == null)
                {
                    s = getRandomString(random, valueSize, 10);
                    VALUE_CACHE.put(valueSize, s);
                }
            }
            else
            {
                s = getRandomString(random, valueSize, 10);
            }

            return s;
        }

    }

    public static Throwable getRoot(Throwable t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("null throwable");
        }

        Throwable root = t;
        while (root.getCause() != null)
        {
            root = root.getCause();
        }

        return root;
    }

    public static void displayContentFromClasspath(String fileName)
    {
        BufferedReader br = null;

        try
        {
            InputStream is = Main.class.getClassLoader().getResourceAsStream(fileName);

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
     * Generic utility class that looks up classes on the classpath and tries to find
     * <package>.<nameRoot><suffix> that implements the given interfaceType and then instantiates it using a no-argument
     * constructor.
     *
     * @param interfaceType - the interface to be implemented by the returned class.
     * @param packageName - the package name (dot separated components) - must NOT end in dot.
     * @param nameRoot - the name root - capitalization matters.
     * @param suffix - the suffix - capitalization matters.
     * @param <T> - the interface to be implemented by the returned class.
     *
     * @exception IllegalArgumentException (with cause) when the class is not found or it cannot be instantiated.
     */
    public static <T> T getInstance(Class<T> interfaceType, String packageName, String nameRoot, String suffix)
        throws Exception
    {
        String fullyQualifiedClassName = packageName + "." + nameRoot + suffix;

        ClassLoader cl = Util.class.getClassLoader();

        Class<T> c;

        try
        {
            //noinspection unchecked
            c = (Class<T>)cl.loadClass(fullyQualifiedClassName);
        }
        catch(Throwable t)
        {
            throw new IllegalArgumentException("cannot find class " + t.getMessage(), t);
        }

        T result;

        try
        {
            result = c.newInstance();
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("class '" + fullyQualifiedClassName + "' failed to instantiate, most likely the class has no no-argument constructor or a private no-argument constructor: " + e.getMessage(), e);
        }

        if (!interfaceType.isAssignableFrom(c))
        {
            throw new IllegalArgumentException(fullyQualifiedClassName + " does not implement " + interfaceType,
                new ClassCastException(interfaceType.getName()));
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------


    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------


    // Inner classes ---------------------------------------------------------------------------------------------------

}
