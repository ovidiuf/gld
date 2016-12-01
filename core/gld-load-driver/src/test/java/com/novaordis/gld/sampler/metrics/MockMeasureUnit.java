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

public class MockMeasureUnit implements MeasureUnit
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String abbreviation;
    private MetricType metricType;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockMeasureUnit(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }
    public MockMeasureUnit(MetricType metricType, String abbreviation)
    {
        this.abbreviation = abbreviation;
        this.metricType = metricType;
    }

    // MeasureUnit implementation --------------------------------------------------------------------------------------

    @Override
    public String abbreviation()
    {
        return abbreviation;
    }

    @Override
    public MetricType getMetricType()
    {
        return metricType;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
