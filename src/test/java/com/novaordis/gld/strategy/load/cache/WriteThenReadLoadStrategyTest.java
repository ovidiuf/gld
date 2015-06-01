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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.Operation;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.operations.cache.Read;
import com.novaordis.gld.operations.cache.Write;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import com.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class WriteThenReadLoadStrategyTest extends LoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(WriteThenReadLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // configure -------------------------------------------------------------------------------------------------------

    @Test
    public void configure() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();

        ls.configure(mc, Arrays.asList("blah"), 0);

        log.debug(".");
    }

    // next ------------------------------------------------------------------------------------------------------------

    @Test
    public void onlyWrites() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // only writes
        List<String> arguments = new ArrayList<>();
        arguments.add("--read-to-write");
        arguments.add("0");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_0_SameResultsWhenWePassLastOperation() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // only writes
        List<String> arguments = new ArrayList<>();
        arguments.add("--read-to-write");
        arguments.add("0");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);
        Operation last = o;

        o = ls.next(last, null);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_1() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // read/write 1
        List<String> arguments = new ArrayList<>();
        arguments.add("--read-to-write");
        arguments.add("1");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);
    }

    @Test
    public void readToWrite_2() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // read/write 2
        List<String> arguments = new ArrayList<>();
        arguments.add("--read-to-write");
        arguments.add("2");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_3() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // read/write 3
        List<String> arguments = new ArrayList<>();
        arguments.add("--read-to-write");
        arguments.add("3");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);
    }

    @Test
    public void writeToRead_0() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // write/read 0
        List<String> arguments = new ArrayList<>();
        arguments.add("--write-to-read");
        arguments.add("0");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_1() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // write/read 1
        List<String> arguments = new ArrayList<>();
        arguments.add("--write-to-read");
        arguments.add("1");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_2() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // write/read 2
        List<String> arguments = new ArrayList<>();
        arguments.add("--write-to-read");
        arguments.add("2");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_3() throws Exception
    {
        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);

        // write/read 3
        List<String> arguments = new ArrayList<>();
        arguments.add("--write-to-read");
        arguments.add("3");

        ls.configure(mc, arguments, 0);

        assertTrue(arguments.isEmpty());

        Operation o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Write);

        o = ls.next(null, null);
        assertTrue(o instanceof Read);
    }

    //
    // integration with SingleThreadedRunner
    //

    @Test
    public void integration_WriteThenRead_SingleThreadedRunner() throws Exception
    {
        // TODO integration_WriteThenRead_SingleThreadedRunner(): this test fails because in the previous version,
        // the Statistics instance used to interrupt the runner, but now the Sampler just records and does not
        // interrupt. We need to fix the WriteThenReadLoadStrategy to know how to stop.

        fail("RETURN TO THIS WHEN YOU NEED CACHE LOAD TO RUN (3)");

//        File storeFile = new File(Tests.getScratchDir(), "test-keys.txt");
//
//        MockCacheService mcs = new MockCacheService();
//
//        MockConfiguration mc = new MockConfiguration();
//        mc.setCacheService(mcs);
//
//        MockSampler ms = new MockSampler();
//
//        CyclicBarrier barrier = new CyclicBarrier(1);
//
//        mc.setKeySize(3);
//        mc.setValueSize(7);
//        mc.setUseDifferentValues(false);
//        mc.setKeyStoreFile(storeFile.getPath());
//
//        WriteThenReadLoadStrategy wtr = new WriteThenReadLoadStrategy();
//
//        // only writes
//        List<String> arguments = new ArrayList<>();
//        arguments.add("--read-to-write");
//        arguments.add("0");
//
//        wtr.configure(mc, arguments, 0);
//
//        KeyStore ks = wtr.getKeyStore();
//        ks.start();
//
//        assertTrue(ks instanceof WriteOnlyFileKeyStore);
//
//        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, wtr, ms, barrier);
//        SingleThreadedRunnerTest.setRunning(st);
//
//        st.run();
//
//        List<OperationThrowablePair> recorded = ms.getRecorded();
//        assertEquals(2, recorded.size());
//
//        Write w = (Write)recorded.get(0).operation;
//        assertNull(recorded.get(0).throwable);
//
//        String key = w.getKey();
//        String value = w.getValue();
//
//        assertNotNull(key);
//        assertNotNull(value);
//
//        Write w2 = (Write)recorded.get(1).operation;
//        assertNull(recorded.get(1).throwable);
//
//        String key2 = w2.getKey();
//        String value2 = w2.getValue();
//
//        assertNotNull(key2);
//        assertNotNull(value2);
//
//        // make sure the key is in
//
//        // 1. cache
//
//        assertEquals(value, mcs.get(key));
//        assertEquals(value2, mcs.get(key2));
//
//        // 2. key storeFile
//
//        String content = Files.read(storeFile);
//
//        assertEquals(key + "\n" + key2 + "\n", content);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected WriteThenReadLoadStrategy getLoadStrategyToTest()
    {
        return new WriteThenReadLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
