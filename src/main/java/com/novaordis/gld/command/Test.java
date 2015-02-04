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

package com.novaordis.gld.command;

import com.novaordis.gld.ConfigurationImpl;
import com.novaordis.gld.CollectorBasedStatistics;
import com.novaordis.gld.Util;

import java.util.concurrent.ThreadLocalRandom;

public class Test extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Test(ConfigurationImpl c)
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isInitialized()
    {
        return false;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();

        long t0 = System.nanoTime();
        long iterations = 10000000;
        for (int i = 0; i < iterations; i ++)
        {
            Util.getRandomKey(ThreadLocalRandom.current(), 70);
            //Util.getRandomKeyUUID(70);
        }
        long t1 = System.nanoTime();

        double msecs = ((double)(t1 - t0))/ CollectorBasedStatistics.NANOS_IN_MILLS;
        double ips = ((double)iterations)/msecs;

        System.out.println(CollectorBasedStatistics.DURATION_MS_FORMAT.format(ips) + " iterations per ms");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
