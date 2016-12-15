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

package io.novaordis.gld.api.statistics;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.sampler.metrics.MeasureUnit;
import io.novaordis.gld.api.sampler.metrics.MetricType;
import io.novaordis.gld.api.sampler.metrics.Metric;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * NOT thread safe - do not maintain shared internal state, or if you do, make sure to synchronize.
 */
public class CSVFormat implements Format
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final java.text.Format TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    // a number of operations per unit of time - don't display the decimal points.
    public static final java.text.Format RATE_FORMAT = new DecimalFormat("#0");

    public static final java.text.Format FLOATING_POINT_FORMAT = new DecimalFormat("#0.0#");

    public static final String TIMESTAMP_HEADER_LABEL = "Time";
    public static final String NOTES_HEADER_LABEL = "Notes";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private MeasureUnit averageOperationDurationTimeUnit;
    private MeasureUnit memoryUnit;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVFormat()
    {
        this.averageOperationDurationTimeUnit = MeasureUnit.MILLISECOND;
        this.memoryUnit = MeasureUnit.MEGABYTE;
    }

    // Format implementation -------------------------------------------------------------------------------------------

    @Override
    public String formatTimestamp(long ms)
    {
        return TIMESTAMP_FORMAT.format(ms);
    }

    @Override
    public String getTimestampLabel()
    {
        return TIMESTAMP_HEADER_LABEL;
    }

    @Override
    public List<Class<? extends Operation>> orderOperationTypes(Set<Class<? extends Operation>> operationTypes)
    {
        List<Class<? extends Operation>> result = new ArrayList<>();
        
        // TODO *extremely* inefficient, I need to find something better

        if (operationTypes.size() == 1)
        {
            //noinspection unchecked
            Class<? extends Operation>[] a = new Class[1];
            operationTypes.toArray(a);
            result.add(a[0]);
            return result;
        }

        if (operationTypes.isEmpty())
        {
            return Collections.emptyList();
        }

        List<String> names = new ArrayList<>();

        for(Class<? extends Operation> ot: operationTypes)
        {
            names.add(ot.getName());
        }
        Collections.sort(names);
        for(String name: names)
        {
            for(Class<? extends Operation> ot: operationTypes)
            {
                if (name.equals(ot.getName()))
                {
                    result.add(ot);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Metric> orderMetrics(Set<Metric> metrics)
    {
        List<Metric> ordered = new ArrayList<>(metrics);
        Collections.sort(ordered);
        return ordered;
    }

    @Override
    public String getSuccessRateHeader(Class<? extends Operation> operationType)
    {
        if (operationType == null)
        {
            throw new IllegalArgumentException("null operation type");
        }

        // TODO parametrize "sec"
        return operationType.getSimpleName() + " Success Rate (ops/sec)";
    }

    @Override
    public String getFailureRateHeader(Class<? extends Operation> operationType)
    {
        if (operationType == null)
        {
            throw new IllegalArgumentException("null operation type");
        }

        // TODO parametrize "sec"
        return operationType.getSimpleName() + " Failure Rate (ops/sec)";
    }

    @Override
    public String getSuccessAverageDurationHeader(Class<? extends Operation> operationType)
    {
        if (operationType == null)
        {
            throw new IllegalArgumentException("null operation type");
        }

        return
            operationType.getSimpleName() + " Average Duration (" +
                averageOperationDurationTimeUnit.abbreviation() + ")";
    }

    /**
     * @see Format#formatRate(double, MeasureUnit)
     */
    @Override
    public String formatRate(double rate, MeasureUnit measureUnitForUnitTheRateIsCalculatedAgainst)
    {
        return RATE_FORMAT.format(rate);
    }

    @Override
    public String formatAverageDuration(double averageDuration, MeasureUnit timeUnit)
    {
        MeasureUnit defaultTimeUnit = getAverageOperationDurationTimeUnit();
        double ad = averageDuration / Statistics.multiplicationFactor(timeUnit, defaultTimeUnit);
        return FLOATING_POINT_FORMAT.format(ad);
    }

    @Override
    public String getMetricHeader(Metric m)
    {
        String s = m.getLabel();
        MeasureUnit mu = m.getMeasureUnit();
        MetricType mt = m.getMetricType();

        if (MetricType.MEMORY.equals(mt))
        {
            // use format's memory measure unit
            mu = getMemoryUnit();
        }

        String a = mu == null ? null : mu.abbreviation();
        s = s + (a == null ? "" : " (" + a + ")");

        // enclose in quotas if it contains commas
        if (s.indexOf(',') != -1)
        {
            s = '"' + s + '"';
        }

        return s;
    }

    @Override
    public String formatMetric(Metric m)
    {
        double outputValue;

        if (MetricType.MEMORY.equals(m.getMetricType()))
        {
            // we convert memory metrics in the format's default
            outputValue = Statistics.convert(m.getValue().doubleValue(), m.getMeasureUnit(), getMemoryUnit());
        }
        else
        {
            outputValue = m.getValue().doubleValue();
        }

        return FLOATING_POINT_FORMAT.format(outputValue);
    }

    @Override
    public String getNotesHeader()
    {
        return NOTES_HEADER_LABEL;
    }

    @Override
    public String formatNotes(List<String> notes)
    {
        String s = "";

        boolean containsComma = false;

        if (notes != null)
        {
            for (Iterator<String> i = notes.iterator(); i.hasNext(); )
            {
                s += i.next();

                containsComma = containsComma || s.indexOf(',') != -1;

                if (i.hasNext())
                {
                    s += "; ";
                }
            }
        }

        if (containsComma)
        {
            s = '"' + s + '"';
        }

        return s;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setAverageOperationDurationTimeUnit(MeasureUnit averageOperationDurationTimeUnit)
    {
        this.averageOperationDurationTimeUnit = averageOperationDurationTimeUnit;
    }

    public MeasureUnit getAverageOperationDurationTimeUnit()
    {
        return averageOperationDurationTimeUnit;
    }

    public void setMemoryUnit(MeasureUnit memoryUnit)
    {
        this.memoryUnit = memoryUnit;
    }

    public MeasureUnit getMemoryUnit()
    {
        return memoryUnit;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
