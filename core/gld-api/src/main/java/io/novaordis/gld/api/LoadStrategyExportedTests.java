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

package io.novaordis.gld.api;

import io.novaordis.gld.api.todiscard.Configuration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * API call tests to be exported to the implementation modules.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public abstract class LoadStrategyExportedTests {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LoadStrategyExportedTests.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Exported Tests --------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void nullArguments() throws Exception {

        LoadStrategy s = getLoadStrategyToTest(null, null, -1);

        Configuration c = getConfigurationToTestWith();

        s.configure(c, null, -1);

        // we should be fine, null means no more arguments to look at
    }

    @Test
    public void fromOutOfBounds_InferiorLimit() throws Exception {

        LoadStrategy s = getLoadStrategyToTest(null, null, -1);

        List<String> args = Arrays.asList("blah", "blah", "blah");

        Configuration c = getConfigurationToTestWith();

        try {

            s.configure(c, args, -1);
            fail("should fail with ArrayIndexOutOfBoundsException because from is lower than acceptable");
        }
        catch(ArrayIndexOutOfBoundsException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void nullConfiguration() throws Exception {

        LoadStrategy s = getLoadStrategyToTest(null, null, -1);

        List<String> args = Arrays.asList("blah", "blah", "blah");

        try {

            s.configure(null, args, 1);
            fail("should fail with IllegalArgumentException on account of null configuration");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // next() ----------------------------------------------------------------------------------------------------------

    @Test
    public void unconfiguredStrategyFailsUponFirstUsage() throws Exception {

        LoadStrategy s = getLoadStrategyToTest(null, null, -1);

        try {

            s.next(null, null, false);
            fail("should fail with IllegalStateException because it was not configured");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @param config - configuration instance. Can be null.
     * @param arguments - strategy-specific arguments. Can be null.
     * @param from - the argument in the list of strategy-specific arguments.
     * @return a strategy that can be fully configured or not configured, depending on the config, arguments and from
     *     values.
     */
    protected abstract LoadStrategy getLoadStrategyToTest(Configuration config, List<String> arguments, int from)
            throws Exception;

    protected abstract Configuration getConfigurationToTestWith() throws Exception;


    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
