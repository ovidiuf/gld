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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.sampler.metrics.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static utilities related to sampling intervals.
 */
public class SamplingIntervalUtil {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(SamplingIntervalUtil.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Distribute the values from the given SamplingInterval to 'extraSamples' more successive same-length intervals
     * (so the total number of returned intervals will be extraSamples + 1). It is possible - and quite probable, if the
     * sampling run periodicity is well chosen relative to the sampling interval - that extraSamples is 0.
     *
     * If n is 0, it simply returns the instance it was passed.
     *
     * TODO: too complex, needs refactoring
     * TODO: the extrapolation algorithm can be improved by making the distribution more precise as we scan the
     *       interval list. Now we distribute evenly across all intervals and rounding errors add up to back-load the
     *       last interval.
     *
     */
    public static SamplingInterval[] extrapolate(SamplingInterval recorded, int extraSamples) {


        if (recorded == null) {
            throw new IllegalArgumentException("null sampling interval");
        }

        if (extraSamples == 0) {
            return new SamplingInterval[] { recorded };
        }

        // total number of samples
        int n = extraSamples + 1;

        SamplingIntervalImpl[] result = new SamplingIntervalImpl[n];
        long duration = recorded.getDurationMs();
        long start = recorded.getStartMs();
        boolean annotationsProcessed = false;

        // metrics should propagate the same values
        Set<Metric> metrics = recorded.getMetrics();

        for(Class<? extends Operation> ot : recorded.getOperationTypes()) {

            CounterValues valuesToBeDistributed = recorded.getCounterValues(ot);

            long successCount = 0L;
            long successCumulatedDuration = 0L;

            Set<Class<? extends Throwable>> failureTypes = valuesToBeDistributed.getFailureTypes();
            Map<Class<? extends Throwable>, Long> failureCount = zeroInitializedFailureCounterMap(failureTypes);
            Map<Class<? extends Throwable>, Long> failureCumulatedDuration = zeroInitializedFailureCounterMap(failureTypes);

            for(int i = 0; i < n; i ++) {

                SamplingIntervalImpl si = result[i];

                if (si == null) {

                    si = new SamplingIntervalImpl(start, duration, recorded.getOperationTypes());
                    si.setMetrics(new HashSet<>(metrics)); // make a copy of the map
                    result[i] = si;
                    start += duration;
                }

                if ((i == 0) && !annotationsProcessed) {

                    annotationsProcessed = true;

                    // place all annotations in the first sampling interval
                    for(String a: recorded.getAnnotations()) {
                        si.addAnnotation(a);
                    }
                }

                long sc;
                long scd = 0L;
                Map<Class<? extends Throwable>, Long> fc = zeroInitializedFailureCounterMap(failureTypes);
                Map<Class<? extends Throwable>, Long> fcd = zeroInitializedFailureCounterMap(failureTypes);

                if (i != n - 1) {

                    sc = valuesToBeDistributed.getSuccessCount() / n;
                    successCount += sc;

                    if (sc != 0) {

                        scd = valuesToBeDistributed.getSuccessCumulatedDurationNano() / n;
                        successCumulatedDuration += scd;
                    }

                    for(Class<? extends Throwable> ft: failureTypes) {

                        fc.put(ft, valuesToBeDistributed.getFailureCount(ft) / n);
                        failureCount.put(ft, failureCount.get(ft) + fc.get(ft));

                        if (fc.get(ft) != 0L) {

                            fcd.put(ft, valuesToBeDistributed.getFailureCumulatedDurationNano(ft) / n);
                            failureCumulatedDuration.put(ft, failureCumulatedDuration.get(ft) + fcd.get(ft));
                        }
                    }
                }
                else {

                    // last sampling interval

                    sc = valuesToBeDistributed.getSuccessCount() - successCount;
                    scd = valuesToBeDistributed.getSuccessCumulatedDurationNano() - successCumulatedDuration;

                    for(Class<? extends Throwable> ft: failureTypes) {
                        fc.put(ft, valuesToBeDistributed.getFailureCount(ft) - failureCount.get(ft));
                        fcd.put(ft, valuesToBeDistributed.getFailureCumulatedDurationNano(ft) - failureCumulatedDuration.get(ft));
                    }
                }

                Map<Class<? extends Throwable>, ImmutableFailureCounter> failures = new HashMap<>();

                for(Class<? extends Throwable> ft: failureTypes) {

                    ImmutableFailureCounter ifc = new ImmutableFailureCounter(fc.get(ft), fcd.get(ft));
                    failures.put(ft, ifc);
                }

                CounterValuesImpl cv = new CounterValuesImpl(sc, scd, failures);
                si.setCounterValues(ot, cv);
            }
        }

        return result;
    }

    public static Set<Metric> snapshotMetrics(Set<Class<? extends Metric>> metricTypes) {

        Set<Metric> result = new HashSet<>();

        for(Class<? extends Metric> mt: metricTypes) {

            try {
                Metric m = mt.newInstance();
                result.add(m);
            }
            catch(Exception e) {
                log.warn("could not create Metric instance from " + mt, e);
            }
        }

        return result;

    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private SamplingIntervalUtil() {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private static Map<Class<? extends Throwable>, Long> zeroInitializedFailureCounterMap(
        Set<Class<? extends Throwable>> failureTypes ) {

        Map<Class<? extends Throwable>, Long> result = new HashMap<>();

        for(Class<? extends Throwable> ft: failureTypes) {
            result.put(ft, 0L);
        }

        return result;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
