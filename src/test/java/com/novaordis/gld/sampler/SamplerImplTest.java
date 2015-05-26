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

import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SamplerImplTest extends SamplerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void negativeSamplerTaskRunInterval() throws Exception
    {
        SamplerImpl si = new SamplerImpl(-1L, 1000L);
        si.registerOperation(MockOperation.class);
        si.start();
        assertTrue(si.isStarted());
        si.stop();
        assertFalse(si.isStarted());
    }

    @Test
    public void zeroSamplerTaskRunInterval() throws Exception
    {
        SamplerImpl si = new SamplerImpl(0L, 1000L);
        si.registerOperation(MockOperation.class);
        si.start();
        assertTrue(si.isStarted());
        si.stop();
        assertFalse(si.isStarted());
    }

    @Test
    public void exceptionInRunDoesNotPreventReleasingTheMutex() throws Exception
    {
        // start the sampler with a very large sampling interval, so the stop timeout will be very large; hoewever,
        // keep the sampling thread run interval small
        long twoDays = 2L * 24 * 60 * 60 * 1000L;
        SamplerImpl si = new SamplerImpl(250L, twoDays);
        si.registerOperation(MockOperation.class);

        si.start();

        assertTrue(si.isStarted());

        // "break" the sampler, so when run() is invoked, it'll throw an exception. Setting the consumers to
        // null will cause an NPE

        si.setConsumers(null);

        log.info(si + " has been broken ...");

        // attempt to stop, the stop must not block indefinitely, if it does, the JUnit will kill the test and fail

        long t0 = System.currentTimeMillis();

        si.stop();

        long t1 = System.currentTimeMillis();

        log.info("the sampler stopped, it took " + (t1 - t0) + " ms to stop the sampler");
    }

    // simulated runs --------------------------------------------------------------------------------------------------

    /**
     * Note: this test is a remnant of work that since migrated in SamplerImplWorkBenchTest.
     *
     * @see com.novaordis.gld.sampler.SamplerImplWorkBenchTest
     */
    @Test
    public void simulatedStepByStepSamplingCollection() throws Exception
    {
        long samplingInterval = 10 * 1000L; // 10 seconds to allow us time to experiment

        // the sampling task run interval is 0, meaning no timer task will be registered
        SamplerImpl s = new SamplerImpl(0L, samplingInterval);
        s.registerOperation(MockOperation.class);
        s.registerConsumer(new SamplingConsumer()
        {
            @Override
            public void consume(SamplingInterval... sis)
            {
                // noop
            }
        });

        assertTrue(s.getLastRunTimestamp() <= 0);

        // we're not actually starting anything because the sampling task interval is 0, but the sampler will look
        // like it started; this will also run the first initialization run()
        s.start();

        assertTrue(s.isStarted());

        assertTrue(s.getLastRunTimestamp() > 0);

        assertNull(s.getCurrent());

        // sampling interval initialization
        s.run();
        SamplingInterval current = s.getCurrent();
        assertNotNull(current);

        long ts = current.getStartMs();
        // make sure it's rounded on the second
        assertEquals(0L, ts - ((ts / 1000) * 1000L));

        // first collection run, should not collect anything
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingInterval, current.getDurationMs());
        Set<Class<? extends Operation>> operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        assertTrue(current.getAnnotations().isEmpty());
        CounterValues cvs = current.getCounterValues(MockOperation.class);
        assertEquals(0L, cvs.getSuccessCount());
        assertEquals(0L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(0L, cvs.getFailureCount());
        assertEquals(0L, cvs.getFailureCumulatedDurationNano());

        // record a success and a failure
        s.record(System.currentTimeMillis(), 1L, 2L, new MockOperation()); // 1
        s.record(System.currentTimeMillis(), 3L, 5L, new MockOperation(), new SocketException()); // 2

        // another collection run, it should collect both the success and the failure
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingInterval, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        assertTrue(current.getAnnotations().isEmpty());
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(1L, cvs.getSuccessCount());
        assertEquals(1L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(1L, cvs.getFailureCount());
        assertEquals(2L, cvs.getFailureCumulatedDurationNano());
        Set<Class<? extends Throwable>> failureTypes = cvs.getFailureTypes();
        assertEquals(1, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(1L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L, cvs.getFailureCumulatedDurationNano(SocketException.class));

        // record a success, the same type a failure, and an annotation
        s.record(System.currentTimeMillis(), 6L, 9L, new MockOperation()); // 3
        s.record(System.currentTimeMillis(), 10L, 14L, new MockOperation(), new SocketException()); // 4
        s.annotate("annotation 1");

        // another collection run
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingInterval, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        List<String> annotations = current.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains("annotation 1"));
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(2L, cvs.getSuccessCount());
        assertEquals(1L + 3L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(2L, cvs.getFailureCount());
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano());
        failureTypes = cvs.getFailureTypes();
        assertEquals(1, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertEquals(2L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano(SocketException.class));

        // record a success, a different type a failure, and another annotation
        s.record(System.currentTimeMillis(), 15L, 20L, new MockOperation()); // 5
        s.record(System.currentTimeMillis(), 21L, 27L, new MockOperation(), new ConnectException()); // 6
        s.annotate("annotation 2");

        // another collection run
        s.run();

        current = s.getCurrent();
        assertEquals(ts, current.getStartMs()); // insure it's the same sampling interval
        assertEquals(samplingInterval, current.getDurationMs());
        operationTypes = current.getOperationTypes();
        assertEquals(1, operationTypes.size());
        assertTrue(operationTypes.contains(MockOperation.class));
        annotations = current.getAnnotations();
        assertEquals(2, annotations.size());
        assertEquals("annotation 1", annotations.get(0));
        assertEquals("annotation 2", annotations.get(1));
        cvs = current.getCounterValues(MockOperation.class);
        assertEquals(3L, cvs.getSuccessCount());
        assertEquals(1L + 3L + 5L, cvs.getSuccessCumulatedDurationNano());
        assertEquals(3L, cvs.getFailureCount());
        assertEquals(2L + 4L + 6L, cvs.getFailureCumulatedDurationNano());
        failureTypes = cvs.getFailureTypes();
        assertEquals(2, failureTypes.size());
        assertTrue(failureTypes.contains(SocketException.class));
        assertTrue(failureTypes.contains(ConnectException.class));
        assertEquals(2L, cvs.getFailureCount(SocketException.class));
        assertEquals(2L + 4L, cvs.getFailureCumulatedDurationNano(SocketException.class));
        assertEquals(1L, cvs.getFailureCount(ConnectException.class));
        assertEquals(6L, cvs.getFailureCumulatedDurationNano(ConnectException.class));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected SamplerImpl getSamplerToTest() throws Exception
    {
        return new SamplerImpl();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
