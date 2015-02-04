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

package com.novaordis.cld;

import com.novaordis.ac.Collector;
import com.novaordis.cld.external.SystemStatistics;
import com.novaordis.cld.operations.Read;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class CollectorBasedStatistics implements Statistics
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final Format TIMESTAMP_FORMAT_MS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    public static final Format TIMESTAMP_FORMAT_SEC = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static final Format DURATION_MS_FORMAT = new DecimalFormat("#.0");
    public static final Format LOAD_FORMAT = new DecimalFormat("#.00");
    public static final Format MEMORY_MB_FORMAT = new DecimalFormat("#.00");

    public static final Format PERCENTAGE = new DecimalFormat("00.00%");

    public static final long NANOS_IN_MILLS = 1000L * 1000L;

    public static final long DEFAULT_SAMPLING_INTERVAL_MS = 1000L; // 1 second

    public static final int BYTES_IN_MB = 1024 * 1024;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean firstSample;

    private Collector csvStatsCollector;

    private long samplingIntervalMs;
    private volatile boolean done;
    private long operationsLeft;

    private long validReadsCountInSample;
    private long readHitsInSample;
    private long validWritesCountInSample;

    /**
     * An array with failure counters, indexed by failure type.
     * @see RedisFailure
     */
    private long[] failureCounters;

    private long cumulatedValidReadsTimeInSampleNano;
    private long cumulatedValidWritesTimeInSampleNano;

    private long samplingIntervalStartMs;

    private long totalValidReads;
    private long totalValidWrites;

    private long totalValidReadsTimeNano;
    private long totalValidWritesTimeNano;

    private long startTimestamp;
    private long endTimestamp;

    private long totalFailures;

    /**
     * An array with failure counters, indexed by failure type.
     * @see RedisFailure
     */
    private long[] totalFailureCounters;

    private SystemStatistics systemStats;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Uses the default sampling interval.
     */
    public CollectorBasedStatistics(Collector collector)
    {
        this(collector, DEFAULT_SAMPLING_INTERVAL_MS);
    }

    public CollectorBasedStatistics(Collector collector, long samplingIntervalMs)
    {
        this.firstSample = true;
        this.done = false;
        this.operationsLeft = Long.MAX_VALUE;
        this.csvStatsCollector = collector;
        this.samplingIntervalMs = samplingIntervalMs;

        this.samplingIntervalStartMs = -1L;

        this.validReadsCountInSample = 0;
        this.readHitsInSample = 0;
        this.validWritesCountInSample = 0;

        this.failureCounters = new long[RedisFailure.FAILURE_TYPES_COUNT];

        this.cumulatedValidReadsTimeInSampleNano = 0L;
        this.cumulatedValidWritesTimeInSampleNano = 0L;

        this.totalValidReads = 0L;
        this.totalValidWrites = 0L;

        this.totalValidReadsTimeNano = 0L;
        this.totalValidWritesTimeNano = 0L;

        this.startTimestamp = 0L;
        this.endTimestamp = 0L;

        this.totalFailures = 0L;
        this.totalFailureCounters = new long[RedisFailure.FAILURE_TYPES_COUNT];

        this.systemStats = new SystemStatistics();
    }

    // Statistics implementation ---------------------------------------------------------------------------------------

    @Override
    public boolean areWeDone()
    {
        return done;
    }

    /**
     * @see Statistics#record(long, long, long, Operation, Throwable)
     */
    @Override
    public void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable t)
    {
        synchronized (this)
        {
            if (firstSample)
            {
                firstSample = false;
                startTimestamp = t0Ms;
                csvStatsCollector.handOver(SamplingInterval.getCsvHeaders());
            }

            operationsLeft--;
            endTimestamp = t0Ms;

            if (operationsLeft == 0L)
            {
                this.done = true;
            }

            if (samplingIntervalStartMs < 0L)
            {
                samplingIntervalStartMs = t0Ms;
            }

            boolean read = op instanceof Read;

            // records that fall right on the edge are accounted for <b>this</b> sampling interval
            if (t0Ms - samplingIntervalStartMs > samplingIntervalMs)
            {
                // compute statistics for the sampling interval(s) that just have finished and start another sampling
                // interval. Also read relevant metrics for the current sampling interval. As much as I wanted to
                // do this on the collector's tread to impact the behavior we're measuring as little as possible,
                // it's not a good idea because the processing may be delayed and the correlation is lost.

                double systemLoadAverage = systemStats.getSystemLoadAverage();
                double systemCpuLoad = systemStats.getSystemCpuLoad();
                double processCpuLoad = systemStats.getProcessCpuLoad();
                long usedHeap = systemStats.getHeapUsed();
                long committedHeap = systemStats.getHeapCommitted();

                SamplingInterval si = new SamplingInterval(
                    samplingIntervalStartMs,
                    validReadsCountInSample,
                    readHitsInSample,
                    validWritesCountInSample,
                    cumulatedValidReadsTimeInSampleNano,
                    cumulatedValidWritesTimeInSampleNano,
                    failureCounters,
                    systemLoadAverage,
                    systemCpuLoad,
                    processCpuLoad,
                    -1.0,
                    usedHeap,
                    committedHeap);

                csvStatsCollector.handOver(si);

                // how many extra sampling intervals we skipped - generate empty sampling intervals
                int skipped = (int)((t0Ms - samplingIntervalStartMs) / samplingIntervalMs) - 1;

                // enter empty samples and we interpolate the system metrics
                for(int i = 1; i <= skipped; i ++)
                {
                    si = new SamplingInterval(samplingIntervalStartMs + i * samplingIntervalMs,
                        0, 0, 0, 0, 0, RedisFailure.EMPTY_PRIMITIVE_COUNTERS, systemLoadAverage,
                        systemCpuLoad, processCpuLoad, -1.0, usedHeap, committedHeap);
                    csvStatsCollector.handOver(si);
                }

                // update global stats
                totalValidReads += validReadsCountInSample;
                totalValidWrites += validWritesCountInSample;
                totalValidReadsTimeNano += cumulatedValidReadsTimeInSampleNano;
                totalValidWritesTimeNano += cumulatedValidWritesTimeInSampleNano;

                // prepare the next sampling interval

                samplingIntervalStartMs += (skipped + 1) * samplingIntervalMs;
                cumulatedValidReadsTimeInSampleNano = 0L;
                cumulatedValidWritesTimeInSampleNano = 0L;
                validReadsCountInSample = 0;
                readHitsInSample = 0;
                validWritesCountInSample = 0;
                Arrays.fill(failureCounters, 0L);
            }

            // contribute to the current sampling interval

            if (t == null)
            {
                if (read)
                {
                    validReadsCountInSample++;
                    if (((Read)op).getValue() != null)
                    {
                        readHitsInSample ++;
                    }
                    cumulatedValidReadsTimeInSampleNano += (t1Nano - t0Nano);
                }
                else
                {
                    validWritesCountInSample++;
                    cumulatedValidWritesTimeInSampleNano += (t1Nano - t0Nano);
                }
            }
            else
            {
                int index = RedisFailure.toFailureIndex(t);
                failureCounters[index] ++;
                totalFailureCounters[index] ++;
                totalFailures++;

                csvStatsCollector.handOver(t);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * We're using the Statistics instance to enforce max operations for efficiency reasons: we seek to minimize the
     * number of places where we synchronize. -1 means unlimited.
     */
    public void setMaxOperations(long mo)
    {
        this.operationsLeft = mo;
    }

    public String aggregatesToString()
    {
        long runDurationS = (endTimestamp - startTimestamp) / 1000L;

        double averageReadDurationMs =  ((double)totalValidReadsTimeNano) / totalValidReads / NANOS_IN_MILLS;
        double averageWriteDurationMs =  ((double)totalValidWritesTimeNano) / totalValidWrites / NANOS_IN_MILLS;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));

        pw.println();
        pw.println("                      run started: " + TIMESTAMP_FORMAT_SEC.format(startTimestamp));
        pw.println("                        run ended: " + TIMESTAMP_FORMAT_SEC.format(endTimestamp));
        pw.println("               run duration (sec): " + runDurationS);
        pw.println();
        pw.println("           total valid operations: " + (totalValidReads + totalValidWrites));
        pw.println("                      total reads: " + totalValidReads);
        pw.println("                     total writes: " + totalValidWrites);
        pw.println();
        pw.println("             throughput (ops/sec): " + (runDurationS == 0 ? "N/A" : (int)((totalValidReads + totalValidWrites) / runDurationS)));
        pw.println("                        reads/sec: " + (runDurationS == 0 ? "N/A" : (int)(totalValidReads / runDurationS)));
        pw.println("                       writes/sec: " + (runDurationS == 0 ? "N/A" : (int)(totalValidWrites / runDurationS)));
        pw.println();
        pw.println("       average read duration (ms): " + (totalValidReads == 0 ? "N/A" : DURATION_MS_FORMAT.format(averageReadDurationMs)));
        pw.println("      average write duration (ms): " + (totalValidWrites == 0 ? "N/A" : DURATION_MS_FORMAT.format(averageWriteDurationMs)));
        pw.println();
        pw.println("                         failures: " + totalFailures);
        pw.println("               failure percentage: " + PERCENTAGE.format((double)totalFailures / (totalFailures + totalValidReads + totalValidWrites)));

        long c = 0;
        int maxNameLength = 0;

        for(int i = 0; i < totalFailureCounters.length; i ++)
        {
            if (totalFailureCounters[i] != 0)
            {
                c += totalFailureCounters[i];
                String s = RedisFailure.toFailure(i);
                if (s.length() > maxNameLength)
                {
                    maxNameLength = s.length();
                }
            }
        }

        if (totalFailures != c)
        {
            System.err.println("[warning]: total failures (" + totalFailures +
                ") different from the sum of the failure breakdown counters (" + c + ")");
        }

        if (totalFailures > 0)
        {
            pw.println("");
            pw.println("Failure statistics:");
            pw.println("");

            for(int i = 0; i < totalFailureCounters.length; i ++)
            {
                if (totalFailureCounters[i] != 0)
                {
                    String format = "%" + (maxNameLength + 1) + "s: %d\n";
                    pw.printf(format, RedisFailure.toFailure(i), totalFailureCounters[i]);
                }
            }
        }

        pw.flush();

        return baos.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    long[] getTotalFailureCounters()
    {
        return totalFailureCounters;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
