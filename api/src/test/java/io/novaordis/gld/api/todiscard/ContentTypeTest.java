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

package io.novaordis.gld.api.todiscard;

import io.novaordis.utilities.UserErrorException;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ContentTypeTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ContentTypeTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void nullArgument() throws Exception
    {
        try
        {
            ContentType.fromString(null);
            fail("should fail with IllegalArgumentException, null");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void unknown() throws Exception
    {
        try
        {
            ContentType.fromString("blah");
            fail("should fail with UserErrorException, unknown type");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void keyvalue() throws Exception
    {
        assertEquals(ContentType.KEYVALUE, ContentType.fromString("keyvalue"));
    }

    @Test
    public void keyDashvalue() throws Exception
    {
        assertEquals(ContentType.KEYVALUE, ContentType.fromString("key-value"));
    }

    @Test
    public void jms() throws Exception
    {
        assertEquals(ContentType.JMS, ContentType.fromString("jms"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
