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

package com.novaordis.gld.strategy.load;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.ContentType;
import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;

import java.util.List;

public class LoadStrategyFactory
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Processes the argument list provided by the upper layer, removing from the list all the arguments pertaining
     * to us, including the --load-strategy argument, and downward recursively, to whatever StorageFactory we're
     * building.
     *
     * TODO this method is virtually identical with StorageStrategyFactory, consolidate.
     *
     * @see com.novaordis.gld.strategy.storage.StorageStrategyFactory
     *
     * @param arguments - mutable list of arguments, arguments pertaining to us will be removed.
     * @param from - the argument list index where we expect to find "--storage-strategy"
     *
     * @return a configured LoadStrategy instance.
     */
    public static LoadStrategy fromArguments(Configuration configuration, List<String> arguments, int from)
        throws Exception
    {
        if (!arguments.get(from).equals("--load-strategy") && !arguments.get(from).equals("--strategy"))
        {
            throw new IllegalArgumentException("expecting '--load-strategy' on position " + from + " in " + arguments);
        }

        LoadStrategy result;

        arguments.remove(from);

        if (from >= arguments.size())
        {
            throw new UserErrorException("a load strategy name should follow --load-strategy",
                new NullPointerException("null storage strategy"));
        }

        String strategyName = arguments.remove(from);
        String originalStrategyName = strategyName;

        // user friendliness - if the first letter of the strategy name is not capitalized,
        // capitalize it for her. This will allow the user to specify --load-strategy read

        if (Character.isLowerCase(strategyName.charAt(0)))
        {
            strategyName = Character.toUpperCase(strategyName.charAt(0)) + strategyName.substring(1);
        }

        ContentType contentType = configuration.getContentType();

        String subPackage = ContentType.MESSAGE.equals(contentType) ? "jms" : "cache";

        try
        {
            result = Util.getInstance(LoadStrategy.class,
                "com.novaordis.gld.strategy.load." + subPackage, strategyName, "LoadStrategy");
        }
        catch(Exception e)
        {
            // turn all load strategy loading exceptions into UserErrorExceptions and bubble them up
            String msg = "invalid load strategy \"" + originalStrategyName + "\": " + e.getMessage();
            Throwable cause = e.getCause();
            throw new UserErrorException(msg, cause);
        }

        // provide the rest of the arguments to the strategy, which will pick the ones it needs and
        // remove them from the list, leaving the rest of the arguments in the list

        result.configure(configuration, arguments, from);

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private LoadStrategyFactory()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
