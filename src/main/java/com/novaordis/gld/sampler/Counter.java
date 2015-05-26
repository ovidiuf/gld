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

public interface Counter
{
    /**
     * Update the corresponding counter The method is supposed to be accessed concurrently in a highly contended
     * environment.
     *
     * Implementations may choose implementations based on compare-and-swap (CAS) algorithms.
     *
     * @see NonBlockingCounter
     *
     * @param t0Ms - the time (in milliseconds) when the operation that is being recorded started.
     * @param t0Nano - the time (in nanoseconds) when the operation that is being recorded started. Logically, it
     *        should be the same as t0Ms, but Java documentation advises against using nano-second precision time
     *        to get absolute time information, so we are only using it to calculate delta in conjunction with 't1Nano'.
     * @param t1Nano - the time (in nanoseconds) when the operation that is being recorded ended. Java documentation
     *        advises against using nano-second precision time to get absolute time information, so we are only using
     *        this value to calculate delta in conjunction with 't0Nano'.
     * @param t - optionally a Throwable associated with the operation. Actually we only expect one or none exception
     *          instances to be passed, the rest will be ignored.
     *
     * @throws java.lang.IllegalArgumentException if t1Nano precedes t0Nano.
     * @throws java.lang.IllegalArgumentException if more than one throwable is passed as argument.
     *
     * @see Sampler#record(long, long, long, com.novaordis.gld.Operation, Throwable...)
     */
    void update(long t0Ms, long t0Nano, long t1Nano, Throwable... t);

    Class<? extends Operation> getOperationType();

    /**
     * @return all this counter's values (successful operations count, successful operations cumulated time, failure
     *         counters, etc.) and reset all values.
     *
     * @see CounterValues
     */
    CounterValues getCounterValuesAndReset();

}
