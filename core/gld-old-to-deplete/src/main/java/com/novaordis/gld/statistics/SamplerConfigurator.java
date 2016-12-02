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

import com.novaordis.gld.UserErrorException;
import io.novaordis.gld.driver.sampler.Sampler;
import io.novaordis.gld.driver.sampler.SamplerImpl;
import io.novaordis.gld.driver.sampler.SamplingConsumer;
import io.novaordis.gld.driver.sampler.metrics.FreePhysicalMemorySize;
import io.novaordis.gld.driver.sampler.metrics.SystemCpuLoad;
import io.novaordis.gld.driver.sampler.metrics.SystemLoadAverage;
import io.novaordis.gld.driver.sampler.metrics.TotalPhysicalMemorySize;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SamplerConfigurator
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final long DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS = 250L;
    public static final long DEFAULT_SAMPLING_INTERVAL_MS = 1000L;

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * @param outputFile may be null
     */
    public static Sampler getSampler(String outputFile, String label) throws Exception
    {
        Sampler result;

        if (label == null || "csv".equals(label))
        {
            result = new SamplerImpl(DEFAULT_SAMPLING_TASK_RUN_INTERVAL_MS, DEFAULT_SAMPLING_INTERVAL_MS);

            result.registerMetric(TotalPhysicalMemorySize.class);
            result.registerMetric(FreePhysicalMemorySize.class);
            result.registerMetric(SystemCpuLoad.class);
            result.registerMetric(SystemLoadAverage.class);

            if (outputFile != null)
            {
                // if we specify an output file, it means we will write the statistics into that file
                PrintWriter filePrintWriter;

                try
                {
                    filePrintWriter = new PrintWriter(new FileWriter(outputFile));
                }
                catch(IOException e)
                {
                    throw new UserErrorException(
                        "cannot write file " + outputFile + ", it is either a directory or wrong permissions are in place", e);
                }


                SamplingConsumer csvFileWriter = new CSVFormatter(filePrintWriter);
                result.registerConsumer(csvFileWriter);
            }

            // can't register any operations yet, as we don't know what service we're sampling at this point, so
            // we will register them when we know, and the we will start the sampler
        }
        else if ("none".equals(label))
        {
            result = null;
        }
        else
        {
            throw new UserErrorException("unknown statistics type '" + label + "'");
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private SamplerConfigurator()
    {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
