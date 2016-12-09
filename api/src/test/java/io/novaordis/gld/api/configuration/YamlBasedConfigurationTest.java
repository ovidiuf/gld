/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.ConfigurationTest;
import io.novaordis.gld.api.StoreConfiguration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public class YamlBasedConfigurationTest extends ConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(YamlBasedConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private File scratchDirectory;
    private File baseDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());

        baseDirectory = new File(System.getProperty("basedir"));
        assertTrue(baseDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //

        assertTrue(io.novaordis.utilities.Files.rmdir(scratchDirectory, false));
    }

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void missingServiceSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f, "# empty"));

        try {

            new YamlBasedConfiguration(f);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "'" + YamlBasedConfiguration.SERVICE_SECTION_LABEL +
                            "' section empty or missing from configuration file .*test.yml"));
        }
    }

    @Test
    public void emptyServiceSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "\n" +
                        "\n"));

        try {

            new YamlBasedConfiguration(f);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "'" + YamlBasedConfiguration.SERVICE_SECTION_LABEL +
                            "' section empty or missing from configuration file .*test.yml"));
        }
    }

    @Test
    public void unknownServiceType() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                        "service:\n" +
                        "  type: no-such-service-type\n" +
                        "\n"));

        try {

            new YamlBasedConfiguration(f);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "unknown service type 'no-such-service-type' in configuration file .*test.yml"));
        }
    }

    @Test
    public void missingLoadSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: mock\n" +
                        "\n"));

        try {

            new YamlBasedConfiguration(f);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches(
                    "'" + YamlBasedConfiguration.LOAD_SECTION_LABEL +
                            "' section empty or missing from configuration file .*test.yml"));
        }
    }

    @Test
    public void missingStoreSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: mock\n" +
                        "load:\n" +
                        "  threads: 1\n" +
                        "\n"));

        //
        // we should be fine, if no store section is found, it means we don't store keys
        //
        YamlBasedConfiguration c = new YamlBasedConfiguration(f);
        StoreConfiguration sc = c.getStoreConfiguration();
        assertNull(sc);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected YamlBasedConfiguration getConfigurationToTest() throws Exception {

        File f = new File(baseDirectory, "src/test/resources/data/reference-configuration.yml");
        assertTrue(f.isFile());

        return new YamlBasedConfiguration(f);
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
