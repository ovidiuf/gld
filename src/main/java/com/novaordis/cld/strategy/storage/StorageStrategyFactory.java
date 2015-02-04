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

package com.novaordis.cld.strategy.storage;

import com.novaordis.cld.Configuration;
import com.novaordis.cld.StorageStrategy;
import com.novaordis.cld.UserErrorException;
import com.novaordis.cld.Util;

import java.util.List;

public class StorageStrategyFactory
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Processes the argument list provided by the upper layer, removing the arguments pertaining to us (and
     * downward recursively, to whatever StorageFactory we're building) from the list.
     *
     * TODO this method is virtually identical with LoadStrategyFactory, consolidate.
     *
     * @see com.novaordis.cld.strategy.load.LoadStrategyFactory
     *
     * @param arguments - mutable list of arguments, arguments pertaining to us will be removed.
     * @param from - the argument list index where we expect to find "--storage-strategy"
     *
     * @return a configured StorageStrategy instance.
     */
    public static StorageStrategy fromArguments(Configuration conf, List<String> arguments, int from) throws Exception
    {
        if (!arguments.get(from).equals("--storage-strategy"))
        {
            throw new IllegalArgumentException("expecting '--storage-strategy' on position " + from + " in " + arguments);
        }

        StorageStrategy result;

        arguments.remove(from);

        if (from >= arguments.size())
        {
            throw new UserErrorException("a storage strategy name should follow --storage-strategy",
                new NullPointerException("null storage strategy"));
        }

        String strategyName = arguments.remove(from);
        String originalStrategyName = strategyName;

        // user friendliness - if the first letter of the strategy name is not capitalized,
        // capitalize it for her. This will allow the user to specify --storage-strategy stdout

        if (Character.isLowerCase(strategyName.charAt(0)))
        {
            strategyName = Character.toUpperCase(strategyName.charAt(0)) + strategyName.substring(1);
        }

        try
        {
            result = Util.getInstance(StorageStrategy.class,
                "com.novaordis.cld.strategy.storage", strategyName, "StorageStrategy");
        }
        catch(Exception e)
        {
            // turn all storage strategy loading exceptions into UserErrorExceptions and bubble them up
            String msg = "invalid storage strategy \"" + originalStrategyName + "\": " + e.getMessage();
            Throwable cause = e.getCause();
            throw new UserErrorException(msg, cause);
        }

        // provide the rest of the arguments to the strategy, which will pick the ones it needs and
        // remove them from the list, leaving the rest of the arguments in the list

        result.configure(conf, arguments, from);

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private StorageStrategyFactory()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------


}
