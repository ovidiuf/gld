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

public interface Counter
{
    /**
     * Update the corresponding counter using a non-blocking algorithm (CAS).
     *
     * @param t0Ms - the time (in milliseconds) when the operation that is being recorded started.
     * @param t0Nano - the time (in nanoseconds) when the operation that is being recorded started. Logically, it
     *        should be the same as t0Ms, but Java documentation advises against using nano-second precision time
     *        to get absolute time information, so we are only using it to calculate delta in conjunction with 't1Nano'.
     * @param t1Nano - the time (in nanoseconds) when the operation that is being recorded ended. Java documentation
     *        advises against using nano-second precision time to get absolute time information, so we are only using
     *        this value to calculate delta in conjunction with 't0Nano'.
     * @param t - optionally a Throwable associated with the operation.
     *
     * @throws java.lang.IllegalArgumentException if t1Nano precedes t0Nano.
     */
    public void update(long t0Ms, long t0Nano, long t1Nano, Throwable... t);

    public Class getOperationType();

}
