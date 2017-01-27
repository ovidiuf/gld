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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.LoadStrategyFactory;
import io.novaordis.gld.api.LoadStrategyTest;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.cache.MockCacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Read;
import io.novaordis.gld.api.cache.operation.Write;
import io.novaordis.gld.api.configuration.MockLoadConfiguration;
import io.novaordis.gld.api.configuration.MockServiceConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WriteThenReadLoadStrategyTest extends LoadStrategyTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(WriteThenReadLoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // init() ----------------------------------------------------------------------------------------------------------

    @Test
    public void configure() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();

        ls.init(msc, new HashMap<>(), mlc);

        ReadWriteRatio readWriteRatio = ls.getReadWriteRatio();
        assertNotNull(readWriteRatio);

        log.debug(".");
    }

    // next ------------------------------------------------------------------------------------------------------------

    @Test
    public void onlyWrites() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // only writes
        //

        rawConfig.put(WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL, 0);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_0_SameResultsWhenWePassLastOperation() throws Exception  {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // only writes
        //

        rawConfig.put(WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL, 0);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);
        Operation last = o;

        o = ls.next(last, null, false);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null, false);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null, false);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null, false);
        assertTrue(o instanceof Write);
        last = o;

        o = ls.next(last, null, false);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_1() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // read/write 1
        //

        rawConfig.put(WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL, 1);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);
    }

    @Test
    public void readToWrite_2() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // read/write 2
        //

        rawConfig.put(WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL, 2);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);
    }

    @Test
    public void readToWrite_3() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // read/write 3
        //

        rawConfig.put(WriteThenReadLoadStrategy.READ_TO_WRITE_LABEL, 3);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);
    }

    @Test
    public void writeToRead_0() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // write/read 0
        //

        rawConfig.put(WriteThenReadLoadStrategy.WRITE_TO_READ_LABEL, 0);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_1() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // write/read 1
        //

        rawConfig.put(WriteThenReadLoadStrategy.WRITE_TO_READ_LABEL, 1);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_2() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // write/read 2
        //

        rawConfig.put(WriteThenReadLoadStrategy.WRITE_TO_READ_LABEL, 2);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);
    }

    @Test
    public void writeToRead_3() throws Exception {

        WriteThenReadLoadStrategy ls = getLoadStrategyToTest();

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        Map<String, Object> rawConfig = new HashMap<>();

        //
        // write/read 3
        //

        rawConfig.put(WriteThenReadLoadStrategy.WRITE_TO_READ_LABEL, 3);

        //
        // use the same value
        //

        rawConfig.put(WriteThenReadLoadStrategy.REUSE_VALUE_LABEL, true);

        ls.init(msc, rawConfig, mlc);

        Operation o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Write);

        o = ls.next(null, null, false);
        assertTrue(o instanceof Read);
    }

    // factory ---------------------------------------------------------------------------------------------------------

    @Test
    public void factory() throws Exception {

        MockCacheServiceConfiguration msc = new MockCacheServiceConfiguration();
        MockLoadConfiguration mlc = new MockLoadConfiguration();
        msc.set(new HashMap<>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        msc.set(WriteThenReadLoadStrategy.NAME,
                ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL,
                LoadStrategy.NAME_LABEL);


        WriteThenReadLoadStrategy s = (WriteThenReadLoadStrategy)LoadStrategyFactory.build(msc, mlc);
        assertEquals(WriteThenReadLoadStrategy.NAME, s.getName());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected WriteThenReadLoadStrategy getLoadStrategyToTest() throws Exception {

        return new WriteThenReadLoadStrategy();
    }

    @Override
    protected MockCacheServiceConfiguration getCorrespondingServiceConfiguration() {

        MockCacheServiceConfiguration c = new MockCacheServiceConfiguration();
        c.set(new HashMap<String, Object>(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);
        return c;
    }

    @Override
    protected void initialize(LoadStrategy ls, MockServiceConfiguration msc) throws Exception {

        assertTrue(ls instanceof WriteThenReadLoadStrategy);
        assertTrue(msc instanceof MockCacheServiceConfiguration);
        msc.set(ls.getName(), ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL, LoadStrategy.NAME_LABEL);
        ls.init(msc, new MockLoadConfiguration());
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
