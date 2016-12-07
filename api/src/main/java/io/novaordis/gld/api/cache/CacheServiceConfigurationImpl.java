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

package io.novaordis.gld.api.cache;

import io.novaordis.gld.api.configuration.RawConfigurationMapWrapper;
import io.novaordis.utilities.UserErrorException;

import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/6/16
 */
public class CacheServiceConfigurationImpl extends RawConfigurationMapWrapper implements CacheServiceConfiguration  {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public CacheServiceConfigurationImpl(Map<String, Object> rawConfiguration) throws Exception {

        super(rawConfiguration);
    }

    // CacheServiceConfiguration implementation ------------------------------------------------------------------------

    @Override
    public int getKeySize() throws UserErrorException {

        Map<String, Object> m = getRawConfigurationMap();

        Object o = m.get(CacheServiceConfiguration.KEY_SIZE_LABEL);

        consistencyCheck(CacheServiceConfiguration.KEY_SIZE_LABEL, o, Integer.class);

        return (Integer)o;
    }

    @Override
    public int getValueSize() throws UserErrorException {

        Map<String, Object> m = getRawConfigurationMap();

        Object o = m.get(CacheServiceConfiguration.VALUE_SIZE_LABEL);

        consistencyCheck(CacheServiceConfiguration.VALUE_SIZE_LABEL, o, Integer.class);

        return (Integer)o;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void consistencyCheck(String label, Object o, Class c) throws UserErrorException {

        if (o == null) {

            throw new UserErrorException("missing required configuration element '" +label + "'");
        }

        Class actualClass = o.getClass();

        if (!actualClass.equals(c)) {
            throw new UserErrorException(
                    "'" + label + "' should be " + c.getSimpleName() + ", but it is " + actualClass.getSimpleName());
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
