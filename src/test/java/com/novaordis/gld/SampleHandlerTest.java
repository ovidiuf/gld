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

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleHandlerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SampleHandlerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void processSample() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        PrintWriter pw = new PrintWriter(baos);
        SampleHandler sh = new SampleHandler(pw);

        long timestamp = 10L;

        SamplingInterval si =
            new SamplingInterval(timestamp, 1L, 2L, 3L, 4L, 5L, new long[] { 6L }, 7.0, 8.0, 9.0, 10.0, 11L, 12L);

        assertTrue(sh.canHandle(si));

        sh.handle(timestamp, "my thread", si);

        String s = baos.toString();

        String expected = SamplingInterval.
            toCsvLine(false, timestamp, 1L, 2L, 3L, 4L, 5L, new Long[]{6L}, 7.0, 8.0, 9.0, 10.0, 11L, 12L, null) + "\n";

        assertEquals(expected, s);
    }

    @Test
    public void processString() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        PrintWriter pw = new PrintWriter(baos);
        SampleHandler sh = new SampleHandler(pw);

        long timestamp = 11L;

        String o = "this is a message to go to file";

        assertTrue(sh.canHandle(o));

        sh.handle(timestamp, "my thread", o);

        String s = baos.toString();

        String expected = SamplingInterval.
            toCsvLine(false, timestamp, null, null, null, null, null, RedisFailure.NULL_COUNTERS, null, null, null, null, null, null, null) + "\n";
        expected = expected.replace("\n", "this is a message to go to file\n");

        assertEquals(expected, s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
