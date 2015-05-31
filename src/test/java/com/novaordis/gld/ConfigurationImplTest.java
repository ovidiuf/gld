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

package com.novaordis.gld;

import com.novaordis.gld.command.Load;
import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.sampler.SamplingConsumer;
import com.novaordis.gld.service.cache.EmbeddedCacheService;
import com.novaordis.gld.service.jms.activemq.ActiveMQService;
import com.novaordis.gld.statistics.CSVFormatter;
import com.novaordis.utilities.Files;
import com.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigurationImplTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ConfigurationImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // nodes - embedded ------------------------------------------------------------------------------------------------

    @Test
    public void embeddedNodes() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded:10001,localhost2:10002",
            });

        List<Node> nodes = c.getNodes();

        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof EmbeddedNode);
        assertEquals("localhost2", nodes.get(1).getHost());
        assertEquals(10002, nodes.get(1).getPort());
    }

    @Test
    public void embeddedNodes_comma() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded:10001,",
                "localhost2:10002"
            });

        List<Node> nodes = c.getNodes();

        assertEquals(2, nodes.size());
        assertTrue(nodes.get(0) instanceof EmbeddedNode);
        assertEquals("localhost2", nodes.get(1).getHost());
        assertEquals(10002, nodes.get(1).getPort());
    }

    // nodes -----------------------------------------------------------------------------------------------------------

    @Test
    public void singleNode() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--service",
                "embedded-generic",
                "--nodes",
                "localhost:10001",
                "--statistics",
                "none"
            });

        List<Node> nodes = c.getNodes();

        assertEquals(1, nodes.size());
        Node n = nodes.get(0);
        assertEquals("localhost", n.getHost());
        assertEquals(10001, n.getPort());
    }

    @Test
    public void twoNodes() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--service",
                "embedded-generic",
                "--nodes",
                "localhost:10001,example.com:10002",
                "--statistics",
                "none"
            });

        List<Node> nodes = c.getNodes();

        assertEquals(2, nodes.size());
        Node n = nodes.get(0);
        assertEquals("localhost", n.getHost());
        assertEquals(10001, n.getPort());
        Node n2 = nodes.get(1);
        assertEquals("example.com", n2.getHost());
        assertEquals(10002, n2.getPort());
    }

    // key-size --------------------------------------------------------------------------------------------------------

    @Test
    public void keySizeValueSize() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
                "--key-size", "55",
                "--value-size", "77"
            });

        assertEquals(55, c.getKeySize());
        assertEquals(77, c.getValueSize());
        assertEquals(-1L, c.getKeyExpirationSecs());
    }

    @Test
    public void expiration() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
                "--expiration", "2"
            });

        assertEquals(2, c.getKeyExpirationSecs());
    }

    @Test
    public void maxWaitMillis() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
            });

        assertEquals(ConfigurationImpl.DEFAULT_MAX_WAIT_MILLIS, c.getMaxWaitMillis());

        c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
                "--max-wait-millis", "777"
            });

        assertEquals(777L, c.getMaxWaitMillis());
    }

    @Test
    public void noCommand() throws Exception
    {
        try
        {
            new ConfigurationImpl(new String[]
                {
                    "--nodes", "embedded"
                });

            fail("should have failed with UserErrorException, no command specified");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // configuration from file -----------------------------------------------------------------------------------------

    @Test
    public void nodesFromConfigurationFile() throws Exception
    {
        File d = Tests.getScratchDirectory();
        File configurationFile = new File(d, "test.conf");

        assertTrue(Files.write(configurationFile,
            "nodes=embedded:2222\n"
        ));

        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--conf", configurationFile.getPath()
            });

        // make sure the command line overrides the configuration file value, but those that are not overridden surface

        List<Node> nodes = c.getNodes();

        assertEquals(1, nodes.size());

        Node n = nodes.get(0);

        assertTrue(n instanceof EmbeddedNode);
    }

    @Test
    public void configurationOverlay() throws Exception
    {
        File d = Tests.getScratchDirectory();
        File configurationFile = new File(d, "test.conf");

        assertTrue(Files.write(configurationFile,
            "nodes=blah:2222\n" +
                "expiration=777\n"
        ));

        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded:10005",
                "--conf", configurationFile.getPath()
            });

        // make sure the command line overrides the configuration file value, but those that are not overridden surface

        List<Node> nodes = c.getNodes();

        assertEquals(1, nodes.size());

        Node n = nodes.get(0);

        assertTrue(n instanceof EmbeddedNode);

        assertEquals(777, c.getKeyExpirationSecs());
    }

    @Test
    public void noPassword() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
            });

        assertNull(c.getPassword());
    }

    @Test
    public void passwordConfigFile() throws Exception
    {
        File d = Tests.getScratchDirectory();
        File configurationFile = new File(d, "test.conf");

        assertTrue(Files.write(configurationFile,
            "password=something\n"
        ));

        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
                "--conf", configurationFile.getPath()
            });

        assertEquals("something", c.getPassword());
    }

    @Test
    public void passwordCommandLine() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes", "embedded",
                "--password", "somethingelse"
            });

        assertEquals("somethingelse", c.getPassword());
    }

    @Test
    public void passwordBothConfigFileAndCommandLine() throws Exception
    {
        File d = Tests.getScratchDirectory();
        File configurationFile = new File(d, "test.conf");
        assertTrue(Files.write(configurationFile, "password=A\n"));

        try
        {
            System.setProperty("password.file.directory", d.getPath());

            ConfigurationImpl c = new ConfigurationImpl(new String[]
                {
                    "load",
                    "--nodes", "embedded",
                    "--conf", configurationFile.getPath(),
                    "--password", "commandlinetakesprecedence"
                });

            assertEquals("commandlinetakesprecedence", c.getPassword());
        }
        finally
        {
            System.clearProperty("password.file.directory");
        }
    }

    // --username -----------------------------------------------------------------------------------------------------

    @Test
    public void username() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded",
                "--username",
                "something"
            });

        assertEquals("something", c.getUsername());
    }

    // --statistics ----------------------------------------------------------------------------------------------------

    @Test
    public void defaultSampler() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded",
            });

        Sampler s = c.getSampler();
        assertNotNull(s);
    }

    @Test
    public void csvSampler() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded",
                "--statistics",
                "csv"
            });

        Sampler s = c.getSampler();
        List<SamplingConsumer> consumers = s.getConsumers();

        // should get the console CSV consumer
        assertEquals(1, consumers.size());

        SamplingConsumer consumer = consumers.get(0);
        assertTrue(consumer instanceof CSVFormatter);
    }

    @Test
    public void noStatistics() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded",
                "--statistics",
                "none"
            });

        assertNull(c.getSampler());
    }

    @Test
    public void invalidStatistics() throws Exception
    {

        try
        {
            new ConfigurationImpl(new String[]
                {
                    "load",
                    "--nodes",
                    "embedded",
                    "--statistics",
                    "blah"
                });
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // miscellaneous ---------------------------------------------------------------------------------------------------

    @Test
    public void miscellanous() throws Exception
    {

        try
        {
            new ConfigurationImpl(new String[]
                {
                    "load",
                    "--type",
                    "jms",
                    "--nodes",
                    "embedded",
                    "--queue",
                    "TEST",
                    "--max-operations",
                    "1",
                    "--statistics",
                    "none",
                    "--threads",
                    "10",
                });
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // --service -------------------------------------------------------------------------------------------------------

    @Test
    public void implicitService() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--nodes",
                "embedded",
            });

        Command command = c.getCommand();
        assertTrue(command instanceof Load);
        Service s = c.getService();
        assertTrue(s instanceof EmbeddedCacheService);
    }

    @Test
    public void explicitService() throws Exception
    {
        ConfigurationImpl c = new ConfigurationImpl(new String[]
            {
                "load",
                "--service",
                "com.novaordis.gld.service.jms.activemq.ActiveMQService",
                "--nodes",
                "embedded",
                "--queue",
                "TEST"
            });

        Command command = c.getCommand();
        assertTrue(command instanceof Load);
        Service s = c.getService();
        assertTrue(s instanceof ActiveMQService);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
