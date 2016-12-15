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

    @Test
    public void defaults() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(null, null);

        // default: a write followed by a series of one read
        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(1, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());
    }

    @Test
    public void readToWrite_Negative() throws Exception {

        try {

            new ReadWriteRatio(-1, null);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("only positive or zero integers can be read-to-write ratios", msg);
        }
    }

    @Test
    public void writeToRead_Negative() throws Exception {

        try {

            new ReadWriteRatio(null, -1);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("only positive or zero integers can be write-to-read ratios", msg);
        }
    }

    @Test
    public void oneToOne() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(1, null);

        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(1, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());

        ReadWriteRatio r2 = new ReadWriteRatio(null, 1);

        assertTrue(r2.isWrite());
        assertFalse(r2.isRead());
        assertEquals(1, r.getFollowUpSeriesSize());

        assertTrue(r2.doesWritingTakePlace());
    }

    @Test
    public void oneToOne_equivalent() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(1, 1);

        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(1, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());
    }

    @Test
    public void oneToOne_incompatible() throws Exception {

        try {

            new ReadWriteRatio(1, 0);
            fail("incompatible values");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void oneToOne_incompatible_2() throws Exception {

        try {

            new ReadWriteRatio(1, 2);
            fail("incompatible values");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void onlyWrites() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(0, null);

        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(0, r.getFollowUpSeriesSize());
    }

    @Test
    public void onlyWrites_IncompatibleWriteToReadValue() throws Exception {

        try {

            new ReadWriteRatio(0, 0);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void onlyWrites_IncompatibleWriteToReadValue_2() throws Exception {

        try {

            new ReadWriteRatio(0, 1);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void onlyWrites_IncompatibleWriteToReadValue_3() throws Exception {

        try {

            new ReadWriteRatio(0, 5);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void multipleReadsToWrite() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(3, null);

        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(3, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());
    }

    @Test
    public void multipleReadsToWrite_IncompatibleWriteToRead() throws Exception {

        try {

            new ReadWriteRatio(3, 0);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }

        try {

            new ReadWriteRatio(3, 1);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }

        try {

            new ReadWriteRatio(3, 3);
            fail("should throw UserErrorException");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("incompatible read-to-write/write-to-read values: .*"));
        }
    }

    @Test
    public void onlyReads() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(null, 0);

        assertFalse(r.isWrite());
        assertTrue(r.isRead());
        assertEquals(0, r.getFollowUpSeriesSize());

        assertFalse(r.doesWritingTakePlace());
    }

    @Test
    public void oneToOne_2() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(null, 1);

        assertTrue(r.isWrite());
        assertFalse(r.isRead());
        assertEquals(1, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());
    }

    @Test
    public void moreReadsThanWrites() throws Exception {

        ReadWriteRatio r = new ReadWriteRatio(null, 2);

        assertTrue(r.isRead());
        assertFalse(r.isWrite());
        assertEquals(2, r.getFollowUpSeriesSize());

        assertTrue(r.doesWritingTakePlace());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
