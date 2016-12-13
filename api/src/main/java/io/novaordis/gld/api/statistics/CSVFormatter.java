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
import io.novaordis.gld.api.sampler.CounterValues;
import io.novaordis.gld.api.sampler.SamplingConsumer;
import io.novaordis.gld.api.sampler.SamplingInterval;
import io.novaordis.gld.api.sampler.metrics.MeasureUnit;
import io.novaordis.gld.api.sampler.metrics.Metric;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

public class CSVFormatter implements SamplingConsumer {


    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CSVFormatter.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void writeHeaders(SamplingInterval si, Format format, Writer w) throws IOException  {
        String headers = toLine(si, format, true);
        w.write(headers + "\n");
    }

    public static void writeLine(SamplingInterval si, Format format, Writer w) throws IOException {
        String line = toLine(si, format, false);
        w.write(line + "\n");
        w.flush();
    }

    /**
     * This method can be used to:
     *
     * 1) generate headers (set 'headers' on true)
     * 2) generate a data CSV line - with or without comments.
     */
    public static String toLine(SamplingInterval si, Format csvFormat, boolean headers) {

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

                long sc = v.getSuccessCount();
                long scdNano = v.getSuccessCumulatedDurationNano();
                double sr = Statistics.calculateRate(sc, si.getDurationMs(), MeasureUnit.MILLISECOND, MeasureUnit.SECOND);
                double adMs = Statistics.calculateAverageDuration(sc, scdNano, MeasureUnit.NANOSECOND, MeasureUnit.MILLISECOND);

                long fc = v.getFailureCount();
                double fr = Statistics.calculateRate(fc, si.getDurationMs(), MeasureUnit.MILLISECOND, MeasureUnit.SECOND);

                s += csvFormat.formatRate(sr, MeasureUnit.SECOND) + ", ";
                s += csvFormat.formatAverageDuration(adMs, MeasureUnit.MILLISECOND) + ", ";
                s += csvFormat.formatRate(fr, MeasureUnit.SECOND) + ", ";
            }
        }

        Set<Metric> metrics = si.getMetrics();
        if (metrics != null)
        {
            List<Metric> orderedMetrics = csvFormat.orderMetrics(metrics);
            for (Metric m : orderedMetrics)
            {
                s += headers ? csvFormat.getMetricHeader(m) : csvFormat.formatMetric(m);
                s += ", ";
            }
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
    private Writer w;

    private CSVFormat csvFormat;

    // Constructors ----------------------------------------------------------------------------------------------------

    public CSVFormatter(Writer w)
    {
        this.w = w;
        this.writeHeaders = true;
        this.csvFormat = new CSVFormat();
    }

    // SamplingConsumer implementation ---------------------------------------------------------------------------------

    /**
     * @see SamplingConsumer#consume(SamplingInterval...)
     */
    @Override
    public void consume(SamplingInterval... samplingIntervals) throws Exception
    {
        if (writeHeaders)
        {
            writeHeaders = false;
            writeHeaders(samplingIntervals[0], csvFormat, w);
        }

        for(SamplingInterval si: samplingIntervals)
        {
            writeLine(si, csvFormat, w);
        }
    }

    @Override
    public void stop()
    {
        // close the underlying Writer
        if (w != null)
        {
            try
            {
                w.close();
            }
            catch(IOException e)
            {
                log.warn(this + " failed to close the underlying writer", e);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
