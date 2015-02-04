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

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class RedisFailure
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final int OTHERS_INDEX = 0;

    // java.net.ConnectException "Connection refused"
    public static final int CONNECTION_REFUSED_INDEX = 1;

    // java.net.SocketException "Broken pipe"
    public static final int BROKEN_PIPE_INDEX = 2;

    // java.net.SocketException "Connection reset"
    public static final int CONNECTION_RESET_INDEX = 3;

    // java.net.SocketTimeoutException "connect timed out"
    public static final int CONNECT_TIMED_OUT_INDEX = 4;

    // java.net.SocketTimeoutException "Read timed out"
    public static final int READ_TIMED_OUT_INDEX = 5;

    // redis.clients.jedis.exceptions.JedisConnectionException "It seems like server has closed the connection"
    public static final int JEDIS_SERVER_CLOSED_CONNECTION_INDEX = 6;

    // redis.clients.jedis.exceptions.JedisConnectionException "Unknown reply: ..."
    public static final int JEDIS_UNKNOWN_REPLY_INDEX = 7;

    // redis.clients.jedis.exceptions.JedisDataException "ERR max number of clients reached"
    public static final int JEDIS_MAX_NUMBER_OF_CLIENTS_REACHED_INDEX = 8;

    // redis.clients.jedis.exceptions.JedisDataException "ERR Connection timed out"
    public static final int JEDIS_CONNECTION_TIMED_OUT_INDEX = 9;

    // java.util.NoSuchElementException: Timeout waiting for idle object
    public static final int POOL_OUT_OF_INSTANCES_INDEX = 10;

    // includes the "others" failures
    public static final int FAILURE_TYPES_COUNT = 11;

    private static final int CSV_HEADER = 0;
    private static final int CLASS_NAME = 1;
    private static final int ERROR_MESSAGE = 2;

    public static final String[][] LITERALS = new String[][]
        {
            { "other failures count",
                "",
                "" },
            { "'connection refused' count",
                "java.net.ConnectException",
                "Connection refused" },
            { "'broken pipe' count",
                "java.net.SocketException",
                "Broken pipe" },
            { "'connection reset' count",
                "java.net.SocketException",
                "Connection reset" },
            { "'connect timed out' count",
                "java.net.SocketTimeoutException",
                "connect timed out" },
            { "'read timed out' count",
                "java.net.SocketTimeoutException",
                "Read timed out" },
            { "jedis 'closed connection' count",
                "redis.clients.jedis.exceptions.JedisConnectionException",
                "It seems like server has closed the connection" },
            { "jedis 'unknown reply' count",
                "redis.clients.jedis.exceptions.JedisConnectionException",
                "Unknown reply" },
            { "jedis 'max number of clients reached' count",
                "redis.clients.jedis.exceptions.JedisDataException",
                "ERR max number of clients reached" },
            { "jedis 'connection timed out' count",
                "redis.clients.jedis.exceptions.JedisDataException",
                "ERR Connection timed out" },
            { "pool out count",
                "java.util.NoSuchElementException",
                "Timeout waiting for idle object" },
        };

    public static final Long[] EMPTY_COUNTERS;
    public static final long[] EMPTY_PRIMITIVE_COUNTERS;
    public static final Long[] NULL_COUNTERS;

    static
    {
        EMPTY_COUNTERS = new Long[FAILURE_TYPES_COUNT];
        Arrays.fill(EMPTY_COUNTERS, 0L);
        EMPTY_PRIMITIVE_COUNTERS = new long[FAILURE_TYPES_COUNT];
        Arrays.fill(EMPTY_PRIMITIVE_COUNTERS, 0L);
        NULL_COUNTERS = new Long[FAILURE_TYPES_COUNT];
    }

    // Static ----------------------------------------------------------------------------------------------------------

    public static int toFailureIndex(Throwable t)
    {
        //noinspection ThrowableResultOfMethodCallIgnored
        Throwable root = Util.getRoot(t);

        String msg = root.getMessage();

        if (root instanceof ConnectException)
        {
            if (LITERALS[CONNECTION_REFUSED_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return CONNECTION_REFUSED_INDEX;
            }
        }

        if (root instanceof SocketException)
        {
            if (LITERALS[BROKEN_PIPE_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return BROKEN_PIPE_INDEX;
            }
            else if (LITERALS[CONNECTION_RESET_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return CONNECTION_RESET_INDEX;
            }
        }

        if (root instanceof SocketTimeoutException)
        {
            if (LITERALS[CONNECT_TIMED_OUT_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return CONNECT_TIMED_OUT_INDEX;
            }
            else if (LITERALS[READ_TIMED_OUT_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return READ_TIMED_OUT_INDEX;
            }
        }

        if (root instanceof JedisConnectionException)
        {
            if (msg != null)
            {
                if (msg.startsWith(LITERALS[JEDIS_SERVER_CLOSED_CONNECTION_INDEX][ERROR_MESSAGE]))
                {
                    return JEDIS_SERVER_CLOSED_CONNECTION_INDEX;
                }
                else if (msg.startsWith(LITERALS[JEDIS_UNKNOWN_REPLY_INDEX][ERROR_MESSAGE]))
                {
                    return JEDIS_UNKNOWN_REPLY_INDEX;
                }
            }
        }

        if (root instanceof JedisDataException)
        {
            if (msg != null)
            {
                if (msg.startsWith(LITERALS[JEDIS_MAX_NUMBER_OF_CLIENTS_REACHED_INDEX][ERROR_MESSAGE]))
                {
                    return JEDIS_MAX_NUMBER_OF_CLIENTS_REACHED_INDEX;
                }

                if (msg.startsWith(LITERALS[JEDIS_CONNECTION_TIMED_OUT_INDEX][ERROR_MESSAGE]))
                {
                    return JEDIS_CONNECTION_TIMED_OUT_INDEX;
                }
            }
        }

        if (root instanceof NoSuchElementException)
        {
            if (LITERALS[POOL_OUT_OF_INSTANCES_INDEX][ERROR_MESSAGE].equals(msg))
            {
                return POOL_OUT_OF_INSTANCES_INDEX;
            }
        }

        System.err.println("[warning] unknown redis failure, top-most: " + t + ", root: " + root);

        return OTHERS_INDEX;
    }

    public static String toFailure(int index)
    {
        if (0 <= index && index < FAILURE_TYPES_COUNT)
        {
            return LITERALS[index][CLASS_NAME] + ": " + LITERALS[index][ERROR_MESSAGE];
        }

        throw new IllegalArgumentException("unknown RedisFailure index: " + index);
    }

    public static String toHeader(int index)
    {
        if (0 <= index && index < FAILURE_TYPES_COUNT)
        {
            return LITERALS[index][CSV_HEADER];
        }

        throw new IllegalArgumentException("unknown RedisFailure index: " + index);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
