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

package io.novaordis.gld.api.sampler.metrics;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * The system load average for the last minute.
 *
 * @see com.sun.management.OperatingSystemMXBean#getSystemCpuLoad
 */
public class SystemCpuLoad implements Metric
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DISPLAY_RANK = 30010;

    // Static ----------------------------------------------------------------------------------------------------------

    private static OperatingSystemMXBean INSTANCE;

    public static OperatingSystemMXBean getJava7OperatingSystemMXBean()
    {
        if (INSTANCE == null)
        {
            INSTANCE = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
        }

        return INSTANCE;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Number value;

    // Constructors ----------------------------------------------------------------------------------------------------

    @SuppressWarnings("UnusedDeclaration")
    public SystemCpuLoad()
    {
        this(getJava7OperatingSystemMXBean().getSystemCpuLoad());
    }

    public SystemCpuLoad(double value)
    {
        this.value = value;
    }

    // Comparable implementation ---------------------------------------------------------------------------------------

    /**
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(Metric o)
    {
        return getDisplayRank() - o.getDisplayRank();
    }

    // Metric implementation ------------------------------------------------------------------------------------------

    @Override
    public Number getValue()
    {
        return value;
    }

    @Override
    public String getLabel()
    {
        return "System CPU Load";
    }

    @Override
    public MetricType getMetricType()
    {
        return null;
    }

    @Override
    public MeasureUnit getMeasureUnit()
    {
        return MeasureUnit.PERCENTAGE;
    }

    @Override
    public int getDisplayRank()
    {
        return DISPLAY_RANK;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        Number n = getValue();
        return n == null ? "null" : n.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
