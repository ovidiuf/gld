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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.LoadStrategy;
import com.novaordis.gld.Operation;
import com.novaordis.gld.Service;
import org.apache.log4j.Logger;

public class MockOperation implements Operation {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockOperation.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean verbose;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Operation implementation ----------------------------------------------------------------------------------------

    @Override
    public void perform(Service cs) throws Exception
    {
        if (verbose) { log.info(this + " mock perform(" + cs + ")"); }
        cs.perform(this);
    }

    @Override
    public LoadStrategy getLoadStrategy()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * We need to explicitly set the instance as verbose in order to get log.info(), otherwise the high concurrency
     * tests are too noisy.
     */
    public void setVerbose(boolean b) {
        this.verbose = b;
    }

    public String toString()
    {
        return "MockOperation[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
