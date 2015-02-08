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

package com.novaordis.gld.statistics;

import com.novaordis.ac.Collector;
import com.novaordis.ac.CollectorFactory;
import com.novaordis.gld.Configuration;
import com.novaordis.gld.Statistics;
import com.novaordis.gld.ThrowableHandler;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.command.Load;

public class StatisticsFactory
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static Statistics getInstance(Configuration configuration, String label) throws Exception
    {
        Statistics result;

        if (label == null || "csv".equals(label))
        {
            // this is the default when nothing is specified
            Load command = (Load)configuration.getCommand();
            Collector collector = CollectorFactory.getInstance("SAMPLE COLLECTOR", Thread.NORM_PRIORITY + 1);
            collector.registerHandler(new SampleHandler(System.getProperty("collector.file")));
            if (configuration.getExceptionFile() != null)
            {
                collector.registerHandler(new ThrowableHandler(configuration.getExceptionFile()));
            }

            result = new CollectorBasedCsvStatistics(
                collector, CollectorBasedCsvStatistics.DEFAULT_SAMPLING_INTERVAL_MS, command.getMaxOperations());

        }
        else if ("none".equals(label))
        {
            result = null;
        }
        else
        {
            throw new UserErrorException("unknown Statistics type: '" + label + "'");
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private StatisticsFactory()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
