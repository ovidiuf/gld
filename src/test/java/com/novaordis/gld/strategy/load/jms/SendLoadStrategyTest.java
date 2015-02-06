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

import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Send;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SendLoadStrategyTest extends JmsLoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void next() throws Exception
    {
        SendLoadStrategy sls = getLoadStrategyToTest();

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));

        MockConfiguration mc = new MockConfiguration();
        mc.setMaxOperations(2L);

        sls.configure(mc, args, 0);

        assertTrue(args.isEmpty());
        Queue queue = (Queue)sls.getDestination();
        assertEquals("test", queue.getName());

        Send send = (Send)sls.next(null, null);
        assertEquals(queue, send.getDestination());

        Send send2 = (Send) sls.next(null, null);
        assertEquals(queue, send2.getDestination());

        Send send3 = (Send) sls.next(null, null);
        assertNull(send3);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected SendLoadStrategy getLoadStrategyToTest()
    {
        return new SendLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
