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

/**
 * Use MeasureUnit.NONE for metrics that are non-dimensional (like system load average, for example).
 */
public interface MeasureUnit
{
    public static final MeasureUnit NONE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return null; }

        @Override
        public MetricType getMetricType()
        {
            return null;
        }

        @Override
        public String toString() { return "NONE"; }
    };

    public static final MeasureUnit PERCENTAGE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "%"; }

        @Override
        public MetricType getMetricType()
        {
            return null;
        }

        @Override
        public String toString() { return "%"; }
    };

    public static final MeasureUnit BYTE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "b"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.MEMORY;
        }

        @Override
        public String toString() { return "BYTE"; }
    };

    public static final MeasureUnit KILOBYTE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "KB"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.MEMORY;
        }

        @Override
        public String toString() { return "KILOBYTE"; }
    };

    public static final MeasureUnit MEGABYTE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "MB"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.MEMORY;
        }

        @Override
        public String toString() { return "MEGABYTE"; }
    };

    public static final MeasureUnit GIGABYTE = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "GB"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.MEMORY;
        }

        @Override
        public String toString() { return "GIGABYTE"; }
    };

    public static final MeasureUnit NANOSECOND = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "ns"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.TIME;
        }

        @Override
        public String toString() { return "NANOSECOND"; }
    };

    public static final MeasureUnit MILLISECOND = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "ms"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.TIME;
        }

        @Override
        public String toString() { return "MILLISECOND"; }
    };

    public static final MeasureUnit SECOND = new MeasureUnit()
    {
        @Override
        public String abbreviation() { return "s"; }

        @Override
        public MetricType getMetricType()
        {
            return MetricType.TIME;
        }

        @Override
        public String toString() { return "SECOND"; }
    };

    /**
     * @return abbreviation of the measure unit: "%" for PERCENTAGE, "ns" for NANOSECOND, "ms" for MILLISECOND, etc.
     * Return null if the metric does not have an abbreviation - such as non-dimensional values.
     */
    String abbreviation();

    MetricType getMetricType();

}
