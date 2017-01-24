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

import io.novaordis.utilities.UserErrorException;

public enum ConnectionPolicy {

    //
    // One connection is created at the beginning of the load run and that connection is used throughout the load run
    // (this is the default in absence of an explicitly configured session policy)
    //
    CONNECTION_PER_RUN("connection-per-run"),
    CONNECTION_PER_THREAD("connection-per-thread"),
    CONNECTION_PER_OPERATION("connection-per-operation"),
    CONNECTION_POOL("connection-pool"),
    ;

    static ConnectionPolicy fromString(String s) throws UserErrorException {

        ConnectionPolicy[] values = ConnectionPolicy.values();
        String msg = "";

        for(int i = 0; i < values.length; i ++) {

            ConnectionPolicy cp = values[i];

            if (cp.getLabel().equals(s)) {

                return cp;
            }

            msg += "'" + cp.getLabel() + "'";

            if (i < values.length - 1) {

                msg += ", ";
            }
        }

        msg = "invalid connection policy '" + s + "', valid options: " + msg;
        throw new UserErrorException(msg);
    }

    private String label;

    ConnectionPolicy(String label) {

        this.label = label;
    }

    public String getLabel() {

        return label;
    }
}
