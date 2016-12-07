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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class UtilTest extends Assert {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(UtilTest.class);

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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
