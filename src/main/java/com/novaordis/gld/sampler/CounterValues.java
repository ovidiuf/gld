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

/**
 * Instances are NOT supposed to be accessed concurrently from multiple threads and must not be thread safe, for
 * performance reasons. The Counter implementations provide protection for those situations.
 */
public interface CounterValues
{
    long getSuccessCount();

    long getSuccessCumulatedTime();

//    int getFailureCount();
//    long getFailureCumulatedTime();
//    List<Throwable> getFailureTypes();

}
