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

package com.novaordis.gld.service.jms.activemq;

import com.novaordis.gld.UserErrorException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveMQConfigurationUtil
{

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Pattern MEMORY_LIMIT_PATTERN = Pattern.compile("^(\\d+)([\\p{Alpha}+]*)$");


    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Gets a command line string that may follow the --memoryUsage option, verify the syntax and the semantics and
     * converts it into a string that is acceptable to ActiveMQ. A typical memory size string is a series of digits
     * followed by a unit.
     *
     * @param commandLineArgument = may not contain spaces.
     *
     * @return a string value that includes spaces, valid in the context of an ActiveMQ configuration file.
     * Example: "5G" is converted to "5 gb".
     *
     * @throws UserErrorException
     */
    public static String sanitizeMemoryUsage(String commandLineArgument) throws UserErrorException
    {
        Matcher m = MEMORY_LIMIT_PATTERN.matcher(commandLineArgument);

        if (!m.find())
        {
            throw new UserErrorException("'" + commandLineArgument + "' is not an ActiveMQ memoryUsage");
        }

        String numericValueString = m.group(1);
        String unit = m.group(2);

        int numericValue;

        try
        {
            numericValue = Integer.parseInt(numericValueString);

        }
        catch(Exception e)
        {
            throw new UserErrorException(
                "'" + commandLineArgument + "' is not an ActiveMQ memoryUsage because '" + numericValueString +
                    "' is not a valid numeric value", e);
        }

        if (unit.equalsIgnoreCase("g") || unit.equalsIgnoreCase("gb"))
        {
            return numericValue + " gb";
        }

        if (unit.equalsIgnoreCase("m") || unit.equalsIgnoreCase("mb"))
        {
            return numericValue + " mb";
        }

        if (unit.equalsIgnoreCase("k") || unit.equalsIgnoreCase("kb"))
        {
            return numericValue + " kb";
        }

        if (unit.equals(""))
        {
            return "" + numericValue;
        }

        throw new UserErrorException("'" + commandLineArgument + "' is not an ActiveMQ memoryUsage because '" +
            unit + "' is not a valid memory unit, use 'mb' or 'gb'");
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private ActiveMQConfigurationUtil()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
