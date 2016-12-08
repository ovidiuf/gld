/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class RandomContentGeneratorTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(RandomContentGeneratorTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // getRandomString() shorter sequences -----------------------------------------------------------------------------

    @Test
    public void getRandomString_InvalidRandomLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        try {

            g.getRandomString(r, 3, 0);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void getRandomString_InvalidTotalLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        try {

            g.getRandomString(r, 0, 3);
            fail("should fail with IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {

            log.info(e.getMessage());
        }
    }

    @Test
    public void getRandomString_EqualTotalLengthRandomSectionLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        String s = g.getRandomString(r, 3, 3);
        log.info(s);
        assertEquals(3, s.length());
    }

    @Test
    public void getRandomString_TotalLengthMultipleOfRandomSectionLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        String s = g.getRandomString(r, 9, 3);

        log.info(s);

        assertEquals(9, s.length());

        String section1 = s.substring(0, 3);
        String section2 = s.substring(3, 6);
        String section3 = s.substring(6);

        assertEquals(section1, section2);
        assertEquals(section2, section3);
    }

    @Test
    public void getRandomString_TotalLengthNotMultipleOfRandomSectionLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        String s = g.getRandomString(r, 11, 3);

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
    public void getRandomString_LongString() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        long t0 = System.currentTimeMillis();

        String s = g.getRandomString(r, 512000, 3);

        long t1 = System.currentTimeMillis();

        assertNotNull(s);

        log.info(t1 - t0 + " ms");
    }

    @Test
    public void getRandomString_TotalLengthSmallerThanMultipleOfRandomSectionLength() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random(System.currentTimeMillis());

        String s = g.getRandomString(r, 3, 11);

        log.info(s);

        assertEquals(3, s.length());
    }

    // getRandomString() -----------------------------------------------------------------------------------------------

    @Test
    public void getRandomString_InvalidKeyValue() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random();

        try {

            g.getRandomString(r, 0);
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("invalid length .*"));
        }
    }

    @Test
    public void getRandomString_InvalidKeyValue2() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random();

        try {

            g.getRandomString(r, -1);
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("invalid length .*"));
        }
    }

    @Test
    public void getRandomString() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random();

        String s = g.getRandomString(r, 1);

        assertEquals(1, s.length());
    }

    @Test
    public void getRandomString_10() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random();

        String s = g.getRandomString(r, 10);

        assertEquals(10, s.length());
    }

    @Test
    public void getRandomString_2000() throws Exception {

        RandomContentGenerator g = new RandomContentGenerator();
        Random r = new Random();

        String s = g.getRandomString(r, 2000);

        assertEquals(2000, s.length());
    }

//    @Test
//    public void getRandomKeyUUID_35() throws Exception
//    {
//        String s = Util.getRandomKeyUUID(35);
//        assertEquals(35, s.length());
//    }
//
//    @Test
//    public void getRandomKeyUUID_36() throws Exception
//    {
//        String s = Util.getRandomKeyUUID(36);
//        assertEquals(36, s.length());
//    }
//
//    @Test
//    public void getRandomKeyUUID_37() throws Exception
//    {
//        String s = Util.getRandomKeyUUID(37);
//        assertEquals(37, s.length());
//    }
//
//    @Test
//    public void getRandomKey() throws Exception
//    {
//        for(int i = 0; i < 1000; i ++)
//        {
//            int length = 150;
//            String s = Util.getRandomKey(ThreadLocalRandom.current(), length);
//
//            for(int j = 0; j < length; j ++)
//            {
//                char c = s.charAt(j);
//
//                assertTrue((c >=48 && c <=57) || (c >=65 && c <=90) || (c >=97 && c <=122));
//            }
//        }
//    }



    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
