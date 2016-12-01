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
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;


public class RedisFailureTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(RedisFailureTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // toFailureIndex() ------------------------------------------------------------------------------------------------

    @Test
    public void toFailureIndex_RandomExcpetion() throws Exception
    {
        Throwable t = new Exception("test", new RuntimeException("test 2", new NullPointerException("something")));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.OTHERS_INDEX == i);
    }


    @Test
    public void toFailureIndex_ConnectionRefused() throws Exception
    {
        Throwable t = new Exception("test", new ConnectException("Connection refused"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.CONNECTION_REFUSED_INDEX == i);
    }

    @Test
    public void toFailureIndex_BrokenPipe() throws Exception
    {
        Throwable t = new Exception("test", new SocketException("Broken pipe"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.BROKEN_PIPE_INDEX == i);
    }

    @Test
    public void toFailureIndex_ConnectionReset() throws Exception
    {
        Throwable t = new Exception("test", new SocketException("Connection reset"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.CONNECTION_RESET_INDEX == i);
    }

    @Test
    public void toFailureIndex_ConnectTimedOut() throws Exception
    {
        Throwable t = new Exception("test", new SocketTimeoutException("connect timed out"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.CONNECT_TIMED_OUT_INDEX == i);
    }

    @Test
    public void toFailureIndex_ReadTimedOut() throws Exception
    {
        Throwable t = new Exception("test", new SocketTimeoutException("Read timed out"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.READ_TIMED_OUT_INDEX == i);
    }

    @Test
    public void toFailureIndex_JedisServerClosedConnection() throws Exception
    {
        Throwable t = new Exception("test",
            new JedisConnectionException("It seems like server has closed the connection"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.JEDIS_SERVER_CLOSED_CONNECTION_INDEX == i);
    }

    @Test
    public void toFailureIndex_JedisUnknownReply() throws Exception
    {
        Throwable t = new Exception("test", new JedisConnectionException("Unknown reply: something"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.JEDIS_UNKNOWN_REPLY_INDEX == i);
    }

    @Test
    public void toFailureIndex_MaxNumberOfClientsReached() throws Exception
    {
        Throwable t = new Exception("test", new JedisDataException("ERR max number of clients reached"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.JEDIS_MAX_NUMBER_OF_CLIENTS_REACHED_INDEX == i);
    }

    @Test
    public void toFailureIndex_JedisConnectionTimedOutIndex() throws Exception
    {
        Throwable t = new Exception("test", new JedisDataException("ERR Connection timed out"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.JEDIS_CONNECTION_TIMED_OUT_INDEX == i);
    }

    @Test
    public void toFailureIndex_PoolOutOfInstances() throws Exception
    {
        Throwable t = new Exception("test", new NoSuchElementException("Timeout waiting for idle object"));
        int i = RedisFailure.toFailureIndex(t);

        assertTrue(RedisFailure.POOL_OUT_OF_INSTANCES_INDEX == i);
    }

    // toHeader() ------------------------------------------------------------------------------------------------------

    @Test
    public void toHeaderUnknownIndex() throws Exception
    {
        try
        {
            RedisFailure.toHeader(-1);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toHeaderUnknownIndex2() throws Exception
    {
        try
        {
            RedisFailure.toHeader(10240);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
