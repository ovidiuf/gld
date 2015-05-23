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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A standard counter implementation, that measures success count, cumulated time (in nanoseconds) and failures.
 */
public class SamplingIntervalImpl implements SamplingInterval
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SamplingIntervalImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<Class, Long> successCount;
    private List<String> annotations;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SamplingIntervalImpl()
    {
        this.successCount = new HashMap<>();
        this.annotations = new ArrayList<>();
        log.debug(this + " constructed");
    }

    // SamplingInterval implementation ---------------------------------------------------------------------------------

    @Override
    public long getTimestamp()
    {
        return 0;
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
        return "SamplingInterval[]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
