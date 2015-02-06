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

import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public abstract class JmsLoadStrategyTest extends LoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(JmsLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void defaultReuseSessionIsFalse()
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();
        assertFalse(jms.isReuseSession());
        log.debug(jms);
    }

    // destination -----------------------------------------------------------------------------------------------------

    @Test
    public void noDestination() throws Exception
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--something", "else"));

        try
        {
            jms.configure(new MockConfiguration(), args, 0);
            fail("should fail with UserErrorException, no destination");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void queue() throws Exception
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "blah"));

        jms.configure(new MockConfiguration(), args, 0);

        Queue q = (Queue)jms.getDestination();
        assertEquals("blah", q.getName());
    }

    @Test
    public void topic() throws Exception
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--topic", "blah"));

        jms.configure(new MockConfiguration(), args, 0);

        Topic t = (Topic)jms.getDestination();
        assertEquals("blah", t.getName());
    }

    @Test
    public void bothQueueAndTopic() throws Exception
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--topic", "blah", "--queue", "blah2"));

        try
        {
            jms.configure(new MockConfiguration(), args, 0);
            fail("should fail with UserErrorException, both queue and topic");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // max-operation ---------------------------------------------------------------------------------------------------

    @Test
    public void noMaxOperations() throws Exception
    {
        JmsLoadStrategy jms = getLoadStrategyToTest();
        assertEquals(Long.MAX_VALUE, jms.getRemainingOperations());
    }

    @Test
    public void maxOperations() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setMaxOperations(777L);

        JmsLoadStrategy jms = getLoadStrategyToTest();
        jms.configure(mc, new ArrayList<>(Arrays.asList("--queue", "test")), 0);

        assertEquals(777L, jms.getRemainingOperations());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected abstract JmsLoadStrategy getLoadStrategyToTest();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
