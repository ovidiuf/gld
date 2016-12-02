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

package com.novaordis.gld.command;

import com.novaordis.gld.Configuration;
import io.novaordis.gld.api.Service;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockCacheService;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.strategy.storage.StdoutStorageStrategy;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ContentCommandTest extends CommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ContentCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void defaultContentCommandConfiguration() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);
        content.initialize();

        StdoutStorageStrategy ss = (StdoutStorageStrategy)content.getStorageStrategy();
        assertNotNull(ss);


        Service cs = mc.getService();
        assertTrue(cs.isStarted());
    }

    @Test
    public void keyCountOnly() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);
        content.addArgument("--key-count-only");
        content.initialize();

        assertTrue(content.isKeyCountOnly());
    }

    @Test
    public void missingStrategy() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);

        content.addArgument("--storage-strategy");

        try
        {
            content.initialize();
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
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);

        content.addArgument("--storage-strategy");
        content.addArgument("NoSuchStrategy");

        try
        {
            content.initialize();
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
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);

        content.addArgument("--storage-strategy");
        content.addArgument("stdout");

        content.initialize();

        StdoutStorageStrategy ss = (StdoutStorageStrategy)content.getStorageStrategy();
        assertNotNull(ss);
    }

    @Test
    public void strategy_Stdout() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        mc.setService(new MockCacheService());
        Content content = getCommandToTest(mc);

        content.addArgument("--storage-strategy");
        content.addArgument("Stdout");

        content.initialize();

        StdoutStorageStrategy ss = (StdoutStorageStrategy)content.getStorageStrategy();
        assertNotNull(ss);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Content getCommandToTest(Configuration c)
    {
        return new Content(c);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
