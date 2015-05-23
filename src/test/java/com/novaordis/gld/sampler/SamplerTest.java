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

import com.novaordis.gld.strategy.load.cache.AnotherTypeOfMockOperation;
import com.novaordis.gld.strategy.load.cache.MockOperation;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class SamplerTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplerTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void lifecycle() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        assertFalse(sampler.isStarted());

        long defaultSamplingInterval = sampler.getSamplingIntervalMs();

        assertTrue(defaultSamplingInterval > 0);

        try
        {
            sampler.record(0L, 0L, 0L, new MockOperation());
            fail("should throw IllegalStateException on account of calling record() on a stopped sampler");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        Counter counter = sampler.registerOperation(MockOperation.class);
        assertNotNull(counter);

        final List<SamplingInterval> samplingIntervals = new ArrayList<>();

        assertTrue(sampler.registerConsumer(new SamplingConsumer()
        {
            public void consume(SamplingInterval samplingInterval)
            {
                // simply accumulate the sampling intervals
                samplingIntervals.add(samplingInterval);
            }

        }));

        long testSamplingTaskRunInterval = 250L;
        sampler.setSamplingTaskRunIntervalMs(testSamplingTaskRunInterval);
        assertEquals(testSamplingTaskRunInterval, sampler.getSamplingTaskRunIntervalMs());

        long testSamplingInterval = 500L;
        sampler.setSamplingIntervalMs(testSamplingInterval);
        assertEquals(testSamplingInterval, sampler.getSamplingIntervalMs());

        sampler.start();

        assertTrue(sampler.isStarted());

        // test idempotency
        sampler.start();

        // send an operation. One operation only.

        sampler.record(0L, 0L, 7L, new MockOperation());

        // wait 2 x sampling interval + 100ms, to make sure the operation is accounted for
        long sleep = 2 * testSamplingInterval + 100L;
        log.info("sleeping for " + sleep + " ms ...");
        Thread.sleep(sleep);

        sampler.stop();

        assertFalse(sampler.isStarted());

        // test idempotency
        sampler.stop();

        assertFalse(sampler.isStarted());

        // try one more record()

        try
        {
            sampler.record(0L, 0L, 0L, new MockOperation());
            fail("should throw IllegalStateException on account of calling record() on a stopped sampler");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        // make sure the generated samples reflect the one operation sent into the sampler
        assertFalse(samplingIntervals.isEmpty());

        int totalCount = 0;

        for(SamplingInterval si: samplingIntervals)
        {
            totalCount += si.getCounterValues(MockOperation.class).getSuccessCount();
        }

        assertEquals(1, totalCount);
    }

    @Test
    public void unknownOperation() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        sampler.registerOperation(MockOperation.class);

        sampler.start();

        try
        {
            // try recording an unknown operation
            sampler.record(0L, 0L, 0L, new AnotherTypeOfMockOperation());
            fail("should throw IllegalArgumentException on account of trying to register an unknown operation");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            sampler.stop();
        }
    }

    @Test
    public void samplingIntervalSmallerThanSamplingTaskRunInterval() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        sampler.setSamplingIntervalMs(2000L);

        try
        {
            sampler.setSamplingTaskRunIntervalMs(3000L);
            fail("should throw IllegalArgumentException because of invalid relationship between sampling intervals");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            sampler.stop();
        }
    }

    @Test
    public void samplingIntervalEqualsThanSamplingTaskRunInterval() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        sampler.setSamplingIntervalMs(2000L);

        try
        {
            sampler.setSamplingTaskRunIntervalMs(2000L);
            fail("should throw IllegalArgumentException because of invalid relationship between sampling intervals");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            sampler.stop();
        }
    }

    @Test
    public void attemptToRegisterAnOperationAfterTheSamplerWasStarted() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        sampler.registerOperation(MockOperation.class);

        sampler.start();

        try
        {
            sampler.registerOperation(AnotherTypeOfMockOperation.class);
            fail("should throw IllegalStateException on account of trying to register an operation after startup");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            sampler.stop();
        }
    }

    @Test
    public void cantModifyTheSamplingIntervalAfterSamplerWasStarted() throws Exception
    {
        Sampler s = getSamplerToTest();

        s.registerOperation(MockOperation.class);

        s.start();

        assertTrue(s.isStarted());

        try
        {
            s.setSamplingIntervalMs(100L);
            fail("should fail, can't modify the sampling interval after the sampler was started");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        s.stop();

        assertFalse(s.isStarted());
    }

    @Test
    public void cantModifyTheSamplingThreadRunIntervalAfterSamplerWasStarted() throws Exception
    {
        Sampler s = getSamplerToTest();

        s.registerOperation(MockOperation.class);

        s.start();

        assertTrue(s.isStarted());

        try
        {
            s.setSamplingTaskRunIntervalMs(100L);
            fail("should fail, can't modify the sampling thread run interval after the sampler was started");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }

        s.stop();

        assertFalse(s.isStarted());
    }

    @Test
    public void attemptToStartTheSamplerWithoutAnyRegisteredOperation() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        try
        {
            sampler.start();
            fail("should throw IllegalStateException on account of trying to start without any registered operation");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
        finally
        {
            sampler.stop();
        }
    }

    @Test
    public void attemptToRegisterANonOperationType() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        try
        {
            sampler.registerOperation(Object.class);
            fail("should throw IllegalArgumentException on account of trying to register an invalid operation type");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void successfulOperationRegistration() throws Exception
    {
        Sampler sampler = getSamplerToTest();

        Counter counter = sampler.registerOperation(MockOperation.class);
        Counter counter2 = sampler.registerOperation(AnotherTypeOfMockOperation.class);

        assertEquals(counter, sampler.getCounter(MockOperation.class));
        assertEquals(counter2, sampler.getCounter(AnotherTypeOfMockOperation.class));
    }

    @Test
    public void annotate() throws Exception
    {
        Sampler sampler = getSamplerToTest();
        sampler.registerOperation(MockOperation.class);
        final List<SamplingInterval> sil = new ArrayList<>();
        sampler.registerConsumer(new SamplingConsumer()
        {
            @Override
            public void consume(SamplingInterval samplingInterval)
            {
                sil.add(samplingInterval);
            }
        });
        sampler.start();

        sampler.annotate("test");

        sampler.stop();

        // at least one of the samples has the comment, but not more than one
        String annotation = null;
        for(SamplingInterval si: sil)
        {
            List<String> annotations = si.getAnnotations();
            assertTrue(annotations.size() <= 1);
            if (annotations.isEmpty())
            {
                continue;
            }

            if (annotation != null)
            {
                fail("annotation found twice: " + annotation + ", " + annotations.get(0));
            }

            annotation = annotations.get(0);
        }

        assertEquals("test", annotation);
    }

    @Test
    public void stop_waitForAFullIntervalAndGenerateAFinalSample() throws Exception
    {
        Sampler sampler = getSamplerToTest();
        sampler.registerOperation(MockOperation.class);
        final List<SamplingInterval> sil = new ArrayList<>();
        sampler.registerConsumer(new SamplingConsumer()
        {
            @Override
            public void consume(SamplingInterval samplingInterval)
            {
                sil.add(samplingInterval);
            }
        });
        sampler.start();

        sampler.annotate("test");
        sampler.record(0L, 1L, 2L, new MockOperation());

        sampler.stop();

        assertFalse(sil.isEmpty());

        int count = 0;
        String annotation = null;

        for(SamplingInterval i: sil)
        {
            count += i.getCounterValues(MockOperation.class).getSuccessCount();

            for(String a: i.getAnnotations())
            {
                if (annotation != null)
                {
                    fail("more than one annotation");
                }

                annotation = a;
            }
        }

        assertEquals(1, count);
        assertEquals("test", annotation);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Sampler getSamplerToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
