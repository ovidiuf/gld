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

package com.novaordis.gld.mock;

import com.novaordis.gld.Operation;
import com.novaordis.gld.sampler.Counter;
import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.sampler.SamplingConsumer;
import com.novaordis.gld.sampler.metrics.Metric;
import org.apache.log4j.Logger;

public class MockSampler implements Sampler
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockSampler.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Sampler implementation ------------------------------------------------------------------------------------------

    @Override
    public void setSamplingIntervalMs(long ms)
    {
        throw new RuntimeException("setSamplingIntervalMs() NOT YET IMPLEMENTED");
    }

    @Override
    public long getSamplingIntervalMs()
    {
        throw new RuntimeException("getSamplingIntervalMs() NOT YET IMPLEMENTED");
    }

    @Override
    public void setSamplingTaskRunIntervalMs(long ms)
    {
        throw new RuntimeException("setSamplingTaskRunIntervalMs() NOT YET IMPLEMENTED");
    }

    @Override
    public long getSamplingTaskRunIntervalMs()
    {
        throw new RuntimeException("getSamplingTaskRunIntervalMs() NOT YET IMPLEMENTED");
    }

    @Override
    public Counter registerOperation(Class<? extends Operation> operationType)
    {
        throw new RuntimeException("registerOperation() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean registerConsumer(SamplingConsumer consumer)
    {
        throw new RuntimeException("registerConsumer() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean registerMetric(Class<? extends Metric> metricType)
    {
        throw new RuntimeException("registerMetric() NOT YET IMPLEMENTED");
    }

    @Override
    public void start()
    {
        log.info(this + " started");
        throw new RuntimeException("start() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isStarted()
    {
        throw new RuntimeException("isStarted() NOT YET IMPLEMENTED");
    }

    @Override
    public void stop()
    {
        throw new RuntimeException("stop() NOT YET IMPLEMENTED");
    }

    @Override
    public void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable... t)
    {
        throw new RuntimeException("record() NOT YET IMPLEMENTED");
    }

    @Override
    public void annotate(String line)
    {
        throw new RuntimeException("annotate() NOT YET IMPLEMENTED");
    }

    @Override
    public Counter getCounter(Class<? extends Operation> operationType)
    {
        throw new RuntimeException("getCounter() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
