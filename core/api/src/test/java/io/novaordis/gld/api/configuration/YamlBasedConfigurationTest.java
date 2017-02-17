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

import io.novaordis.gld.api.sampler.SamplerConfiguration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    // general Yaml behavior -------------------------------------------------------------------------------------------

    @Test
    public void list() throws Exception {

        String content =
                "some-list:\n" +
                        " - a\n" +
                        " - b\n" +
                        " - c\n";

        ByteArrayInputStream baos = new ByteArrayInputStream(content.getBytes());

        Yaml yaml = new Yaml();

        Map m = (Map)yaml.load(baos);

        List l = (List)m.get("some-list");

        assertEquals(3, l.size());

        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertEquals("c", l.get(2));
    }

    // load ------------------------------------------------------------------------------------------------------------

    @Test
    public void load_EmptyFile() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        Files.write(f, "");

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        try {

            c.load(f);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("empty configuration file"));
        }
    }

    @Test
    public void load_CommentsOnly() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f, "# empty"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        try {

            c.load(f);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("empty configuration file"));
        }
    }

    @Test
    public void load_emptyServiceSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "\n" +
                        "\n"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        try {

            c.load(f);
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
    public void load_unknownServiceType() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                        "service:\n" +
                        "  type: no-such-service-type\n" +
                        "\n"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        try {

            c.load(f);
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
    public void load_missingLoadSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: cache\n" +
                        "\n"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        c.load(f);

        LoadConfiguration lc = c.getLoadConfiguration();

        assertNotNull(lc);

        //
        // default behavior
        //
        assertEquals(LoadConfiguration.DEFAULT_THREAD_COUNT, lc.getThreadCount());
        assertNull(lc.getOperations());
    }

    @Test
    public void load_missingStoreSection() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: cache\n" +
                        "load:\n" +
                        "  threads: 1\n" +
                        "\n"));

        //
        // we should be fine, if no store section is found, it means we don't store keys
        //
        YamlBasedConfiguration c = new YamlBasedConfiguration();
        c.load(f);
        StoreConfiguration sc = c.getStoreConfiguration();
        assertNull(sc);
    }

    @Test
    public void load_storeConfiguration() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: cache\n" +
                        "load:\n" +
                        "  threads: 1\n" +
                        "store:\n" +
                        "  type: mock\n" +
                        "  directory: .\n" +
                        "\n"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();
        c.load(f);
        StoreConfiguration sc = c.getStoreConfiguration();
        assertEquals("mock", sc.getStoreType());
    }

    @Test
    public void load_outputConfiguration() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        assertTrue(Files.write(f,
                "service:\n" +
                        "  type: cache\n" +
                        "  implementation:\n" +
                        "    name: embedded\n" +
                        "output:\n" +
                        "  statistics:\n" +
                        "    file: test.csv\n" +
                        "\n"));

        YamlBasedConfiguration c = new YamlBasedConfiguration();
        c.load(f);

        OutputConfiguration oc = c.getOutputConfiguration();
        SamplerConfiguration sc = oc.getSamplerConfiguration();
        assertNotNull(sc);
    }

    @Test
    public void load_noParentSpecifiedForConfigurationFile() throws Exception {

        File f = new File("no-such-file.yml");

        //
        // we know there's no such file, but we want to make sure that parent directory configuration is done
        // correctly
        //

        YamlBasedConfiguration c = new YamlBasedConfiguration();
        assertNull(c.getConfigurationDirectory());

        try {

            c.load(f);

        }
        catch(FileNotFoundException e) {

            //
            // that's fine
            //
            log.info(e.getMessage());
        }

        //
        // make sure the parent directory was initialized correctly
        //
        assertEquals(new File("."), c.getConfigurationDirectory());
    }

    @Test
    public void load_RandomContent() throws Exception {

        File f = new File(scratchDirectory, "test.yml");
        Files.write(f, "some random content\nsome other random content\n");

        YamlBasedConfiguration c = new YamlBasedConfiguration();

        try {

            c.load(f);
            fail("should throw exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("invalid configuration file "));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected YamlBasedConfiguration getConfigurationToTest() throws Exception {

        File f = new File(baseDirectory, "src/test/resources/data/cache-service-reference-configuration.yml");
        assertTrue(f.isFile());

        YamlBasedConfiguration c = new YamlBasedConfiguration();
        c.load(f);
        return c;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
