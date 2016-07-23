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

package com.novaordis.gld.service.jms.activemq;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.EmbeddedNode;
import com.novaordis.gld.Node;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.service.ServiceTest;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class CollocatedBrokerServiceTest extends ServiceTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CollocatedBrokerServiceTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // configure() -----------------------------------------------------------------------------------------------------

    @Test
    public void configure_noMemoryUsage() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        CollocatedBrokerService cbs = getServiceToTest(mc, Arrays.asList((Node)(new EmbeddedNode())));

        List<String> arguments = new ArrayList<>(Arrays.asList("--something"));

        cbs.configure(arguments);

        // we should be fine
        assertEquals(1, arguments.size());
        assertEquals("--something", arguments.get(0));
    }

    @Test
    public void configure_incompleteArgumentList() throws Exception
    {
        MockConfiguration mc = new MockConfiguration();
        CollocatedBrokerService cbs = getServiceToTest(mc, Arrays.asList((Node)(new EmbeddedNode())));

        List<String> arguments = new ArrayList<>(Arrays.asList("--memoryUsage"));

        try
        {
            cbs.configure(arguments);
            fail("should fail because nothing follows after --memoryUsage");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void configure_NoOptionalValues() throws Exception
    {
        CollocatedBrokerService s = getServiceToTest(new MockConfiguration(), Arrays.asList(getTestNode()));

        List<String> arguments = new ArrayList<>(Arrays.asList(
            "--this-argument-surely-is-not-interesting-to-the-service",
            "apples",
            "--memoryUsage",
            "5gb",
            "--this-argument-is-also-not-interesting-to-the-service",
            "oranges"
        ));

        s.configure(arguments);

        // make sure unknown arguments were removed from list
        assertEquals(4, arguments.size());
        assertEquals("--this-argument-surely-is-not-interesting-to-the-service", arguments.get(0));
        assertEquals("apples", arguments.get(1));
        assertEquals("--this-argument-is-also-not-interesting-to-the-service", arguments.get(2));
        assertEquals("oranges", arguments.get(3));

        assertEquals("5 gb", s.getMemoryUsage());
        assertEquals(CollocatedBrokerService.DEFAULT_LOCAL_DIRECTORY, s.getDirectory());
        assertFalse(s.isDeleteDirectoryAtBoot());
        assertEquals(CollocatedBrokerService.DEFAULT_BROKER_ID, s.getBrokerId());
    }

    @Test
    public void configure_AllValues() throws Exception
    {
        CollocatedBrokerService s = getServiceToTest(new MockConfiguration(), Arrays.asList(getTestNode()));

        List<String> arguments = new ArrayList<>(Arrays.asList(
            "--this-argument-surely-is-not-interesting-to-the-service",
            "apples",
            "--memoryUsage",
            "5gb",
            "--directory",
            "/some/arbitrary/directory",
            "--delete-directory-at-boot",
            "--broker-id",
            "p001",
            "--this-argument-is-also-not-interesting-to-the-service",
            "oranges"
        ));

        s.configure(arguments);

        // make sure unknown arguments were removed from list
        assertEquals(4, arguments.size());
        assertEquals("--this-argument-surely-is-not-interesting-to-the-service", arguments.get(0));
        assertEquals("apples", arguments.get(1));
        assertEquals("--this-argument-is-also-not-interesting-to-the-service", arguments.get(2));
        assertEquals("oranges", arguments.get(3));

        assertEquals("5 gb", s.getMemoryUsage());
        assertEquals("/some/arbitrary/directory", s.getDirectory());
        assertTrue(s.isDeleteDirectoryAtBoot());
        assertEquals("p001", s.getBrokerId());

    }

    // start() ---------------------------------------------------------------------------------------------------------

    @Test
    public void start_noNodes() throws Exception
    {
        CollocatedBrokerService s = getServiceToTest(new MockConfiguration(), Arrays.asList(getTestNode()));

        try
        {
            s.start();
            fail("should fail because we did not specify target nodes");
        }
        catch (UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void start_noMemoryUsage() throws Exception
    {
        CollocatedBrokerService s = getServiceToTest(new MockConfiguration(), Arrays.asList(getTestNode()));

        s.setTarget(Arrays.asList((Node)new EmbeddedNode()));

        try
        {
            s.start();
            fail("should fail because we did not specify memoryUsage");
        }
        catch (UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Override
    @Test
    public void lifeCycle() throws Exception
    {
        CollocatedBrokerService s = getServiceToTest(new MockConfiguration(), Arrays.asList(getTestNode()));

        assertFalse(s.isStarted());

        s.setConfiguration(new MockConfiguration());

        File scratchDir = Tests.getScratchDir();
        assertTrue(scratchDir.isDirectory());

        File brokerDir = new File(scratchDir, "gld.broker");
        assertTrue(Files.mkdir(brokerDir));
        assertTrue(brokerDir.isDirectory());

        File testFile = new File(brokerDir, "to-be-deleted");
        assertTrue(Files.write(testFile, "to be deleted"));
        assertTrue(testFile.isFile());

        s.configure(new ArrayList<>(Arrays.asList(
            "--memoryUsage", "10mb",
            "--directory", brokerDir.getAbsolutePath(),
            "--delete-directory-at-boot")));
        s.setTarget(Arrays.asList((Node)new EmbeddedNode()));

        s.start();

        assertTrue(s.isStarted());

        // verify that the previous directory is deleted and a new one is created
        File d2 = new File(brokerDir.getAbsolutePath());
        assertTrue(d2.isDirectory());
        assertFalse(new File(d2, "to-be-deleted").isFile()); // this insures the directory was deleted

        // starting an already started service instance should throw IllegalStateException

        try
        {
            s.start();
            fail("should fail with IllegalStateException");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        assertTrue(s.isStarted());

        s.stop();

        assertFalse(s.isStarted());

        // stopping an already started stopped instance should be a noop

        s.stop();

        assertFalse(s.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CollocatedBrokerService getServiceToTest(Configuration configuration, List<Node> nodes) throws Exception
    {
        return new CollocatedBrokerService();
    }

    @Override
    protected Node getTestNode()
    {
        return new EmbeddedNode();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
