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
    /**
     * The duration of the interval (in nanoseconds) the underlying counter values have been collected within.
     */
    long getIntervalNano();

    /**
     * @return the number of successful operations accumulated within the current interval, whose length can be
     * obtained with getIntervalNano().
     *
     * @see CounterValues#getIntervalNano();
     */
    long getSuccessCount();

    /**
     * @return the cumulated duration (in nanoseconds) for all successful operations within the current interval, whose
     * length can be obtained with getIntervalNano().
     *
     * @see CounterValues#getIntervalNano();
     */
    long getSuccessCumulatedDurationNano();

    /**
     * The failure types seen during the current interval.
     *
     * The failure type set is cumulative: once a failure has been reported for a specific operation, that failure
     * type will be always present in subsequent counters generated for that operation, even if the associated counters
     * will be zero if that failure does not show up again. TODO: we may not want that - consider refactoring and
     * letting the upper layer worry about failure management.
     *
     * @return may be empty, never null.
     */
    Set<Class<? extends Throwable>> getFailureTypes();

    /**
     * @return the number of failed operations accumulated within the current interval, whose length can be
     * obtained with getIntervalNano(). The counter does not differentiate on failure type, it includes all failures.
     *
     * @see CounterValues#getIntervalNano();
     */
    long getFailureCount();

    /**
     * @param failureType the class implementing the failure. It must be the exact exception class, superclasses are
     *                    ignored.
     *
     * @return the failure count for a specific failure type, as encountered during the current interval, whose length
     * can be obtained with getIntervalNano(). Return 0L if there's no such failure type.
     *
     * @see CounterValues#getIntervalNano();
     */
    long getFailureCount(Class<? extends Throwable> failureType);

    /**
     * @return the cumulated duration (in nanoseconds) for all failed operations within the current interval, whose
     * length can be obtained with getIntervalNano().
     *
     * @see CounterValues#getIntervalNano();
     */
    long getFailureCumulatedDurationNano();

    /**
     * @param failureType the class implementing the failure. It must be the exact exception class, superclasses are
     *                    ignored.
     *
     * @return the cumulated time (in nanoseconds) for a specific failure type within the current interval, whose
     * length can be obtained with getIntervalNano().
     *
     * @see CounterValues#getIntervalNano();
     */
    long getFailureCumulatedDurationNano(Class<? extends Throwable> failureType);

}
