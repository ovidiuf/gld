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

package com.novaordis.gld.sampler;

import com.novaordis.gld.Operation;

import java.util.List;
import java.util.Set;

public interface SamplingInterval
{
    /**
     * @return the timestamp (in milliseconds) of the sampling interval start.
     */
    long getTimestamp();

    /**
     * @return the duration of the sampling interval, in ms
     */
    long getDuration();

    /**
     * The types of the operations sampled in this interval.
     */
    Set<Class<? extends Operation>> getOperationTypes();

    /**
     * @return null if the operation type was not known to the sampler when it generated this sampling interval.
     */
    CounterValues getCounterValues(Class<? extends Operation> operationType);

    /**
     * @return the annotations that were entered at the console during this sampling interval. May return an empty
     * list if no annotations were entered.
     */
    List<String> getAnnotations();

}
