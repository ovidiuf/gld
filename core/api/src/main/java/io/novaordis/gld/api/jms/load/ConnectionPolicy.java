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

package io.novaordis.gld.api.jms.load;

public enum ConnectionPolicy {

    //
    // One connection is created at the beginning of the load run and that connection is used throughout the load run
    // (this is the default in absence of an explicitly configured session policy)
    //
    CONNECTION_PER_RUN,
    CONNECTION_PER_THREAD,
    CONNECTION_PER_OPERATION,
    CONNECTION_POOL,
    ;
}
