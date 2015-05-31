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

package com.novaordis.gld.sampler.metrics;

public class MockMetric implements Metric
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String label;
    private MetricType metricType;
    private MeasureUnit measureUnit;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMetric(String label, MeasureUnit measureUnit)
    {
        this(label, measureUnit, null);
    }

    public MockMetric(String label, MeasureUnit measureUnit, MetricType metricType)
    {
        this.label = label;
        this.metricType = metricType;
        this.measureUnit = measureUnit;
    }

    // Metric implementation -------------------------------------------------------------------------------------------

    @Override
    public Number getValue()
    {
        throw new RuntimeException("getValue() NOT YET IMPLEMENTED");
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public MetricType getMetricType()
    {
        return metricType;
    }

    @Override
    public MeasureUnit getMeasureUnit()
    {
        return measureUnit;
    }

    @Override
    public int getDisplayRank()
    {
        throw new RuntimeException("getDisplayRank() NOT YET IMPLEMENTED");
    }

    @Override
    public int compareTo(Metric o)
    {
        throw new RuntimeException("compareTo() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
