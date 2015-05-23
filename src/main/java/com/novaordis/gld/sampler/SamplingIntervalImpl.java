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

import org.apache.log4j.Logger;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<Class, Long> successCount;
    private List<String> annotations;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param durationMs - the interval duration in milliseconds.
     * @param operationTypes  - the types of the operations sampled in this interval.
     */
    public SamplingIntervalImpl(long intervalStartTimestamp, long durationMs, Set<Class> operationTypes)
    {
        this.intervalStartTimestamp = intervalStartTimestamp;
        this.durationMs = durationMs;
        this.operationTypes = operationTypes;
        this.successCount = new HashMap<>();
        this.annotations = new ArrayList<>();
        log.debug(this + " constructed");
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
     * @return 0 if the operation type is unknown.
     *
     * @see SamplingInterval#getSuccessCount(Class)
     */
    @Override
    public long getSuccessCount(Class operationType)
    {
        Long lv = successCount.get(operationType);

        if (lv == null)
        {
            return 0;
        }

        return lv;
    }

    @Override
    public List<String> getAnnotations()
    {
        return annotations;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setSuccessCount(Class operationType, long lv)
    {
        successCount.put(operationType, lv);
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
