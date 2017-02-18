/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package io.novaordis.gld.api.service;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public enum ServiceType {

    cache(12, 1024),
    jms(null, 1024),
    http(10, 1024),
    mock(10, 77889),    // used for testing
    unknown(10, 1024), // used for testing
    ;

    public static ServiceType fromString(String s) {

        for(ServiceType t: values()) {

            if (t.toString().equalsIgnoreCase(s)) {

                return t;
            }
        }

        throw new IllegalArgumentException("unknown service type '" + s + "'");
    }

    private Integer defaultKeySize;
    private int defaultValueSize;

    ServiceType(Integer defaultKeySize, int defaultValueSize) {

        this.defaultKeySize = defaultKeySize;
        this.defaultValueSize = defaultValueSize;
    }

    /**
     * Return the default key size for the service, in characters. Some services (JMS) do not have a default key size,
     * as it is provided by the implementation, so the method may return null.
     */
    public Integer getDefaultKeySize() {

        return defaultKeySize;
    }

    /**
     * Return the default value size for the service, in bytes.
     */
    public int getDefaultValueSize() {

        return defaultValueSize;
    }

}
