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
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ActiveMQConfigurationUtilTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ActiveMQConfigurationUtilTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // sanitizeMemoryUsage() -------------------------------------------------------------------------------------------

    @Test
    public void sanitizeMemoryUsage_InvalidValue() throws Exception
    {
        try
        {
            ActiveMQConfigurationUtil.sanitizeMemoryUsage("G");
            fail("should fail, not a valid memory limit");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void sanitizeMemoryUsage_InvalidUnit() throws Exception
    {
        try
        {
            ActiveMQConfigurationUtil.sanitizeMemoryUsage("10blah");
            fail("should fail, not a valid unit");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void sanitizeMemoryUsage() throws Exception
    {
        String s = ActiveMQConfigurationUtil.sanitizeMemoryUsage("5G");
        assertEquals("5 gb", s);
    }

    @Test
    public void sanitizeMemoryUsage2() throws Exception
    {
        String s = ActiveMQConfigurationUtil.sanitizeMemoryUsage("5GB");
        assertEquals("5 gb", s);
    }

    @Test
    public void sanitizeMemoryUsage_OnlyDigits() throws Exception
    {
        String s = ActiveMQConfigurationUtil.sanitizeMemoryUsage("5");
        assertEquals("5", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
