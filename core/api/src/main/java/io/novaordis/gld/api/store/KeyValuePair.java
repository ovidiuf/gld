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

package io.novaordis.gld.api.store;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class KeyValuePair {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String key;
    private StoredValue value;

    // Constructors ----------------------------------------------------------------------------------------------------

    public KeyValuePair(String key, StoredValue value) {

        this.key = key;
        this.value = value;
    }

    public KeyValuePair(String key) {

        this(key, Null.INSTANCE);
    }

    public KeyValuePair() {

        this(null, Null.INSTANCE);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getKey() {

        return key;
    }

    /**
     * @exception IllegalArgumentException if the key is null
     */
    public void setKey(String key) {

        if (key == null) {

            throw new IllegalArgumentException("null key");
        }

        this.key = key;
    }

    /**
     * Never returns null.
     */
    public StoredValue getValue() {

        return value;
    }

    public void setValue(StoredValue v) {

        if (v == null) {

            throw new IllegalArgumentException("null StoredValue instance");
        }

        this.value = v;
    }

    @Override
    public String toString() {

        return "" + key + ", " + value;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
