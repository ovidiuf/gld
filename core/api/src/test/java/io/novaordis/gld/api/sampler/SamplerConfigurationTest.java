/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.sampler;

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/18/17
 */
public abstract class SamplerConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SamplerConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void getStatisticsFile_Null() throws Exception {

        Map<String, Object> raw = new HashMap<>();

        SamplerConfiguration sc = getSamplerConfigurationToTest(raw, new File("."));

        try {

            sc.getFile();
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing required statistics output file", msg);
        }
    }

    @Test
    public void getStatisticsFile_NotAString() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        raw.put(SamplerConfiguration.FILE_LABEL, 10);

        SamplerConfiguration sc = getSamplerConfigurationToTest(raw, new File("."));

        try {

            sc.getFile();
            fail("should throw exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals(
                    "expected " + SamplerConfiguration.FILE_LABEL + " to be a String but it is a(n) Integer: \"10\"",
                    msg);
        }
    }

    @Test
    public void defaults() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        raw.put(SamplerConfiguration.FILE_LABEL, "test.csv");

        SamplerConfiguration c = getSamplerConfigurationToTest(raw, new File("."));

        assertEquals(SamplerConfiguration.DEFAULT_SAMPLING_INTERVAL_MS, c.getSamplingInterval());
        assertEquals(SamplerConfiguration.DEFAULT_FORMAT, c.getFormat());
        assertEquals(new File("./test.csv"), c.getFile());
        assertEquals(SamplerConfiguration.DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS, c.getSamplingTaskRunInterval());
        assertTrue(c.getMetrics().isEmpty());
    }

    @Test
    public void nonDefaults() throws Exception {

        Map<String, Object> raw = new HashMap<>();

        raw.put(SamplerConfiguration.SAMPLING_INTERVAL_LABEL, 1234);
        raw.put(SamplerConfiguration.FORMAT_LABEL, "excel");
        raw.put(SamplerConfiguration.SAMPLING_TASK_RUN_INTERVAL_LABEL, 361);
        raw.put(SamplerConfiguration.METRICS_LABEL, Arrays.asList("A", "B", "C"));

        SamplerConfiguration c = getSamplerConfigurationToTest(raw, new File("."));

        assertEquals(1234, c.getSamplingInterval());
        assertEquals("excel", c.getFormat());
        assertEquals(361, c.getSamplingTaskRunInterval());

        List<String> metrics = c.getMetrics();
        assertEquals(3, metrics.size());
        assertEquals("A", metrics.get(0));
        assertEquals("B", metrics.get(1));
        assertEquals("C", metrics.get(2));
    }

    @Test
    public void relativeFile() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        raw.put(SamplerConfiguration.FILE_LABEL, "test.csv");
        SamplerConfiguration c = getSamplerConfigurationToTest(raw, new File(System.getProperty("basedir")));
        assertEquals(new File(System.getProperty("basedir"), "test.csv"), c.getFile());
    }

    @Test
    public void absoluteFile() throws Exception {

        Map<String, Object> raw = new HashMap<>();
        raw.put(SamplerConfiguration.FILE_LABEL, "/x/y/z/test.csv");
        SamplerConfiguration c = getSamplerConfigurationToTest(raw, new File(System.getProperty("basedir")));
        assertEquals(new File("/x/y/z/test.csv"), c.getFile());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract SamplerConfiguration getSamplerConfigurationToTest(
            Map<String, Object> rawMap, File configurationDirectory) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
