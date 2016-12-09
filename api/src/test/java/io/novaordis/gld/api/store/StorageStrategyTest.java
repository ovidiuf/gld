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

package io.novaordis.gld.api.store;

import io.novaordis.gld.api.todiscard.StorageStrategy;
import org.apache.log4j.Logger;

import static junit.framework.TestCase.fail;

@Deprecated
public abstract class StorageStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(StorageStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

//    @Test
//    public void insureConfigureBehavesConsistentlyOnNullList() throws Exception
//    {
//        StorageStrategy ss = getStorageStrategyToTest();
//
//        try
//        {
//            //            Configuration c = new MockConfiguration();
//            Configuration c = null;
//
//            ss.configure(c, null, -1);
//            fail("should be failing with IllegalArgumentException");
//        }
//        catch(IllegalArgumentException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void invalidFromValue() throws Exception
//    {
//        StorageStrategy ss = getStorageStrategyToTest();
//
//        try
//        {
//            //            Configuration c = new MockConfiguration();
//            Configuration c = null;
//
//            ss.configure(c, new ArrayList<String>(), -1);
//            fail("should be failing with ArrayIndexOutOfBoundsException");
//        }
//        catch(ArrayIndexOutOfBoundsException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void insureStartFailsIfNotConfigured() throws Exception
//    {
//        StorageStrategy ss = getStorageStrategyToTest();
//
//        if (ss.isConfigured())
//        {
//            log.info(ss + " already configured, OK");
//            return;
//        }
//
//        try
//        {
//            ss.start();
//            fail("should be failing with IllegalStateException");
//        }
//        catch(IllegalStateException e)
//        {
//            log.info(e.getMessage());
//        }
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract StorageStrategy getStorageStrategyToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
