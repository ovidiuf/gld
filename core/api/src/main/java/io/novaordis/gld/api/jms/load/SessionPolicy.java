/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.jms.load;

import io.novaordis.utilities.UserErrorException;

public enum SessionPolicy {

    SESSION_PER_THREAD,

    //
    // A session is created every time an operation is about to be executed against the server, used to execute the
    // operation and then closed and discarded (this is the default in absence of an explicitly configured session
    // policy)
    //
    SESSION_PER_OPERATION,
    ;

    static SessionPolicy fromString(String s) throws UserErrorException {

        throw new RuntimeException("NYE");
    }
}
