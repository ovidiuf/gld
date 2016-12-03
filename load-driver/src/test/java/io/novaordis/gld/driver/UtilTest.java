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

import io.novaordis.utilities.UserErrorException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    // command line processing utilities -------------------------------------------------------------------------------

    @Test
    public void extractOption_emptyLine() throws Exception
    {
        List<String> args = new ArrayList<>();
        String result = Util.extractOption("--something", true, args, -1);
        assertNull(result);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractOption_NoArgumentFound() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "blah", "--something-else", "blah"));
        String result = Util.extractOption("--no-such-thing", true, args, 0);
        assertNull(result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--something-else", args.get(2));
        assertEquals("blah", args.get(3));
    }

    @Test
    public void extractOption_ArgumentOnFromPosition_Boolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "blah", "--something-else", "blah"));
        String result = Util.extractOption("--something", true, args, 0);
        assertEquals("--something", result);
        assertEquals(3, args.size());
        assertEquals("blah", args.get(0));
        assertEquals("--something-else", args.get(1));
        assertEquals("blah", args.get(2));
    }

    @Test
    public void extractOption_ArgumentOnFromPosition_String() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "blah", "--something-else", "blah"));
        String result = Util.extractOption("--something", false, args, 0);
        assertEquals("blah", result);
        assertEquals(2, args.size());
        assertEquals("--something-else", args.get(0));
        assertEquals("blah", args.get(1));
    }

    @Test
    public void extractOption_ArgumentBetweenFromPositionAndEndOfList_Boolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last", "etc"));

        String result = Util.extractOption("--something-else", true, args, 0);
        assertEquals("--something-else", result);
        assertEquals(5, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("blah2", args.get(2));
        assertEquals("--last", args.get(3));
        assertEquals("etc", args.get(4));
    }

    @Test
    public void extractOption_ArgumentBetweenFromPositionAndEndOfList_String() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last", "etc"));

        String result = Util.extractOption("--something-else", false, args, 0);
        assertEquals("blah2", result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--last", args.get(2));
        assertEquals("etc", args.get(3));
    }

    @Test
    public void extractOption_ArgumentOnTheLastPositionInList_Boolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last"));

        String result = Util.extractOption("--last", true, args, 0);
        assertEquals("--last", result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--something-else", args.get(2));
        assertEquals("blah2", args.get(3));
    }

    @Test
    public void extractOption_ArgumentOnTheLastPositionInList_String() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last", "etc"));

        String result = Util.extractOption("--last", false, args, 0);
        assertEquals("etc", result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--something-else", args.get(2));
        assertEquals("blah2", args.get(3));
    }

    @Test
    public void extractOption_OptionFollowsImmediatelyAfterTheStringOption_Boolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        String result = Util.extractOption("--something", true, args, 0);
        assertEquals("--something", result);

        assertEquals(1, args.size());
        assertEquals("--something-else", args.get(0));
    }

    @Test
    public void extractOption_OptionFollowsImmediatelyAfterTheStringOption_String() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        try
        {
            Util.extractOption("--something", false, args, 0);
            fail("should fail as another option follows immediately after a string option");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void extractOption_OptionOnTheLastPosition_Boolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        String result = Util.extractOption("--something-else", true, args, 0);
        assertEquals("--something-else", result);

        assertEquals(1, args.size());
        assertEquals("--something", args.get(0));
    }

    @Test
    public void extractOption_OptionOnTheLastPosition_String() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        try
        {
            Util.extractOption("--something-else", false, args, 0);
            fail("should fail as another option is the last position in the list");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void extractString_emptyLine() throws Exception
    {
        List<String> args = new ArrayList<>();
        String result = Util.extractString("--something", args, -1);
        assertNull(result);
        assertTrue(args.isEmpty());
    }

    @Test
    public void extractString_NoArgumentFound() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "blah", "--something-else", "blah"));
        String result = Util.extractString("--no-such-thing", args, 0);
        assertNull(result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--something-else", args.get(2));
        assertEquals("blah", args.get(3));
    }

    @Test
    public void extractString_ArgumentOnFromPosition() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "blah", "--something-else", "blah"));
        String result = Util.extractString("--something", args, 0);
        assertEquals("blah", result);
        assertEquals(2, args.size());
        assertEquals("--something-else", args.get(0));
        assertEquals("blah", args.get(1));
    }

    @Test
    public void extractString_ArgumentBetweenFromPositionAndEndOfList() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last", "etc"));

        String result = Util.extractString("--something-else", args, 0);
        assertEquals("blah2", result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--last", args.get(2));
        assertEquals("etc", args.get(3));
    }

    @Test
    public void extractString_ArgumentOnTheLastPositionInList() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--something-else", "blah2", "--last", "etc"));

        String result = Util.extractString("--last", args, 0);
        assertEquals("etc", result);
        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--something-else", args.get(2));
        assertEquals("blah2", args.get(3));
    }

    @Test
    public void extractString_OptionFollowsImmediatelyAfterTheStringOption() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        try
        {
            Util.extractString("--something", args, 0);
            fail("should fail as another option follows immediately after a string option");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void extractString_OptionOnTheLastPosition() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--something", "--something-else"));

        try
        {
            Util.extractString("--something-else", args, 0);
            fail("should fail as another option is the last position in the list");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void extractString_OutOfBounds() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--a", "a", "--b", "b", "--c", "c", "--d", "d"));

        assertNull(Util.extractString("--a", args, 3));

        assertEquals(8, args.size());
    }

    @Test
    public void extractString_succession() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--a", "a", "--b", "b", "--c", "c", "--d", "d"));

        int from = 2;

        assertNull(Util.extractString("--a", args, from));

        assertEquals(8, args.size());

        assertEquals("b", Util.extractString("--b", args, from));

        assertEquals(6, args.size());
        assertEquals("--a", args.get(0));
        assertEquals("a", args.get(1));
        assertEquals("--c", args.get(2));
        assertEquals("c", args.get(3));
        assertEquals("--d", args.get(4));
        assertEquals("d", args.get(5));

        assertEquals("d", Util.extractString("--d", args, from));

        assertEquals(4, args.size());
        assertEquals("--a", args.get(0));
        assertEquals("a", args.get(1));
        assertEquals("--c", args.get(2));
        assertEquals("c", args.get(3));

        assertEquals("c", Util.extractString("--c", args, from));

        assertEquals(2, args.size());
        assertEquals("--a", args.get(0));
        assertEquals("a", args.get(1));

        assertNull(Util.extractString("--e", args, from));

        assertEquals(2, args.size());
        assertEquals("--a", args.get(0));
        assertEquals("a", args.get(1));
    }

    @Test
    public void extractBoolean() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--some-flag", "blah2", "--last", "etc"));

        assertTrue(Util.extractBoolean("--some-flag", args, 0));

        assertEquals(5, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("blah2", args.get(2));
        assertEquals("--last", args.get(3));
        assertEquals("etc", args.get(4));

        assertFalse(Util.extractBoolean("--does-not-exist", args, 0));
        assertEquals(5, args.size());
    }

    @Test
    public void extractLong() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--some-flag", "blah2", "--last", "5"));

        Long l = Util.extractLong("--last", args, 0);

        assertEquals(5L, l.longValue());

        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--some-flag", args.get(2));
        assertEquals("blah2", args.get(3));

        assertNull(Util.extractLong("--does-not-exist", args, 0));

        assertEquals(4, args.size());
    }

    @Test
    public void extractLong_InvalidNumber() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--some-flag", "blah2", "--last", "thisisnotaLong"));

        try
        {
            Util.extractLong("--last", args, 0);
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof NumberFormatException);
        }
    }

    @Test
    public void extractInteger() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--some-flag", "blah2", "--last", "5"));

        Integer i = Util.extractInteger("--last", args, 0);

        assertEquals(5, i.intValue());

        assertEquals(4, args.size());
        assertEquals("--something", args.get(0));
        assertEquals("blah", args.get(1));
        assertEquals("--some-flag", args.get(2));
        assertEquals("blah2", args.get(3));

        assertNull(Util.extractLong("--does-not-exist", args, 0));

        assertEquals(4, args.size());
    }

    @Test
    public void extractInteger_InvalidNumber() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList(
            "--something", "blah", "--some-flag", "blah2", "--last", "thisisnotanInt"));

        try
        {
            Util.extractInteger("--last", args, 0);
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof NumberFormatException);
        }
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
