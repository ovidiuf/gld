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

/**
 * A metric (usually system-wide) such as load average, cpu load, various memory values to be read and included with
 * each sample.
 *
 * The comparable interface is used to sort a set of metrics for display.
 */
public interface Metric extends Comparable<Metric>
{
    /**
     * @return the value of the metric at the time the method was invoked. Could be an Integer, Double, etc, depending
     * on the type of the metric.
     *
     */
    Number getValue();

    /**
     * @return a human readable label that can show up in a table column or a graph.
     */
    String getLabel();

    /**
     * May be null.
     */
    MetricType getMetricType();

    /**
     * The measure unit of the values returned by getValue(). If the metric does not have a measure unit, this
     * method will return null.
     */
    MeasureUnit getMeasureUnit();

    int getDisplayRank();
}
