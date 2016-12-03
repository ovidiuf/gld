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

package io.novaordis.gld.driver.todeplete.command;

import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;

public class GenerateKeysTest extends CommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(GenerateKeysTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

//    @Test
//    public void missingKeyCount() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        GenerateKeys gc = new GenerateKeys(mc);
//
//        try
//        {
//            gc.getKeyCount();
//            fail("should fail with UserErrorException on account of missing key count");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void keyCountNotAnInteger() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        GenerateKeys gc = new GenerateKeys(mc);
//        gc.addArgument("blah");
//
//        try
//        {
//            gc.getKeyCount();
//            fail("should fail with UserErrorException on account of invalid key count");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void keyCount() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        GenerateKeys gc = new GenerateKeys(mc);
//        gc.addArgument("15");
//
//        int i = gc.getKeyCount();
//
//        assertEquals(15, i);
//    }
//
//    @Test
//    public void missingFileName() throws Exception
//    {
//        MockConfiguration mc = new MockConfiguration();
//        GenerateKeys gc = new GenerateKeys(mc);
//        gc.addArgument("10");
//
//        try
//        {
//            gc.initialize();
//            fail("should fail with UserErrorException on account of missing key store file name");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }
//
//    @Test
//    public void generateKeys() throws Exception
//    {
//        File f = new File(Tests.getScratchDir(), "keys.txt");
//
//        MockConfiguration mc = new MockConfiguration();
//        GenerateKeys gc = new GenerateKeys(mc);
//        gc.addArgument("3");
//        mc.setKeyStoreFile(f.getPath());
//
//        gc.initialize();
//
//        gc.execute();
//
//        assertTrue(f.isFile());
//
//        String s = Files.read(f);
//        StringTokenizer st = new StringTokenizer(s, "\n");
//
//        assertTrue(st.hasMoreTokens());
//        log.info(st.nextToken());
//
//        assertTrue(st.hasMoreTokens());
//        log.info(st.nextToken());
//
//        assertTrue(st.hasMoreTokens());
//        log.info(st.nextToken());
//
//        assertFalse(st.hasMoreTokens());
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected GenerateKeys getCommandToTest(Configuration c)
    {
        return new GenerateKeys(c);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
