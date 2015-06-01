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

package com.novaordis.gld.sampler;

import java.util.ArrayList;
import java.util.List;

public class MockSamplingConsumer implements SamplingConsumer
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<SamplingInterval> samplingIntervals;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockSamplingConsumer()
    {
        this.samplingIntervals = new ArrayList<>();
    }

    // SamplingConsumer implementation ---------------------------------------------------------------------------------

    @Override
    public void consume(SamplingInterval... samplingIntervals)
    {
        for(SamplingInterval si: samplingIntervals)
        {
            this.samplingIntervals.add(si);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<SamplingInterval> getSamplingIntervals()
    {
        return samplingIntervals;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
