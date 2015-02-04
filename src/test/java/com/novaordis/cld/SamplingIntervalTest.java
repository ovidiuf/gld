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

package com.novaordis.cld;

import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SamplingIntervalTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplingIntervalTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void toCsvLine_Headers() throws Exception
    {
        String expected =
            "time, throughput (ops/sec), reads/sec, hits/sec, hits (%), writes/sec,    average read duration (ms), average write duration (ms),    other failures count, 'connection refused' count, 'broken pipe' count, 'connection reset' count, 'connect timed out' count, 'read timed out' count, jedis 'closed connection' count, jedis 'unknown reply' count, jedis 'max number of clients reached' count, jedis 'connection timed out' count, pool out count,    system load average, system cpu load, process cpu load, used heap (MB), committed heap (MB), comments";

        String headers = SamplingInterval.toCsvLine(
            true, -1L, null, null, null, null, null,
            RedisFailure.EMPTY_COUNTERS,
            null, null, null, null,
            null, null,
            null);

        assertEquals(expected, headers);

        String headers2 = SamplingInterval.getCsvHeaders();

        assertEquals(expected, headers2);
    }

    @Test
    public void toCsvLine_EmptyLine() throws Exception
    {
        long timestamp = 17L;

        String emptyLine = SamplingInterval.toCsvLine(
            false, timestamp, null, null, null, null, null,
            RedisFailure.NULL_COUNTERS,
            null, null, null, null,
            null, null,
            null);

        String expected = CollectorBasedStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) + ", , , , , ,    , ,    , , , , , , , , , , ,    , , , , , ";

        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_CommentOnly() throws Exception
    {
        long timestamp = 18L;

        String emptyLine = SamplingInterval.toCsvLine(
            false, timestamp, null, null, null, null, null,
            RedisFailure.NULL_COUNTERS,
            null, null, null, null,
            null, null,
            "this is a comment");

        String expected = CollectorBasedStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) + ", , , , , ,    , ,    , , , , , , , , , , ,    , , , , , this is a comment";

        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_SampleValuesOnly_NoComment() throws Exception
    {
        long timestamp = 18L;

        String emptyLine = SamplingInterval.toCsvLine(
            false, timestamp, 1L, 2L, 3L, 4L, 5L,
            new Long[] { 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L },
            31.1, 32.2, 33.3, 34.4,
            50L, 51L,
            null);

        String expected = CollectorBasedStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) +
            ", 4, 1, 2, 200.00%, 3,    .0, .0,    10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,    31.10, 32.20, 33.30, .00, .00, ";

        assertEquals(expected, emptyLine);
    }

    @Test
    public void toCsvLine_SampleValuesAndComment() throws Exception
    {
        long timestamp = 18L;

        String emptyLine = SamplingInterval.toCsvLine(
            false, timestamp, 1L, 2L, 3L, 4L, 5L,
            new Long[] { 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L },
            31.1, 32.2, 33.3, 34.4,
            50L, 51L,
            "this is a comment");

        String expected = CollectorBasedStatistics.TIMESTAMP_FORMAT_MS.format(timestamp) +
            ", 4, 1, 2, 200.00%, 3,    .0, .0,    10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,    31.10, 32.20, 33.30, .00, .00, this is a comment";

        assertEquals(expected, emptyLine);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
