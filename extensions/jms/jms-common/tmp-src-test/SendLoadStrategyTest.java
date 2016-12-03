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
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.jms.Send;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SendLoadStrategyTest extends JmsLoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReceiveLoadStrategyTest.class);

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    @Test
    public void nullArguments() throws Exception {

        LoadStrategy s = getLoadStrategyToTest(null, null, -1);
        Configuration c = new MockConfiguration();

        try {

            s.configure(c, null, -1);
            fail("should complain a about missing destination");
        }
        catch(UserErrorException e) {
            log.info(e.getMessage());
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void next() throws Exception {

        SendLoadStrategy sls = getLoadStrategyToTest(null, null, -1);

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));

        MockConfiguration mc = new MockConfiguration();

        // load will register itself with configuration
        new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "2")), 0);

        sls.configure(mc, args, 0);

        assertTrue(args.isEmpty());
        Queue queue = (Queue)sls.getDestination();
        assertEquals("test", queue.getName());

        Send send = (Send)sls.next(null, null, false);
        assertEquals(queue, send.getDestination());

        Send send2 = (Send) sls.next(null, null, false);
        assertEquals(queue, send2.getDestination());

        Send send3 = (Send) sls.next(null, null, false);
        assertNull(send3);
    }

    @Test
    public void operationTypes() throws Exception {

        SendLoadStrategy ls = getLoadStrategyToTest(null, null, -1);

        Set<Class<? extends Operation>> operationTypes = ls.getOperationTypes();

        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(Send.class));
    }

    @Test
    public void messageSize() throws Exception {
        int messageSize = 1275;

        List<String> args = new ArrayList<>(Arrays.asList("--queue", "test"));

        MockConfiguration mc = new MockConfiguration();
        mc.setValueSize(messageSize);

        SendLoadStrategy ls = getLoadStrategyToTest(mc, args, 0);

        assertEquals(messageSize, ls.getMessageSize());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.strategy.load.LoadStrategyTest#getLoadStrategyToTest(Configuration, List, int)
     */
    @Override
    protected SendLoadStrategy getLoadStrategyToTest(Configuration config, List<String> arguments, int from)
        throws Exception {

        SendLoadStrategy ls = new SendLoadStrategy();

        if (config != null) {
            ls.configure(config, arguments, from);
        }

        return ls;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
