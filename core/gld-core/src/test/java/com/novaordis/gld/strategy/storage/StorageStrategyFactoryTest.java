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

package com.novaordis.gld.strategy.storage;

import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StorageStrategyFactoryTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(StorageStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void fromArguments_NoStorageStrategyFlag() throws Exception
    {
        try
        {
            StorageStrategyFactory.fromArguments(new MockConfiguration(), Arrays.asList("blah"), 0);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void missingStrategy() throws Exception
    {
        List<String> args = new ArrayList<>();
        args.add("--storage-strategy");

        try
        {
            StorageStrategyFactory.fromArguments(new MockConfiguration(), args, 0);
            fail("should fail with UserErrorException, missing storage strategy");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof NullPointerException);
        }
    }

    @Test
    public void unknownStrategy() throws Exception
    {
        List<String> args = new ArrayList<>();
        args.add("--storage-strategy");
        args.add("NoSuchStrategy");

        try
        {
            StorageStrategyFactory.fromArguments(new MockConfiguration(), args, 0);
            fail("should fail with UserErrorException, no such storage strategy");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable cause = e.getCause();
            assertNotNull(cause);
        }
    }

    @Test
    public void strategy_stdout() throws Exception
    {
        List<String> args = new ArrayList<>();
        args.add("--storage-strategy");
        args.add("stdout");

        StdoutStorageStrategy ss =
            (StdoutStorageStrategy)StorageStrategyFactory.fromArguments(new MockConfiguration(), args, 0);
        assertNotNull(ss);
    }

    @Test
    public void strategy_Stdout() throws Exception
    {
        List<String> args = new ArrayList<>();
        args.add("--storage-strategy");
        args.add("Stdout");

        StdoutStorageStrategy ss =
            (StdoutStorageStrategy)StorageStrategyFactory.fromArguments(new MockConfiguration(), args, 0);
        assertNotNull(ss);
    }

    @Test
    public void makeSureAllRelevantArgumentsAreRemovedFromList() throws Exception
    {
        List<String> args = new ArrayList<>();
        args.add("blah");
        args.add("--storage-strategy");
        args.add("Mock");
        args.add("--mock-argument");
        args.add("something");
        args.add("post-argument");

        MockStorageStrategy mss =
            (MockStorageStrategy)StorageStrategyFactory.fromArguments(new MockConfiguration(), args, 1);
        assertNotNull(mss);

        assertEquals("something", mss.getMockArgument());
        assertEquals(2, args.size());
        assertEquals("blah", args.get(0));
        assertEquals("post-argument", args.get(1));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
