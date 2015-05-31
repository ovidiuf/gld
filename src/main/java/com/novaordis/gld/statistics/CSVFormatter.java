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

import com.novaordis.gld.Operation;
import com.novaordis.gld.sampler.CounterValues;
import com.novaordis.gld.sampler.SamplingConsumer;
import com.novaordis.gld.sampler.SamplingInterval;
import com.novaordis.gld.sampler.metrics.MeasureUnit;
import com.novaordis.gld.sampler.metrics.Metric;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static com.novaordis.gld.statistics.Statistics.calculateAverageDuration;
import static com.novaordis.gld.statistics.Statistics.calculateRate;

public class CSVFormatter implements SamplingConsumer
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CSVFormatter.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void writeHeaders(SamplingInterval si, Format format, PrintWriter pw)
    {
        pw.println(toLine(si, format, true));
    }

    public static void writeLine(PrintWriter pw, SamplingInterval si, Format format)
    {
        pw.println(toLine(si, format, false));
        pw.flush();
    }

    /**
     * This method can be used to:
     *
     * 1) generate headers (set 'headers' on true)
     * 2) generate a data CSV line - with or without comments.
     */
    public static String toLine(SamplingInterval si, Format csvFormat, boolean headers)
    {
        String s = "";

        if (headers)
        {
            s += csvFormat.getTimestampLabel();
        }
        else
        {
            s += csvFormat.formatTimestamp(si.getStartMs());
        }

        s += ", ";

        List<Class<? extends Operation>> orderedOperationTypes = csvFormat.orderOperationTypes(si.getOperationTypes());

        for(Class<? extends Operation> ot: orderedOperationTypes)
        {
            if (headers)
            {
                s += csvFormat.getSuccessRateHeader(ot) + ", ";
                s += csvFormat.getSuccessAverageDurationHeader(ot) + ", ";
                s += csvFormat.getFailureRateHeader(ot) + ", ";
            }
            else
            {
                CounterValues v = si.getCounterValues(ot);
                long intervalDurationNano = v.getIntervalNano();

                if (intervalDurationNano != 1000L * 1000000L)
                {
                    log.warn("interval duration different from standard 1 sec, currently " + intervalDurationNano + " ns");
                }

                long sc = v.getSuccessCount();
                long scdNano = v.getSuccessCumulatedDurationNano();
                double sr = calculateRate(sc, intervalDurationNano, MeasureUnit.NANOSECOND, MeasureUnit.SECOND);
                double adMs = calculateAverageDuration(sc, scdNano, MeasureUnit.NANOSECOND, MeasureUnit.MILLISECOND);

                long fc = v.getFailureCount();
                double fr = calculateRate(fc, intervalDurationNano, MeasureUnit.NANOSECOND, MeasureUnit.SECOND);

                s += csvFormat.formatRate(sr, MeasureUnit.SECOND) + ", ";
                s += csvFormat.formatAverageDuration(adMs, MeasureUnit.MILLISECOND) + ", ";
                s += csvFormat.formatRate(fr, MeasureUnit.SECOND) + ", ";
            }
        }

        Set<Metric> metrics = si.getMetrics();
        List<Metric> orderedMetrics = csvFormat.orderMetrics(metrics);

        for(Metric m: orderedMetrics)
        {
            s += headers ? csvFormat.getMetricHeader(m) : csvFormat.formatMetric(m);
            s+= ", ";
        }

        if (headers)
        {
            s += csvFormat.getNotesHeader();
        }
        else
        {
            List<String> notes = si.getAnnotations();
            s += csvFormat.formatNotes(notes);
        }


        // TODO

        //s+= ", ";

        //
        // failure counters
        //



        return s;
    }


    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean writeHeaders;

    // we don't want it to be buffered, we want to go to disk as soon as possible when a line is ready, we won't
    // block anything because the work is done on a dedicated thread
    private PrintWriter pw;

    private CSVFormat csvFormat;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVFormatter(PrintWriter pw)
    {
        this.pw = pw;
        this.writeHeaders = true;
        this.csvFormat = new CSVFormat();
    }

    // SamplingConsumer implementation ---------------------------------------------------------------------------------

    @Override
    public void consume(SamplingInterval... samplingIntervals)
    {
        if (writeHeaders)
        {
            writeHeaders = false;
            writeHeaders(samplingIntervals[0], csvFormat, pw);
        }

        for(SamplingInterval si: samplingIntervals)
        {
            writeLine(pw, si, csvFormat);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
