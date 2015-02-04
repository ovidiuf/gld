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

package com.novaordis.cld.strategy.load;

import com.novaordis.cld.LoadStrategy;
import com.novaordis.cld.mock.MockConfiguration;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public abstract class LoadStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LoadStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullArguments() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();

        try
        {
            s.configure(new MockConfiguration(), null, 0);
            fail("should fail with IllegalArgumentException because of null arguments");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void fromOutOfBounds_InferiorLimit() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();

        List<String> args = Arrays.asList("blah", "blah", "blah");

        try
        {
            s.configure(new MockConfiguration(), args, -1);
            fail("should fail with ArrayIndexOutOfBoundsException because from is lower than acceptable");
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void fromOutOfBounds_SuperiorLimit() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();

        List<String> args = Arrays.asList("blah", "blah", "blah");

        try
        {
            s.configure(new MockConfiguration(), args, 3);
            fail("should fail with ArrayIndexOutOfBoundsException because from is higher than acceptable");
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullConfiguration() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();

        List<String> args = Arrays.asList("blah", "blah", "blah");

        try
        {
            s.configure(null, args, 1);
            fail("should fail with IllegalArgumentException on account of null configuration");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void unconfiguredStrategyFailsUponFirstUsage() throws Exception
    {
        LoadStrategy s = getLoadStrategyToTest();

        try
        {
            s.next(null, null);
            fail("should fail with IllegalStateException because it was not configured");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @return an unconfigured strategy.
     */
    protected abstract LoadStrategy getLoadStrategyToTest() throws Exception;


    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
