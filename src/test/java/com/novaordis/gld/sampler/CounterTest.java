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

package com.novaordis.gld.sampler;

import com.novaordis.gld.Operation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.BindException;
import java.net.SocketException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class CounterTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CounterTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void t1PrecedesT0() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        try
        {
            c.update(0L, 10L, 9L);
            fail("should fail because t1 precedes t0");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void moreThanOneThrowable() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        try
        {
            c.update(0L, 10L, 11L, new Exception(), new Exception());
            fail("should fail, more than one throwable given");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void happyPath_Success() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        c.update(0L, 10L, 11L);
        c.update(1L, 10L, 12L);
        c.update(2L, 10L, 13L);

        assertEquals(MockOperation.class, c.getOperationType());

        CounterValues v0 = c.getCounterValuesAndReset();

        long sc0 = v0.getSuccessCount();
        long sct0 = v0.getSuccessCumulatedDurationNano();

        assertEquals(3, sc0);
        assertEquals(1L + 2L + 3L, sct0);

        assertEquals(0, v0.getFailureCount());
        assertEquals(0, v0.getFailureCumulatedDurationNano());
        assertTrue(v0.getFailureTypes().isEmpty());
        assertEquals(0, v0.getFailureCount(Throwable.class));

        c.update(3L, 10L, 14L);

        CounterValues v1 = c.getCounterValuesAndReset();

        long sc1 = v1.getSuccessCount();
        long sct1 = v1.getSuccessCumulatedDurationNano();

        assertEquals(1, sc1);
        assertEquals(4L, sct1);

        assertEquals(0, v1.getFailureCount());
        assertEquals(0, v1.getFailureCumulatedDurationNano());
        assertTrue(v1.getFailureTypes().isEmpty());
        assertEquals(0, v1.getFailureCount(Throwable.class));

        CounterValues v2 = c.getCounterValuesAndReset();

        long sc2 = v2.getSuccessCount();
        long sct2 = v2.getSuccessCumulatedDurationNano();

        assertEquals(0, sc2);
        assertEquals(0L, sct2);

        assertEquals(0, v2.getFailureCount());
        assertEquals(0, v2.getFailureCumulatedDurationNano());
        assertTrue(v2.getFailureTypes().isEmpty());
        assertEquals(0, v2.getFailureCount(Throwable.class));
    }

    @Test
    public void happyPath_Failures() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        c.update(0L, 10L, 11L, new SocketException());
        c.update(1L, 10L, 12L, new BindException());
        c.update(2L, 10L, 13L, new SocketException());

        assertEquals(MockOperation.class, c.getOperationType());

        CounterValues v0 = c.getCounterValuesAndReset();
        assertEquals(0, v0.getSuccessCount());
        assertEquals(0L, v0.getSuccessCumulatedDurationNano());
        assertEquals(3, v0.getFailureCount());
        assertEquals(1L + 2L + 3L, v0.getFailureCumulatedDurationNano());
        Set<Class<? extends Throwable>> failureTypes = v0.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertEquals(2, v0.getFailureCount(SocketException.class));
        assertEquals(1, v0.getFailureCount(BindException.class));
        assertEquals(0, v0.getFailureCount(Throwable.class));

        c.update(3L, 10L, 14L, new SocketException());

        CounterValues v1 = c.getCounterValuesAndReset();
        assertEquals(0, v1.getSuccessCount());
        assertEquals(0L, v1.getSuccessCumulatedDurationNano());
        assertEquals(1, v1.getFailureCount());
        assertEquals(4L, v1.getFailureCumulatedDurationNano());
        failureTypes = v1.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(BindException.class));
        assertEquals(1, v1.getFailureCount(SocketException.class));
        assertEquals(0, v1.getFailureCount(BindException.class));
        assertEquals(0, v1.getFailureCount(Throwable.class));

        CounterValues v2 = c.getCounterValuesAndReset();

        assertEquals(0, v2.getSuccessCount());
        assertEquals(0L, v2.getSuccessCumulatedDurationNano());

        assertEquals(0, v2.getFailureCount());
        assertEquals(0L, v2.getFailureCumulatedDurationNano());
        failureTypes = v2.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(BindException.class));
        assertEquals(0, v2.getFailureCount(SocketException.class));
        assertEquals(0, v2.getFailureCount(BindException.class));
        assertEquals(0, v2.getFailureCount(Throwable.class));
    }

    @Test
    public void happyPath_Success_and_Failures() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        c.update(0L, 10L, 11L);
        c.update(1L, 10L, 12L);
        c.update(2L, 10L, 13L, new SocketException());
        c.update(3L, 10L, 14L);
        c.update(4L, 10L, 15L, new BindException());
        c.update(5L, 10L, 16L);
        c.update(6L, 10L, 17L, new SocketException());
        c.update(7L, 10L, 18L);

        assertEquals(MockOperation.class, c.getOperationType());

        CounterValues v0 = c.getCounterValuesAndReset();

        assertEquals(5, v0.getSuccessCount());
        assertEquals(1L + 2L + 4L + 6L + 8L, v0.getSuccessCumulatedDurationNano());
        assertEquals(3, v0.getFailureCount());
        assertEquals(3L + 5L + 7L, v0.getFailureCumulatedDurationNano());
        Set<Class<? extends Throwable>> failureTypes = v0.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertEquals(2, v0.getFailureCount(SocketException.class));
        assertEquals(1, v0.getFailureCount(BindException.class));
        assertEquals(0, v0.getFailureCount(Throwable.class));

        c.update(8L, 10L, 11L);
        c.update(9L, 10L, 12L, new SocketException());
        c.update(10L, 10L, 13L);

        CounterValues v1 = c.getCounterValuesAndReset();

        assertEquals(2, v1.getSuccessCount());
        assertEquals(1L + 3L, v1.getSuccessCumulatedDurationNano());
        assertEquals(1, v1.getFailureCount());
        assertEquals(2L, v1.getFailureCumulatedDurationNano());
        failureTypes = v1.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(BindException.class));
        assertEquals(1, v1.getFailureCount(SocketException.class));
        assertEquals(0, v1.getFailureCount(BindException.class));
        assertEquals(0, v1.getFailureCount(Throwable.class));

        CounterValues v2 = c.getCounterValuesAndReset();

        assertEquals(0, v2.getSuccessCount());
        assertEquals(0L, v2.getSuccessCumulatedDurationNano());
        assertEquals(0, v2.getFailureCount());
        assertEquals(0L, v2.getFailureCumulatedDurationNano());
        failureTypes = v2.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(BindException.class));
        assertEquals(0, v2.getFailureCount(SocketException.class));
        assertEquals(0, v2.getFailureCount(BindException.class));
        assertEquals(0, v2.getFailureCount(Throwable.class));
    }

    @Test
    public void updateImmuneToTrailingNull() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        // this should be fine
        c.update(0L, 2L, 4L, null);

        CounterValues cv = c.getCounterValuesAndReset();

        assertEquals(1, cv.getSuccessCount());
        assertEquals(2L, cv.getSuccessCumulatedDurationNano());
    }

    @Test
    public void updateImmuneToTrailingNullWhenWrappedInAnArray() throws Exception
    {
        Counter c = getCounterToTest(MockOperation.class);

        Exception e = null;

        // this should be fine
        c.update(0L, 2L, 4L, e);

        CounterValues cv = c.getCounterValuesAndReset();

        assertEquals(1, cv.getSuccessCount());
        assertEquals(2L, cv.getSuccessCumulatedDurationNano());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Counter getCounterToTest(Class<? extends Operation> operationType) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
