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

package com.novaordis.gld.statistics;

import com.novaordis.gld.sampler.metrics.MeasureUnit;
import com.novaordis.gld.sampler.metrics.MetricType;

public class Statistics
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * @param count the number of events per interval.
     * @param intervalDuration the length of the interval the events ware counted for.
     * @param intervalMeasureUnit the time unit of the length of the interval.
     * @param target the time unit for the target interval we calculate the rate for, always one unit (example per
     *               second, per millisecond, etc).
     *
     * @return the calculated rate for one target unit
     */
    public static double calculateRate(
        long count, long intervalDuration, MeasureUnit intervalMeasureUnit, MeasureUnit target)
    {
        return (double)multiplicationFactor(intervalMeasureUnit, target) / intervalDuration * count;
    }

    /**
     * @param count the number of events.
     * @param cumulatedDuration the events cumulated duration, expressed in the 'cumulatedDurationUnit'
     * @param cumulatedDurationUnit the time unit for the cumulated duration.
     * @param targetUnit the target unit we will express calculated average duration in.
     * @return the average duration of the events (cumulatedDuration/count) expressed in target unit.
     */
    public static double calculateAverageDuration(
        long count, long cumulatedDuration, MeasureUnit cumulatedDurationUnit, MeasureUnit targetUnit)
    {
        return (double)cumulatedDuration / count / multiplicationFactor(cumulatedDurationUnit, targetUnit);
    }

    /**
     * How many times a source unit should be multiplied to obtain a destination unit.
     */
    public static long multiplicationFactor(MeasureUnit source, MeasureUnit destination)
    {
        if (source.equals(destination))
        {
            return 1L;
        }

        if (MeasureUnit.NANOSECOND.equals(source))
        {
            if (MeasureUnit.MILLISECOND.equals(destination))
            {
                return 1000000L;
            }
            else if (MeasureUnit.SECOND.equals(destination))
            {
                return 1000000000L;
            }
        }

        MetricType smt = source.getMetricType();
        MetricType dmt = destination.getMetricType();

        if (smt == null || dmt == null ||! smt.equals(dmt))
        {
            throw new IllegalArgumentException(source + " and " + destination + " are incompatible");
        }

        throw new RuntimeException("don't know the multiplication factor from source " + source + " to destination " + destination);
    }


    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private Statistics()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
