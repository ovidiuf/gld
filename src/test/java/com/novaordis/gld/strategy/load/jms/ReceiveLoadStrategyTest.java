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

package com.novaordis.gld.strategy.load.jms;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Receive;
import com.novaordis.gld.strategy.load.cache.WriteThenReadLoadStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReceiveLoadStrategyTest extends JmsLoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReceiveLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    @Test
    public void nullArguments() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();
        Configuration c = new MockConfiguration();

        try
        {
            s.configure(c, null, -1);
            fail("should complain a about missing destination");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void timeout() throws Exception
    {
        ReceiveLoadStrategy rld = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test", "--timeout", "7"));

        MockConfiguration mc = new MockConfiguration();
        new Load(mc, Collections.<String>emptyList(), 0);

        rld.configure(mc, args, 0);

        assertEquals(7L, rld.getTimeoutMs().longValue());
    }

    @Test
    public void noTimeout() throws Exception
    {
        ReceiveLoadStrategy rld = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));

        MockConfiguration mc = new MockConfiguration();
        new Load(mc, Collections.<String>emptyList(), 0);

        rld.configure(mc, args, 0);

        assertNull(rld.getTimeoutMs());
    }

    @Test
    public void next() throws Exception
    {
        ReceiveLoadStrategy rld = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test", "--timeout", "7"));

        MockConfiguration mc = new MockConfiguration();

        // load will register itself with configuration
        new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "2")), 0);

        rld.configure(mc, args, 0);
        assertTrue(args.isEmpty());
        assertEquals(2, rld.getRemainingOperations());

        Queue queue = (Queue)rld.getDestination();
        assertEquals("test", queue.getName());

        Receive receive = (Receive)rld.next(null, null);
        assertEquals(queue, receive.getDestination());
        assertEquals(new Long(7), receive.getTimeoutMs());

        Receive receive2 = (Receive)rld.next(null, null);
        assertEquals(queue, receive2.getDestination());

        Receive receive3 = (Receive)rld.next(null, null);
        assertNull(receive3);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected ReceiveLoadStrategy getLoadStrategyToTest()
    {
        return new ReceiveLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
