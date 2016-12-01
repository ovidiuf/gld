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

package com.novaordis.gld.mock;

import com.novaordis.ac.Handler;
import com.novaordis.gld.statistics.DeprecatedSamplingInterval;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MockHandler implements Handler
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockHandler.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<DeprecatedSamplingInterval> samplingIntervals;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockHandler()
    {
        this.samplingIntervals = new ArrayList<DeprecatedSamplingInterval>();
    }

    // Handler implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean canHandle(Object o)
    {
        // doesn't matter, we don't even look at this in the mock setup
        return true;
    }

    @Override
    public void handle(long timestamp, String threadName, Object o)
    {
        if (o instanceof DeprecatedSamplingInterval)
        {
            samplingIntervals.add((DeprecatedSamplingInterval) o);
        }
    }

    @Override
    public void close()
    {

    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<DeprecatedSamplingInterval> getSamplingIntervals()
    {
        return samplingIntervals;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
