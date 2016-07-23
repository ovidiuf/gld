/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package com.novaordis.gld.strategy.load.cache.http;

import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionWrite;

import java.util.Random;

/**
 * A HttpSession simulation.
 *
 * There is a one-to-one association between a SingleThreadRunner's thread and a HttpSessionSimulation instance.
 *
 * HttpSessionSimulation implementation is NOT thread safe, it is supposed to be accessed by a thread and a thread only
 * during its life.
 *
 * The operations simulated by a HTTP session are currently hardcoded, and they generally consist in a "creation"
 * event, followed by a configurable number of write and reads, followed by an "invalidation" event.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public class HttpSessionSimulation {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_SESSION_ID_LENGTH = 18;

    public static final int DEFAULT_WRITE_COUNT = 10;

    private static final char[] SESSION_ID_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-_".toCharArray();

    // Static ----------------------------------------------------------------------------------------------------------

    public static String generateSessionId(Random random) {

        byte[] bytes = new byte[DEFAULT_SESSION_ID_LENGTH];
        random.nextBytes(bytes);
        // Encode the result
        char[] id = encode(bytes);
        return String.valueOf(id);
    }

    public static char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        char[] alphabet = SESSION_ID_ALPHABET;
        //
        // 3 bytes encode to 4 chars.  Output is always an even
        // multiple of 4 characters.
        //
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            //noinspection PointlessArithmeticExpression
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Random random;
    private String sessionId;

    //
    // the number of writes this session should simulate during its life time. Can be zero.
    //
    private int initialWriteCount;
    private int remainingWrites;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionSimulation() {

        this(null);
    }

    public HttpSessionSimulation(String sessionId) {

        random = new Random();
        this.sessionId = sessionId;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getSessionId() {
        return sessionId;
    }

    public HttpSessionOperation next() {

        if (sessionId == null) {

            this.sessionId = generateSessionId(random);
            return new HttpSessionCreate(this);
        }

        //
        // once the session is created, is simulates a configurable number of "writes"
        //

        if (remainingWrites > 0) {

            remainingWrites --;
            return new HttpSessionWrite(this);
        }

        return new HttpSessionInvalidate(this);
    }

    /**
     * Sets the initial write count this session simulation is supposed to perform. Once the first next() invocation
     * is performed, changing the initial write count throws an IllegalStateException
     *
     * @exception IllegalStateException if the method is invoked after the first next() invocation occured.
     */
    public void setWriteCount(int i) {

        if (i < 0) {
            throw new IllegalArgumentException("invalid write count value " + i);
        }

        if (sessionId != null) {

            //
            // the first next() was invoked
            //

            throw new IllegalStateException("cannot set write count after next() was invoked on " + this);
        }

        this.initialWriteCount = i;
        this.remainingWrites = initialWriteCount;
    }

    public int getWriteCount() {

        return initialWriteCount;
    }

    @Override
    public String toString() {

        return sessionId;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
