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
    private Set<Class> operationTypes;
    private Map<Class, CounterValues> values;
    private List<String> annotations;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @see SamplingIntervalImpl(long, long, Set, List)
     */
    public SamplingIntervalImpl(long intervalStartTimestamp, long durationMs, Set<Class> operationTypes)
    {
        this(intervalStartTimestamp, durationMs, operationTypes, null);
    }

    /**
     * @param durationMs - the interval duration in milliseconds.
     * @param operationTypes  - the types of the operations sampled in this interval. null or empty set is not
     *                        acceptable, we must have at least one operation type we collect statistics for
     *
     * @param annotations null or empty list are valid values
     */
    public SamplingIntervalImpl(
        long intervalStartTimestamp, long durationMs, Set<Class> operationTypes, List<String> annotations)
    {
        this.intervalStartTimestamp = intervalStartTimestamp;
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

        for(Class c: operationTypes)
        {
            if (!Operation.class.isAssignableFrom(c))
            {
                throw new IllegalArgumentException(c + " is not an Operation");
            }

            this.operationTypes.add(c);
            this.values.put(c, new CounterValuesImpl());
        }

        if (annotations == null)
        {
            this.annotations = new ArrayList<>();
        }
        else
        {
            this.annotations = annotations;
        }

        log.debug(this + " created");
    }

    // SamplingInterval implementation ---------------------------------------------------------------------------------

    @Override
    public long getTimestamp()
    {
        return intervalStartTimestamp;
    }

    /**
     * @return the interval duration in milliseconds.
     */
    @Override
    public long getDuration()
    {
        return durationMs;
    }

    @Override
    public Set<Class> getOperationTypes()
    {
        return operationTypes;
    }

    /**
     * @see SamplingInterval#getCounterValues(Class)
     */
    @Override
    public CounterValues getCounterValues(Class operationType)
    {
        return values.get(operationType);
    }

    @Override
    public List<String> getAnnotations()
    {
        return annotations;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setCounterValues(Class operationType, CounterValues counterValues)
    {
        values.put(operationType, counterValues);
    }

    public void addAnnotation(String s)
    {
        annotations.add(s);
    }

    @Override
    public String toString()
    {
        return "[" + TIMESTAMP_DISPLAY_FORMAT.format(intervalStartTimestamp) + " ... ]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
