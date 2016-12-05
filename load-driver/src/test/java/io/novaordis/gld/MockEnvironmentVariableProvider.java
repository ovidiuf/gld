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

package io.novaordis.gld;

import io.novaordis.utilities.env.EnvironmentVariableProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class MockEnvironmentVariableProvider implements EnvironmentVariableProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, String> values;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockEnvironmentVariableProvider() {

        this.values = new HashMap<>();
    }

    // EnvironmentVariableProvider implementation ----------------------------------------------------------------------

    @Override
    public String getenv(String s) {

        if (s == null) {
            throw new NullPointerException("null name");
        }

        return values.get(s);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setenv(String name, String value) {

        values.put(name, value);

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
