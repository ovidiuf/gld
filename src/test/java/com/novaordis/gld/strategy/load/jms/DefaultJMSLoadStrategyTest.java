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

import com.novaordis.gld.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Receive;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DefaultJmsLoadStrategyTest extends LoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(DefaultJmsLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void queueOrTopicNeeded() throws Exception
    {
        DefaultJmsLoadStrategy s = getLoadStrategyToTest();
        try
        {
            s.configure(new MockConfiguration(), Collections.<String>emptyList(), 0);
            fail("should fail with UserErrorException, either a queue or a topic must be specified");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void send() throws Exception
    {
        DefaultJmsLoadStrategy s = getLoadStrategyToTest();

        s.configure(new MockConfiguration(), new ArrayList<>(Arrays.asList("--queue", "test")), 0);

        Queue queue = (Queue)s.getDestination();

        assertEquals("test", queue.getName());

        Send send = (Send)s.next(null, null);

        assertEquals(queue, send.getDestination());
    }

    @Test
    public void receiveNoTimeout() throws Exception
    {
        DefaultJmsLoadStrategy s = getLoadStrategyToTest();

        s.configure(new MockConfiguration(), new ArrayList<>(Arrays.asList("--queue", "test", "--tmp-receive")), 0);

        Queue queue = (Queue)s.getDestination();

        assertEquals("test", queue.getName());

        Receive receive = (Receive)s.next(null, null);

        assertEquals(queue, receive.getDestination());
        assertNull(receive.getTimeoutMs());
    }

    @Test
    public void receiveWithTimeout() throws Exception
    {
        DefaultJmsLoadStrategy s = getLoadStrategyToTest();

        s.configure(new MockConfiguration(),
            new ArrayList<>(Arrays.asList("--queue", "test", "--tmp-receive", "--receive-timeout", "1000")), 0);

        Queue queue = (Queue)s.getDestination();

        assertEquals("test", queue.getName());

        Receive receive = (Receive)s.next(null, null);

        assertEquals(queue, receive.getDestination());
        assertEquals(new Long(1000L), receive.getTimeoutMs());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected DefaultJmsLoadStrategy getLoadStrategyToTest()
    {
        return new DefaultJmsLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
