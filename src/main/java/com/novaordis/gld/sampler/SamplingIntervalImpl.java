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
import com.novaordis.gld.sampler.metrics.MeasureUnit;
import com.novaordis.gld.sampler.metrics.Metric;
import org.apache.log4j.Logger;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A standard counter implementation, that measures success count, cumulated time (in nanoseconds) and failures.
 */
public class SamplingIntervalImpl implements SamplingInterval
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplingIntervalImpl.class);

    public static final Format TIMESTAMP_DISPLAY_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss,SSS");

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long intervalStartTimestamp;
    private long durationMs;
    private Set<Class<? extends Operation>> operationTypes;
    private Map<Class<? extends Operation>, CounterValuesImpl> values;
    private List<String> annotations;
    private List<Number> metricValues;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param durationMs - the interval duration in milliseconds.
     * @param operationTypes  - the types of the operations sampled in this interval. null or empty set is not
     *                        acceptable, we must have at least one operation type we collect statistics for
     *
     * @see SamplingIntervalImpl(long, long, Set, List)
     */
    public SamplingIntervalImpl(long intervalStartTimestampMs, long durationMs,
                                Set<Class<? extends Operation>> operationTypes)
    {
        this.intervalStartTimestamp = intervalStartTimestampMs;
        this.durationMs = durationMs;

        if (operationTypes == null)
        {
            throw new IllegalArgumentException("null operation types");
        }

        if (operationTypes.isEmpty())
        {
            throw new IllegalArgumentException("no operation types specified");
        }

        this.operationTypes = new HashSet<>();
        this.values = new HashMap<>();

        // insure the operation types are valid and initialize the values map with zero

        for(Class<? extends Operation> c: operationTypes)
        {
            this.operationTypes.add(c);
            this.values.put(c, new CounterValuesImpl());
        }

        this.annotations = new ArrayList<>();

        log.debug(this + " created");
    }

    // SamplingInterval implementation ---------------------------------------------------------------------------------

    /**
     * @see SamplingInterval#getStartMs()
     */
    @Override
    public long getStartMs()
    {
        return intervalStartTimestamp;
    }

    /**
     * @see SamplingInterval#getDurationMs()
     */
    @Override
    public long getDurationMs()
    {
        return durationMs;
    }

    /**
     * @see SamplingInterval#getEndMs()
     */
    @Override
    public long getEndMs()
    {
        return intervalStartTimestamp + durationMs;
    }

    /**
     * @see SamplingInterval#getOperationTypes()
     */
    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        return operationTypes;
    }

    /**
     * @see SamplingInterval#getCounterValues(Class)
     */
    @Override
    public CounterValues getCounterValues(Class<? extends Operation> operationType)
    {
        return values.get(operationType);
    }

    @Override
    public List<String> getAnnotations()
    {
        return annotations;
    }

    @Override
    public List<Number> getMetricValues()
    {
        return metricValues;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Sets the CounterValues instance for the given operationType with the given values, throwing away any other
     * associated CounterValue instance.
     *
     * The implementation operates under the assumption that the only thread invoking this method is the sampling
     * thread, so the implementation is NOT thread safe.
     */
    public void setCounterValues(Class<? extends Operation> operationType, CounterValuesImpl counterValues)
    {
        values.put(operationType, counterValues);
    }

    /**
     * If no CounterValues instance is associated with the given operation type, sets the given values (the same
     * semantics as the setCounterValues() method. Otherwise, it increments the existing counters with the given values.
     *
     * The implementation operates under the assumption that the only thread invoking this method is the sampling
     * thread, so the implementation is NOT thread safe.
     *
     * @see SamplingIntervalImpl#setCounterValues(Class, CounterValuesImpl)
     */
    public void incrementCounterValues(Class<? extends Operation> operationType, CounterValues counterValues)
    {
        CounterValuesImpl cvs = values.get(operationType);

        if (cvs == null)
        {
            cvs = new CounterValuesImpl();
            values.put(operationType, cvs);
        }

        cvs.incrementWith(counterValues);
    }

    public void addAnnotation(String s)
    {
        annotations.add(s);
    }

    public void setMetrics(List<Number> metricValues)
    {
        this.metricValues = metricValues;
    }

    @Override
    public String toString()
    {
        return "[" +
            TIMESTAMP_DISPLAY_FORMAT.format(intervalStartTimestamp) + " - " +
            TIMESTAMP_DISPLAY_FORMAT.format(intervalStartTimestamp + durationMs) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
