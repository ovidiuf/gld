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

package com.novaordis.cld.strategy.load;

import com.novaordis.cld.UserErrorException;
import com.novaordis.cld.mock.MockConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LoadStrategyFactoryTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LoadStrategyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void missingLoadStrategyOption() throws Exception
    {
        List<String> args = Arrays.asList("blah", "blah");

        try
        {
            LoadStrategyFactory.fromArguments(new MockConfiguration(), args, 1);
            fail("should fail with IllegalArgumentException, --load-strategy missing at the given position");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void missingLoadStrategyName() throws Exception
    {
        List<String> args = new ArrayList<>(Arrays.asList("--load-strategy"));

        try
        {
            LoadStrategyFactory.fromArguments(new MockConfiguration(), args, 0);
            fail("should fail with IllegalArgumentException, --load-strategy is on the last position");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());

            Throwable t = e.getCause();

            assertTrue(t instanceof NullPointerException);

            log.info(t.getMessage());
        }
    }

    @Test
    public void upperCaseStrategyName() throws Exception
    {
        String name = "Mock";

        List<String> args = new ArrayList<>(Arrays.asList(
            "something", "--load-strategy", name, "--mock-argument", "blah", "something-else"));

        MockLoadStrategy mls = (MockLoadStrategy)LoadStrategyFactory.fromArguments(new MockConfiguration(), args, 1);

        assertNotNull(mls);

        assertEquals("blah", mls.getMockArgument());

        assertEquals(2, args.size());
        assertEquals("something", args.get(0));
        assertEquals("something-else", args.get(1));
    }

    @Test
    public void lowerCaseStrategyName() throws Exception
    {
        String name = "mock";

        List<String> args = new ArrayList<>(Arrays.asList(
            "something", "--load-strategy", name, "--mock-argument", "blah", "something-else"));

        MockLoadStrategy mls = (MockLoadStrategy)LoadStrategyFactory.fromArguments(new MockConfiguration(), args, 1);

        assertNotNull(mls);

        assertEquals("blah", mls.getMockArgument());

        assertEquals(2, args.size());
        assertEquals("something", args.get(0));
        assertEquals("something-else", args.get(1));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
