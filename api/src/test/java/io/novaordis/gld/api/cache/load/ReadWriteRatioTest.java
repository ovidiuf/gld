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

import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReadWriteRatioTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ReadWriteRatioTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // read-to-write, write-to-read ------------------------------------------------------------------------------------

//    @Test
//    public void defaults() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio(null, null);
//
//        // default: a write followed by a series of one read
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }
//
//    @Test
//    public void readToWriteNotAnInteger() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("blah", null);
//            fail("should fail with UserErrorException because --read-to-write value is not an integer");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeToReadNotAnInteger() throws Exception {
//
//        try {
//
//            new ReadWriteRatio(null, "blah");
//            fail("should fail with UserErrorException because --write-to-read value is not an integer");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void readToWriteNotAnInteger2() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("2.5", null);
//            fail("should fail with UserErrorException because --read-to-write value is not an integer");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeToReadNotAnInteger2() throws Exception {
//
//        try {
//
//            new ReadWriteRatio(null, "0.1");
//            fail("should fail with UserErrorException because --write-to-read value is not an integer");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void readToWrite_Negative() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("-1", null);
//            fail("should fail with UserErrorException because --read-to-write value is not positive or zero");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void writeToRead_Negative() throws Exception {
//
//        try {
//
//            new ReadWriteRatio(null, "-1");
//            fail("should fail with UserErrorException because --write-to-read value is not positive or zero");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void oneToOne() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio("1", null);
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//
//        ReadWriteRatio r2 = new ReadWriteRatio(null, "1");
//
//        assertTrue(r2.isWrite());
//        assertFalse(r2.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r2.doesWritingTakePlace());
//    }
//
//    @Test
//    public void oneToOne_equivalent() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio("1", "1");
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }
//
//    @Test
//    public void oneToOne_incompatible() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("1", "0");
//            fail("incompatible values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void oneToOne_incompatible_2() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("1", "2");
//            fail("incompatible values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void onlyWrites() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio("0", null);
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(0, r.getFollowUpSeriesSize());
//    }
//
//    @Test
//    public void onlyWrites_IncompatibleWriteToReadValue() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("0", "0");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void onlyWrites_IncompatibleWriteToReadValue_2() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("0", "1");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void onlyWrites_IncompatibleWriteToReadValue_3() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("0", "5");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void multipleReadsToWrite() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio("3", null);
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(3, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }
//
//    @Test
//    public void multipleReadsToWrite_IncompatibleWriteToRead() throws Exception {
//
//        try {
//
//            new ReadWriteRatio("3", "0");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//
//        try {
//
//            new ReadWriteRatio("3", "1");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//
//        try {
//
//            new ReadWriteRatio("3", "3");
//            fail("should fail with UserErrorException, incompatible --read-to-write/--write-to-read values");
//        }
//        catch(UserErrorException e) {
//
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void onlyReads() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio(null, "0");
//
//        assertFalse(r.isWrite());
//        assertTrue(r.isRead());
//        assertEquals(0, r.getFollowUpSeriesSize());
//
//        assertFalse(r.doesWritingTakePlace());
//    }
//
//    @Test
//    public void oneToOne_2() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio(null, "1");
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }
//
//    @Test
//    public void moreReadsThanWrites() throws Exception {
//
//        ReadWriteRatio r = new ReadWriteRatio(null, "2");
//
//        assertTrue(r.isRead());
//        assertFalse(r.isWrite());
//        assertEquals(2, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }

//    @Test
//    public void writeToReadRatio() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes", "embedded",
//                "--write-to-read", "5",
//            });
//
//        Load load = (Load)c.getCommand();
//        ReadWriteRatio r = ((WriteThenReadLoadStrategy)load.getLoadStrategy()).getReadWriteRatio();
//
//        assertTrue(r.isRead());
//        assertFalse(r.isWrite());
//        assertEquals(5, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }

//    @Test
//    public void onlyReads_2() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes", "embedded",
//                "--write-to-read", "0",
//            });
//
//        Load load = (Load)c.getCommand();
//        ReadWriteRatio r = ((WriteThenReadLoadStrategy)load.getLoadStrategy()).getReadWriteRatio();
//
//        assertTrue(r.isRead());
//        assertFalse(r.isWrite());
//        assertEquals(0, r.getFollowUpSeriesSize());
//
//        assertFalse(r.doesWritingTakePlace());
//    }

//    @Test
//    public void readToWriteRatio() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes", "embedded",
//                "--read-to-write", "7",
//            });
//
//        Load load = (Load)c.getCommand();
//        ReadWriteRatio r = ((WriteThenReadLoadStrategy)load.getLoadStrategy()).getReadWriteRatio();
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(7, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }

//    @Test
//    public void onlyWrites_2() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes", "embedded",
//                "--read-to-write", "0",
//            });
//
//        Load load = (Load)c.getCommand();
//        ReadWriteRatio r = ((WriteThenReadLoadStrategy)load.getLoadStrategy()).getReadWriteRatio();
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(0, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }

//    @Test
//    public void writeToReadAndReadToWriteAreMutuallyExclusive() throws Exception {
//
//        try
//        {
//            new ConfigurationImpl(new String[]
//                {
//                    "load",
//                    "--nodes", "embedded",
//                    "--read-to-write", "2",
//                    "--write-to-read", "2",
//                });
//
//            fail("should fail because --read-to-write and --write-to-read are mutually exclusive");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }

//    @Test
//    public void writeToReadAndReadToWriteAreMutuallyExclusiveUnlessTheyAreBothOne() throws Exception {
//
//        ConfigurationImpl c = new ConfigurationImpl(new String[]
//            {
//                "load",
//                "--nodes", "embedded",
//                "--read-to-write", "1",
//                "--write-to-read", "1",
//            });
//
//        Load load = (Load)c.getCommand();
//        ReadWriteRatio r = ((WriteThenReadLoadStrategy)load.getLoadStrategy()).getReadWriteRatio();
//
//        assertTrue(r.isWrite());
//        assertFalse(r.isRead());
//        assertEquals(1, r.getFollowUpSeriesSize());
//
//        assertTrue(r.doesWritingTakePlace());
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
