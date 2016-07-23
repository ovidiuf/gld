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

package com.novaordis.gld.strategy.storage;

import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.mock.MockConfiguration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextFileStorageStrategyTest extends StorageStrategyTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TextFileStorageStrategyTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // configure() -----------------------------------------------------------------------------------------------------

    @Test
    public void configure_NoFileName() throws Exception
    {
        TextFileStorageStrategy tfss = getStorageStrategyToTest();

        List<String> emptyArgumentList = new ArrayList<>();

        try
        {
            tfss.configure(new MockConfiguration(), emptyArgumentList, 0);
            fail("should fail with UserErrorException, no output file name");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void configure_outputIsTheLastArgument() throws Exception
    {
        TextFileStorageStrategy tfss = getStorageStrategyToTest();

        List<String> arguments = new ArrayList<>(Arrays.asList("something", "something-else", "--output"));

        try
        {
            tfss.configure(new MockConfiguration(), arguments, 2);
            fail("should fail with UserErrorException, --output last in list");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void configure() throws Exception
    {
        TextFileStorageStrategy tfss = getStorageStrategyToTest();

        List<String> arguments = new ArrayList<>(Arrays.asList(
            "something", "something-else", "--output", "./file.txt"));

        tfss.configure(new MockConfiguration(), arguments, 2);

        assertEquals("./file.txt", tfss.getFileName());
    }

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Test
    public void startStoreStop() throws Exception
    {
        File file = new File(Tests.getScratchDir(), "test-keys.txt");

        TextFileStorageStrategy ss = getStorageStrategyToTest();

        List<String> arguments = new ArrayList<>();
        arguments.add("--output");
        arguments.add(file.getPath());

        ss.configure(new MockConfiguration(), arguments, 0);

        ss.start();

        ss.store("KEY-01", "VALUE-01");
        ss.store("KEY-02", "VALUE-02");
        ss.store("KEY-03", "VALUE-03");

        ss.stop();

        String content = Files.read(file);

        log.info(content);

        assertEquals("KEY-01=VALUE-01\nKEY-02=VALUE-02\nKEY-03=VALUE-03\n", content);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TextFileStorageStrategy getStorageStrategyToTest() throws Exception
    {
        return new TextFileStorageStrategy();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
