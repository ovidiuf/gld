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

import com.novaordis.cld.mock.MockInterface;
import com.novaordis.cld.mock.MockInterface2;
import com.novaordis.cld.mock.mockpackage.WinningStrategy;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class UtilTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(UtilTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void getRandomKeyUUID_35() throws Exception
    {
        String s = Util.getRandomKeyUUID(35);
        assertEquals(35, s.length());
    }

    @Test
    public void getRandomKeyUUID_36() throws Exception
    {
        String s = Util.getRandomKeyUUID(36);
        assertEquals(36, s.length());
    }

    @Test
    public void getRandomKeyUUID_37() throws Exception
    {
        String s = Util.getRandomKeyUUID(37);
        assertEquals(37, s.length());
    }

    @Test
    public void getRandomKey() throws Exception
    {
        for(int i = 0; i < 1000; i ++)
        {
            int length = 150;
            String s = Util.getRandomKey(ThreadLocalRandom.current(), length);

            for(int j = 0; j < length; j ++)
            {
                char c = s.charAt(j);

                assertTrue((c >=48 && c <=57) || (c >=65 && c <=90) || (c >=97 && c <=122));
            }
        }
    }

    // getRandomString() -----------------------------------------------------------------------------------------------

    @Test
    public void getRandomString_EqualTotalLengthRandomSectionLength() throws Exception
    {
        String s = Util.getRandomString(new Random(System.currentTimeMillis()), 3, 3);
        log.info(s);
        assertEquals(3, s.length());
    }

    @Test
    public void getRandomString_TotalLengthMultipleOfRandomSectionLength() throws Exception
    {
        String s = Util.getRandomString(new Random(System.currentTimeMillis()), 9, 3);
        log.info(s);
        assertEquals(9, s.length());
        String section1 = s.substring(0, 3);
        String section2 = s.substring(3, 6);
        String section3 = s.substring(6);

        assertEquals(section1, section2);
        assertEquals(section2, section3);
    }

    @Test
    public void getRandomString_TotalLengthNotMultipleOfRandomSectionLength() throws Exception
    {
        String s = Util.getRandomString(new Random(System.currentTimeMillis()), 11, 3);
        log.info(s);
        assertEquals(11, s.length());
        String section1 = s.substring(0, 3);
        String section2 = s.substring(3, 6);
        String section3 = s.substring(6, 9);
        String section4 = s.substring(9);

        assertEquals(section1, section2);
        assertEquals(section2, section3);
        assertTrue(section3.startsWith(section4));
    }

    @Test
    public void getRandomString_LongString() throws Exception
    {
        long t0 = System.currentTimeMillis();
        String s = Util.getRandomString(new Random(System.currentTimeMillis()), 512000, 3);
        assertNotNull(s);
        long t1 = System.currentTimeMillis();

        log.info(t1 - t0 + " ms");
    }

    @Test
    public void getRandomString_TotalLengthSmallerThanMultipleOfRandomSectionLength() throws Exception
    {
        String s = Util.getRandomString(new Random(System.currentTimeMillis()), 3, 11);
        log.info(s);
        assertEquals(3, s.length());
    }

    @Test
    public void getRandomString_InvalidRandomLength() throws Exception
    {
        try
        {
            Util.getRandomString(new Random(System.currentTimeMillis()), 3, 0);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getRandomString_InvalidTotalLength() throws Exception
    {
        try
        {
            Util.getRandomString
                (new Random(System.currentTimeMillis()), 0, 3);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    // getInstance() tests ---------------------------------------------------------------------------------------------

    @Test
    public void getInstance_WrongPackageName() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.nosuchpackate", "Winning", "Strategy");
            fail("should have failed with IllegalArgumentException - wrong package name");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassNotFoundException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongBaseName() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "winning", "Strategy");
            fail("should have failed with IllegalArgumentException - wrong base name");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof NoClassDefFoundError);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongSuffix() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "Winning", "Trickery");
            fail("should have failed with IllegalArgumentException - wrong suffix");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassNotFoundException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_WrongType() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface2.class, "com.novaordis.cld.mock.mockpackage", "Winning", "Strategy");
            fail("should have failed with IllegalArgumentException - wrong suffix");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof ClassCastException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_NoNoArgumentConstructor() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "NoNoArgConstructor", "Strategy");
            fail("should have failed with IllegalArgumentException - no no-argument constructor");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof InstantiationException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance_PrivateNoArgumentConstructor() throws Exception
    {
        try
        {
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "PrivateNoArgConstructor", "Strategy");
            fail("should have failed with IllegalArgumentException - no no-argument constructor");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof IllegalAccessException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void getInstance() throws Exception
    {
        MockInterface o =
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "Winning", "Strategy");

        WinningStrategy ws = (WinningStrategy)o;

        assertNotNull(ws);
    }

    @Test
    public void getInstance_EmptySuffix() throws Exception
    {
        MockInterface o =
            Util.getInstance(MockInterface.class, "com.novaordis.cld.mock.mockpackage", "WinningStrategy", "");

        WinningStrategy ws = (WinningStrategy)o;

        assertNotNull(ws);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
