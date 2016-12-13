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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.Operation;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A non-blocking counter implementation. It relies on compare-ans-set non-blocking java.util.concurrent.atomic objects.
 * Provides thread safety for produced CounterValues instances.
 *
 * @see CounterValues
 */
public class NonBlockingCounter implements Counter {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(NonBlockingCounter.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Class<? extends Operation> operationType;

    private AtomicLong successCount;
    private AtomicLong cumulatedSuccessTimeNano;
    private ConcurrentMap<Class<? extends Throwable>, NonBlockingFailureCounter> failureCounters;

    // Constructors ----------------------------------------------------------------------------------------------------

    public NonBlockingCounter(Class<? extends Operation> operationType)
    {
        this.operationType = operationType;
        this.successCount = new AtomicLong(0L);
        this.cumulatedSuccessTimeNano = new AtomicLong(0L);
        this.failureCounters = new ConcurrentHashMap<>();

        log.debug(this + " created");
    }

    // Counter implementation ------------------------------------------------------------------------------------------

    @Override
    public Class<? extends Operation> getOperationType()
    {
        return operationType;
    }

    /**
     * @see Counter#update(long, long, long, Throwable...)
     */
    @Override
    public void update(long t0Ms, long t0Nano, long t1Nano, Throwable... t)
    {
        long duration = t1Nano - t0Nano;

        if (duration < 0)
        {
            throw new IllegalArgumentException("t1 " + t1Nano + " precedes t0 " + t0Nano);
        }

        if (t == null || t.length == 0 || (t[0] == null))
        {
            // success

            successCount.incrementAndGet();
            cumulatedSuccessTimeNano.addAndGet(duration);
        }
        else if (t.length > 1)
        {
            // unsupported usage
            throw new IllegalArgumentException(
                "just one throwable is allowed as argument, but " + t.length + " were provided");
        }
        else
        {
            // failure
            Class<? extends Throwable> failureType = t[0].getClass();

            NonBlockingFailureCounter failureCounter = failureCounters.get(failureType);

            if (failureCounter == null)
            {
                // only create the instance if it is *not* in the map - there's a slight change a FailureCounter
                // instance will be created unnecessarily but that is an unlikely, rare and ultimately harmless event
                failureCounter = new NonBlockingFailureCounter();
                failureCounters.putIfAbsent(failureType, failureCounter);
            }

            failureCounter.increment(duration);
        }
    }

    /**
     * @see Counter#getCounterValuesAndReset()
     */
    @Override
    public CounterValues getCounterValuesAndReset()
    {
        long sc = successCount.getAndSet(0L);
        long cstn = cumulatedSuccessTimeNano.getAndSet(0L);

        // TODO:
        //       This is not exactly atomic, the key set may change (grow) between running keySet() and acquiring
        //       statistics per failure type, but this is fine, we'll next those statistics during the next read

        Set<Class<? extends Throwable>> failureTypes = failureCounters.keySet();
        Map<Class<? extends Throwable>, ImmutableFailureCounter> failureCounterSnapshot = new HashMap<>();

        for(Class<? extends Throwable> failureType: failureTypes)
        {
            NonBlockingFailureCounter c = failureCounters.get(failureType);
            ImmutableFailureCounter ifc = c.getFailureCounterSnapshotAndReset();
            failureCounterSnapshot.put(failureType, ifc);
        }

        return new CounterValuesImpl(sc, cstn, failureCounterSnapshot);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return operationType == null ? "null" : operationType.getSimpleName();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
