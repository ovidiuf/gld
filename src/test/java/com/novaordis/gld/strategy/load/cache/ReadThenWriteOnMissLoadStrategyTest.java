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

import com.novaordis.gld.Configuration;
import com.novaordis.gld.Operation;
import com.novaordis.gld.SingleThreadedRunner;
import com.novaordis.gld.SingleThreadedRunnerTest;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.keystore.RandomKeyGenerator;
import com.novaordis.gld.mock.MockCacheService;
import com.novaordis.gld.mock.MockConfiguration;
import com.novaordis.gld.mock.MockSampler;
import com.novaordis.gld.mock.OperationThrowablePair;
import com.novaordis.gld.operations.cache.Read;
import com.novaordis.gld.operations.cache.Write;
import com.novaordis.gld.strategy.load.LoadStrategyTest;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReadThenWriteOnMissLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ReadThenWriteOnMissLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception {
        Tests.cleanup();
    }

    @Test
    public void hit_noKeyStore() throws Exception {
        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        // first operation is always a read
        Operation o = rtwom.next(null, null, false);

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals(11, key.length());
        assertNull(r.getValue());

        // make it a "hit"
        r.setValue("something");

        o = rtwom.next(r, null, false);

        // the next operation is another read, for a different random key

        Read r2 = (Read)o;

        String key2 = r2.getKey();
        log.info(key2);
        assertEquals(11, key2.length());
        assertNull(r2.getValue());
        assertNotEquals(key, key2);
    }

    @Test
    public void miss_noKeyStore() throws Exception {
        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        // first operation is always a read
        Operation o = rtwom.next(null, null, false);

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals(11, key.length());

        // insure it's a miss
        assertNull(r.getValue());

        o = rtwom.next(r, null, false);

        // the next operation is a write for the key we missed

        Write w = (Write)o;

        String key2 = w.getKey();
        assertEquals(key, key2);

        String value = w.getValue();
        assertEquals(17, value.length());
    }

    @Test
    public void readAfterWrite_noKeyStore() throws Exception {

        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        Write w = new Write("TEST-KEY", "TEST-VALUE");

        Operation o = rtwom.next(w, null, false);

        // the next operation after a write is another read

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals(11, key.length());
    }

    @Test
    public void hit_validKeyStore() throws Exception {

        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");

        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);
        mc.setKeyStoreFile(keyStoreFile.getPath());

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        // first operation is always a read
        Operation o = rtwom.next(null, null, false);

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals("KEY0", key);
        assertNull(r.getValue());

        // make it a "hit"
        r.setValue("something");

        o = rtwom.next(r, null, false);

        // the next operation is another read, for the next key

        Read r2 = (Read)o;

        String key2 = r2.getKey();
        log.info(key2);
        assertEquals("KEY1", key2);
        assertNull(r2.getValue());
    }

    @Test
    public void miss_validKeyStore() throws Exception {
        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");

        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);
        mc.setKeyStoreFile(keyStoreFile.getPath());

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        // first operation is always a read
        Operation o = rtwom.next(null, null, false);

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals("KEY0", key);

        // insure it's a miss
        assertNull(r.getValue());

        o = rtwom.next(r, null, false);

        // the next operation is a write for the key we missed

        Write w = (Write)o;

        String key2 = w.getKey();
        assertEquals(key, key2);

        String value = w.getValue();
        assertEquals(17, value.length());
    }

    @Test
    public void readAfterWrite_validKeyStore() throws Exception {
        File keyStoreFile = new File(Tests.getScratchDir(), "keys.txt");
        Files.write(keyStoreFile, "KEY0\nKEY1\nKEY2\n");

        ReadThenWriteOnMissLoadStrategy rtwom = getLoadStrategyToTest(null, null, -1);

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(11);
        mc.setValueSize(17);
        mc.setUseDifferentValues(false);
        mc.setKeyStoreFile(keyStoreFile.getPath());

        rtwom.configure(mc, Collections.<String>emptyList(), 0);

        Write w = new Write("TEST-KEY", "TEST-VALUE");

        Operation o = rtwom.next(w, null, false);

        // the next operation after a write is another read

        Read r = (Read)o;

        String key = r.getKey();
        log.info(key);
        assertEquals("KEY0", key);
    }

    //
    // integration with SingleThreadedRunner
    //

    @Test
    public void integration_ReadThenWriteOnMiss_SingleThreadedRunner_ReadThenOutOfOps() throws Exception {
        MockCacheService mcs = new MockCacheService()
        {
            @Override
            public String get(String key)
            {
                // we override get() to return a hit for any key
                return "SYNTHETIC-HIT";
            }
        };

        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);
        mc.setService(mcs);
        mc.setCommand(new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "1")), 0));

        ReadThenWriteOnMissLoadStrategy rtwom = new ReadThenWriteOnMissLoadStrategy();
        rtwom.configure(mc, Collections.<String>emptyList(), 0);
        assertTrue(rtwom.getKeyStore() instanceof RandomKeyGenerator);

        MockSampler ms = new MockSampler();
        CyclicBarrier barrier = new CyclicBarrier(1);
        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, rtwom, ms, barrier, new AtomicBoolean(false));
        SingleThreadedRunnerTest.setRunning(st);

        st.run();

        List<OperationThrowablePair> recorded = ms.getRecorded();

        // we should record a read and a write
        assertEquals(1, recorded.size());

        Read r = (Read)recorded.get(0).operation;
        assertNull(recorded.get(0).throwable);
        assertTrue(r.hasBeenPerformed());
        assertNotNull(r.getKey());
        assertEquals("SYNTHETIC-HIT", r.getValue());
    }

    @Test
    public void integration_ReadThenWriteOnMiss_SingleThreadedRunner_ReadThenWrite() throws Exception {
        MockCacheService mcs = new MockCacheService();
        MockConfiguration mc = new MockConfiguration();
        mc.setKeySize(1);
        mc.setValueSize(1);
        mc.setUseDifferentValues(false);
        mc.setService(mcs);
        mc.setCommand(new Load(mc, new ArrayList<>(Arrays.asList("--max-operations", "1")), 0));

        ReadThenWriteOnMissLoadStrategy rtwom = new ReadThenWriteOnMissLoadStrategy();
        rtwom.configure(mc, Collections.<String>emptyList(), 0);
        assertTrue(rtwom.getKeyStore() instanceof RandomKeyGenerator);

        MockSampler ms = new MockSampler();
        CyclicBarrier barrier = new CyclicBarrier(1);
        SingleThreadedRunner st = new SingleThreadedRunner("TEST", mc, rtwom, ms, barrier, new AtomicBoolean(false));
        SingleThreadedRunnerTest.setRunning(st);

        st.run();

        List<OperationThrowablePair> recorded = ms.getRecorded();

        // we should record a read and a write
        assertEquals(2, recorded.size());

        Read r = (Read)recorded.get(0).operation;
        assertNull(recorded.get(0).throwable);
        assertTrue(r.hasBeenPerformed());
        String key = r.getKey();
        assertNotNull(key);
        assertNull(r.getValue());

        Write w = (Write)recorded.get(1).operation;
        assertNull(recorded.get(1).throwable);
        assertTrue(w.isSuccessful());
        String value = w.getValue();

        //
        // make sure the key was written in cache
        //

        assertEquals(value, mcs.get(key));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @see LoadStrategyTest#getLoadStrategyToTest(Configuration, List, int)
     */
    @Override
    protected ReadThenWriteOnMissLoadStrategy getLoadStrategyToTest(
        Configuration config, List<String> arguments, int from) throws Exception {
        return new ReadThenWriteOnMissLoadStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
