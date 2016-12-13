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
import io.novaordis.gld.api.sampler.metrics.Metric;

import java.util.List;
import java.util.Set;

public interface Format {

    String formatTimestamp(long ms);

    String getTimestampLabel();

    /**
     * Orders the operation types for display.
     */
    List<Class<? extends Operation>> orderOperationTypes(Set<Class<? extends Operation>> operationTypes);

    /**
     * Orders the metrics for display
     */
    List<Metric> orderMetrics(Set<Metric> metrics);

    String getSuccessRateHeader(Class<? extends Operation> operationType);
    String getFailureRateHeader(Class<? extends Operation> operationType);

    String getSuccessAverageDurationHeader(Class<? extends Operation> operationType);

    /**
     * Displays operation rates per time unit. Even if rate is float, the display will always show integral values.
     */
    String formatRate(double rate, MeasureUnit measureUnitForOneUnitTheRateIsCalculatedAgainst);
    String formatAverageDuration(double averageDuration, MeasureUnit timeUnit);

    String getMetricHeader(Metric m);
    String formatMetric(Metric m);

    String getNotesHeader();
    String formatNotes(List<String> notes);
}
