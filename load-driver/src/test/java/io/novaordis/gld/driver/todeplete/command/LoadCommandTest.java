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

package io.novaordis.gld.driver.todeplete.command;

import io.novaordis.gld.api.todiscard.Configuration;
import org.apache.log4j.Logger;

public class LoadCommandTest extends CommandTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LoadCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // initialize()/load strategy --------------------------------------------------------------------------------------

//    @Test
//    public void noStrategySpecified_UseDefault() throws Exception {
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        assertNull(load.getLoadStrategy());
//
//        load.initialize();
//
//        LoadStrategy s = load.getLoadStrategy();
//
//        assertEquals(Load.DEFAULT_CACHE_LOAD_STRATEGY, s);
//
//        // make sure the load strategy is also installed in the configuration
//        LoadStrategy s2 = mc.getLoadStrategy();
//
//        assertEquals(s, s2);
//    }
//
//    @Test
//    public void missingStrategyName() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--strategy");
//
//        assertNull(load.getLoadStrategy());
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException because nothing follows --strategy");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void nonExistentStrategy() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--strategy");
//        load.addArgument("IAmQuiteSureThisStrategyDoesNotExist");
//
//        assertNull(load.getLoadStrategy());
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException because the strategy does not exist");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//            ClassNotFoundException cnfe = (ClassNotFoundException)e.getCause();
//            log.info(cnfe.getMessage());
//        }
//    }
//
//    @Test
//    public void strategyInstantiationFailure() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--strategy");
//        load.addArgument("FailureToInstantiate");
//
//        assertNull(load.getLoadStrategy());
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException because the strategy failed to instantiate");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//
//            InstantiationException ie = (InstantiationException)e.getCause();
//            log.info(ie.getMessage());
//        }
//    }
//
//    @Test
//    public void notALoadStrategy() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--strategy");
//        load.addArgument("NotA");
//
//        assertNull(load.getLoadStrategy());
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException because the strategy does not implement LoadStrategy");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//
//            Throwable t = e.getCause();
//
//            ClassCastException cce = (ClassCastException)t;
//            log.info(cce.getMessage());
//        }
//    }
//
//    @Test
//    public void validLoadStrategy() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--strategy");
//        load.addArgument("Mock");
//
//        assertNull(load.getLoadStrategy());
//
//        load.initialize();
//
//        LoadStrategy ls = load.getLoadStrategy();
//
//        assertTrue(ls instanceof MockLoadStrategy);
//
//        assertTrue(load.isInitialized());
//    }
//
//    // initialize()/load strategy and storage strategy -----------------------------------------------------------------
//
//    @Test
//    public void defaultLoadStrategyWithCustomStorageStrategy() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--storage-strategy");
//        load.addArgument("Mock");
//        load.addArgument("--mock-argument");
//        load.addArgument("blah");
//
//        assertNull(load.getLoadStrategy());
//        assertNull(mc.getStorageStrategy());
//
//        load.initialize();
//
//        assertTrue(load.isInitialized());
//
//        LoadStrategy ls = load.getLoadStrategy();
//        assertEquals(ls, Load.DEFAULT_CACHE_LOAD_STRATEGY);
//
//        MockStorageStrategy mss = (MockStorageStrategy)mc.getStorageStrategy();
//        assertEquals("blah", mss.getMockArgument());
//        assertTrue(mss.isStarted());
//    }
//
//    @Test
//    public void customLoadStrategyWithCustomStorageStrategy() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--load-strategy");
//        load.addArgument("Mock");
//        load.addArgument("--mock-load-argument");
//        load.addArgument("load-blah");
//
//        load.addArgument("--storage-strategy");
//        load.addArgument("Mock");
//        load.addArgument("--mock-storage-argument");
//        load.addArgument("storage-blah");
//
//        assertNull(load.getLoadStrategy());
//        assertNull(mc.getStorageStrategy());
//
//        load.initialize();
//
//        assertTrue(load.isInitialized());
//
//        MockLoadStrategy mls = (MockLoadStrategy)load.getLoadStrategy();
//        assertEquals("load-blah", mls.getMockLoadArgument());
//
//        MockStorageStrategy mss = (MockStorageStrategy)mc.getStorageStrategy();
//        assertEquals("storage-blah", mss.getMockStorageArgument());
//        assertTrue(mss.isStarted());
//    }
//
//    // read-to-write ratio ---------------------------------------------------------------------------------------------
//
//    @Test
//    public void missingReadToWriteValue() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--read-to-write");
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException, no actual read-to-write value following");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void missingWriteToReadValue() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--write-to-read");
//
//        try
//        {
//            load.initialize();
//            fail("should fail with UserErrorException, no actual write-to-read value following");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void misspelledRatio() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        Load load = new Load(mc, Collections.<String>emptyList(), 0);
//
//        load.addArgument("--write-toread");
//        load.addArgument("66");
//
//        load.initialize();
//
//        //
//        // modified this test after introducing services configurable from command line arguments. Because of that
//        // the command may see arguments it does not know, and it can't fail anymore
//        //
//
//        List<String> argsAfterInitialization = load.getArguments();
//
//        // nobody consumed them
//        assertEquals(2, argsAfterInitialization.size());
//        assertEquals("--write-toread", argsAfterInitialization.next(0));
//        assertEquals("66", argsAfterInitialization.next(1));
//    }
//
//    // content type ----------------------------------------------------------------------------------------------------
//
//    @Test
//    public void type_default_KeyValue() throws Exception {
//
//        MockConfiguration mc = new MockConfiguration();
//        List<String> args = new ArrayList<>(Collections.singletonList("load"));
//        Load load = new Load(mc, args, 0);
//        // we don't know yet the content type, we don't have a service defined
//        assertNull(load.getContentType());
//    }
//
//    @Test
//    public void type_KeyValue() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        List<String> args = new ArrayList<>(Arrays.asList("load", "--service", "embedded-cache"));
//        Load load = new Load(mc, args, 0);
//        // the Load command did not next a change to initialize the service yet, so it does not know the content type
//        assertNull(load.getContentType());
//    }
//
//    @Test
//    public void type_JMS() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        List<String> args = new ArrayList<>(Arrays.asList("load", "--service", "activemq"));
//        Load load = new Load(mc, args, 0);
//        // the Load command did not next a change to initialize the service yet, so it does not know the content type
//        assertNull(load.getContentType());
//    }
//
//    @Test
//    public void invalidType() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        List<String> args = new ArrayList<>(Arrays.asList("load", "--type", "blah"));
//
//        try
//        {
//            new Load(mc, args, 0);
//            fail("should fail on invalid content type");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void keyValueDefault() throws Exception
//    {
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes",
//                "embedded"
//            });
//
//        Load command = (Load)c.getCommand();
//        assertEquals(ContentType.KEYVALUE, command.getContentType());
//    }
//
//    @Test
//    public void keyValueExplicit() throws Exception
//    {
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--service",
//                "embedded-cache",
//                "--nodes",
//                "embedded"
//            });
//
//        Load command = (Load)c.getCommand();
//        assertEquals(ContentType.KEYVALUE, command.getContentType());
//    }
//
//    @Test
//    public void jms() throws Exception
//    {
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--service",
//                "activemq",
//                "--nodes",
//                "embedded",
//                "--queue",
//                "test"
//            });
//
//        Load command = (Load)c.getCommand();
//        assertEquals(ContentType.JMS, command.getContentType());
//    }
//
//    // --max-operations ------------------------------------------------------------------------------------------------
//
//    @Test
//    public void missingMaxOperations() throws Exception {
//
//        try {
//
//            new ConfigurationImpl(new String[]
//                {
//                    "load",
//                    "--nodes",
//                    "embedded",
//                    "--max-operations"
//                });
//
//            fail("should fail with UserErrorException, missing --max-operations value");
//
//        }
//        catch(UserErrorException e) {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void maxOperations() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[] {
//
//                "load",
//                "--nodes",
//                "embedded",
//                "--max-operations",
//                "100"
//            });
//
//        Load load = (Load)c.getCommand();
//        assertEquals(new Long(100), load.getMaxOperations());
//    }
//
//    @Test
//    public void maxOperations_default() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[] {
//                "load",
//                "--nodes",
//                "embedded",
//            });
//
//        Load load = (Load)c.getCommand();
//
//        assertNull(load.getMaxOperations());
//    }
//
//    @Test
//    public void maxOperations_InvalidValue() throws Exception {
//
//        try {
//
//            new ConfigurationImpl(new String[] {
//                    "load",
//                    "--nodes",
//                    "embedded",
//                    "--max-operations",
//                    "blah"
//                });
//
//            fail("should fail with UserErrorException, wrong --max-operations value");
//
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//
//            Throwable cause = e.getCause();
//
//            assertTrue(cause instanceof NumberFormatException);
//        }
//    }
//
//    // --duration ------------------------------------------------------------------------------------------------------
//
//    @Test
//    public void duration_ViaConfiguration() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[] {
//
//                "load",
//                "--nodes",
//                "embedded",
//                "--duration",
//                "10m"
//        });
//
//        Load load = (Load)c.getCommand();
//
//        Duration d = load.getDuration();
//
//        assertNotNull(d);
//
//        assertEquals(10L * 60 * 1000, d.getMilliseconds());
//
//        //
//        // make sure duration is also available in configuration
//        //
//
//        Duration d2 = c.getDuration();
//        assertEquals(10L * 60 * 1000, d2.getMilliseconds());
//    }
//
//    @Test
//    public void duration_ViaConfiguration_InvalidValue() throws Exception {
//
//        try {
//            new ConfigurationImpl(new String[]{
//
//                    "load",
//                    "--nodes",
//                    "embedded",
//                    "--duration",
//                    "not-a-duration"
//            });
//        }
//        catch(UserErrorException e) {
//
//            String msg = e.getMessage();
//            log.info(msg);
//            assertTrue(e.getCause() instanceof DurationFormatException);
//        }
//    }
//
//    @Test
//    public void duration_ViaConstructor() throws Exception {
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        List<String> arguments = new ArrayList<>(Arrays.asList(
//                "something",
//                "--duration",
//                "7s",
//                "something-else"));
//
//        Load load = new Load(mc, arguments, 1);
//
//        Duration d = load.getDuration();
//
//        assertNotNull(d);
//
//        assertEquals(7L * 1000, d.getMilliseconds());
//
//        assertEquals(2, arguments.size());
//        assertEquals("something", arguments.next(0));
//        assertEquals("something-else", arguments.next(1));
//
//        //
//        // make sure duration is also available in configuration
//        //
//
//        Duration d2 = mc.getDuration();
//        assertEquals(7L * 1000, d2.getMilliseconds());
//    }
//
//    @Test
//    public void duration_ViaConstructor_InvalidValue() throws Exception {
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setService(new MockCacheService());
//
//        List<String> arguments = new ArrayList<>(Arrays.asList(
//                "something",
//                "--duration",
//                "7s",
//                "something-else"));
//
//        try {
//
//            new Load(mc, arguments, 1);
//        }
//        catch(UserErrorException e) {
//
//            String msg = e.getMessage();
//            log.info(msg);
//            assertTrue(e.getCause() instanceof DurationFormatException);
//        }
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected Load getCommandToTest(Configuration c) {

//        return new Load(c, Collections.<String>emptyList(), 0);
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
