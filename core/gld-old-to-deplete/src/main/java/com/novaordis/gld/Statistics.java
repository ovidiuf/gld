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

package com.novaordis.gld;

import io.novaordis.gld.api.Operation;

public interface Statistics
{
    boolean areWeDone();

    /**
     * As per Java documentation, we don't use t0Nano and t1Nano to get absolute time information, we're using them
     * only to calculate deltas. Absolute timestamp for the sample is provided by t0Ms.
     *
     * @param t0Ms - the time (in milliseconds) when the operation that is being recorded started.
     *
     * @param t0Nano - the time (in nanoseconds) when the operation that is being recorded started. Logically, it
     *        should be the same as t0Ms, but Java documentation advises against using nano-second precision time
     *        to get absolute time information, so we are only using it to calculate delta in conjunction with 't1Nano'.
     *
     * @param t1Nano - the time (in nanoseconds) when the operation that is being recorded ended. Java documentation
     *        advises against using nano-second precision time to get absolute time information, so we are only using
     *        this value to calculate delta in conjunction with 't0Nano'.
     */
    void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable t);

    /**
     * Flushes the in-flight information and prevent new recordings (record will throw an IllegalStateException if
     * invoked).
     */
    void close();

    /**
     * Annotate the statistics, using the current time stamp.
     */
    void annotate(String line);
}
