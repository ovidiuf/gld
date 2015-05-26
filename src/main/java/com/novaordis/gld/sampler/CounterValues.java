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

import java.util.Set;

/**
 * Instances are NOT supposed to be accessed concurrently from multiple threads and must not be thread safe, for
 * performance reasons. The Counter implementations provide protection for those situations.
 */
public interface CounterValues
{
    long getSuccessCount();

    /**
     * @return the cumulated duration (in nanoseconds) for all successful operations.
     */
    long getSuccessCumulatedDuration();

    /**
     * The failure type set is cumulative: once a failure has been reported for a specific operation, that failure
     * type will be always present in subsequent counters generated for that operation, even if the associated counters
     * will be zero if that failure does not show up again.
     *
     * @return may be empty, never null.
     */
    Set<Class<? extends Throwable>> getFailureTypes();

    /**
     * @return the total failure count. Does not differentiates on type.
     */
    long getFailureCount();

    /**
     * @param failureType the class implementing the failure. It must be the exact exception class, superclasses are
     *                    ignored.
     *
     * @return the failure count for a specific failure type. Return 0L if there's no such failure type.
     */
    long getFailureCount(Class<? extends Throwable> failureType);

    /**
     * @return the cumulated time (in nanoseconds) for all failed operations.
     */
    long getFailureCumulatedDurationNano();

    /**
     * @param failureType the class implementing the failure. It must be the exact exception class, superclasses are
     *                    ignored.
     *
     * @return the cumulated time (in nanoseconds) for a specific failure type.
     */
    long getFailureCumulatedDurationNano(Class<? extends Throwable> failureType);



}
