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

import io.novaordis.gld.api.sampler.metrics.MeasureUnit;
import io.novaordis.gld.api.sampler.metrics.Metric;
import io.novaordis.gld.api.sampler.metrics.MetricType;

public class MockMetric implements Metric
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String label;
    private MetricType metricType;
    private MeasureUnit measureUnit;
    private Number value;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMetric(String label, MeasureUnit measureUnit)
    {
        this(label, measureUnit, null);
    }

    public MockMetric(String label, MeasureUnit measureUnit, MetricType metricType)
    {
        this.label = label;

        if (measureUnit != null)
        {
            this.measureUnit = measureUnit;
            MetricType mumt = measureUnit.getMetricType();
            if (mumt != null)
            {
                this.metricType = mumt;
            }
        }

        if (metricType != null)
        {
            if (this.metricType != null && !this.metricType.equals(metricType))
            {
                throw new IllegalArgumentException(
                    "the measure unit " + measureUnit + "'s metric type " + this.metricType +
                        " is not compatible with the metric type given as argument " + metricType);
            }

            this.metricType = metricType;
        }
    }

    // Metric implementation -------------------------------------------------------------------------------------------

    @Override
    public Number getValue()
    {
        return value;
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

    public void setValue(Number value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "" + label + " " + value + " " + measureUnit;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
