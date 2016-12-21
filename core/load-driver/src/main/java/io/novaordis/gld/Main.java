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

package io.novaordis.gld;

import io.novaordis.gld.api.configuration.Configuration;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.configuration.YamlBasedConfiguration;
import io.novaordis.gld.command.Command;
import io.novaordis.gld.driver.LoadDriverImpl;
import io.novaordis.gld.driver.Util;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.env.EnvironmentVariableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class Main {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME = "GLD_CONF_FILE";

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        //
        // attempt to handle first the commands that do not need a load driver instance
        //

        Command c = Command.toCommand(arguments);

        if (c != null) {

            c.execute();
            System.exit(0);
        }

        //
        // instantiate the load driver instance and execute the life cycle
        //

        int exitCode = loadDriverLifeCycle(arguments);
        System.exit(exitCode);

    }

    // Package protected static ----------------------------------------------------------------------------------------

    /**
     * @return an appropriate exit code: 0 if the driver completed its scenario and exited successfully, non-zero
     * otherwise
     */
    static int loadDriverLifeCycle(List<String> arguments) {

        LoadDriver ld = null;

        int exitCode = 1;

        try {

            boolean background = extractBackgroundSetting(arguments);
            File configurationFile = extractConfigurationFile(arguments);
            Configuration c = new YamlBasedConfiguration(configurationFile);

            log.debug("configuration file " + configurationFile + " is syntactically correct");

            ld = new LoadDriverImpl("0", background);

            ld.init(c);

            log.debug(ld + " initialized");

            ld.run();

            log.debug(ld + " executed scenario successfully");

            exitCode = 0;
        }
        catch(Throwable t) {

            log.debug("load driver failure: " + t.getMessage(), t);

            if (ld != null) {

                ld.error(t);
            }
            else {

                System.out.println(Util.formatErrorMessage(t));
            }
        }

        return exitCode;
    }

    /**
     * Extracts the configuration file from the argument list, removing the related elements from the list. If the
     * configuration file is not specified in the command line argument list, the implementation attempts to locate the
     * name of the configuration file from the environment, using the environment variable 'GLD_CONF_FILE'.
     *
     * @exception io.novaordis.utilities.UserErrorException
     */
    static File extractConfigurationFile(List<String> arguments) throws Exception {

        File f = null;

        for(Iterator<String> i = arguments.iterator(); i.hasNext(); ) {

            String crt = i.next();

            if ("-c".equals(crt)) {

                if (!i.hasNext()) {
                    throw new UserErrorException("a configuration file name must follow -c");
                }

                i.remove();

                String file = i.next();

                i.remove();

                f = new File(file);

                break;
            }
        }

        if (f == null) {

            //
            // no configuration file specified
            //

            EnvironmentVariableProvider evp = EnvironmentVariableProvider.getInstance();
            String defaultConfigurationFile = evp.getenv(CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME);

            if (defaultConfigurationFile == null) {
                throw new UserErrorException("no configuration file specified on command line with -c and no " +
                        Main.CONFIGURATION_FILE_ENVIRONMENT_VARIABLE_NAME + " environment variable defined");
            }

            f = new File(defaultConfigurationFile);
        }

        if (!f.isFile()) {

            throw new UserErrorException("the configuration file " + f.getAbsolutePath() + " does not exist");
        }

        if (!f.canRead()) {

            throw new UserErrorException("the configuration file " + f.getAbsolutePath() + " cannot be read");
        }

        log.debug("configuration file " + f.getAbsolutePath());

        return f;
    }

    /**
     * Extracts the command line background execution configuration (--background|--foreground). If not specified,
     * the default is assumed to be background (the method returns true)
     *
     * @exception UserErrorException on inconsistent configuration.
     */
    static boolean extractBackgroundSetting(List<String> arguments) throws UserErrorException {

        Boolean background = null;

        for(Iterator<String> i = arguments.iterator(); i.hasNext(); ) {

            String crt = i.next();

            if (crt.startsWith("--background") || crt.startsWith("--foreground")) {

                i.remove();

                boolean b = extractBoolean(crt.substring(0, 12), crt);

                if (!crt.startsWith("--background")) {

                    b = !b;
                }

                if (background != null && background != b) {
                    throw new UserErrorException("conflicting background configuration settings");
                }

                background = b;
            }
        }

        if (background == null) {

            //
            // default if nothing was specified is to run in background
            //
            return true;
        }

        return background;
    }

    static boolean extractBoolean(String optionLiteral, String s) throws UserErrorException {

        if (!s.startsWith(optionLiteral)) {
            throw new IllegalArgumentException(s + " does not start with " + optionLiteral);
        }

        s = s.substring(optionLiteral.length());

        if (s.length() == 0) {
            return true;
        }

        if (!s.startsWith("=")) {
            throw new UserErrorException(optionLiteral + " missing =");
        }

        s = s.substring(1);

        if ("true".equals(s.toLowerCase())) {
            return true;
        }

        if ("false".equals(s.toLowerCase())) {
            return false;
        }

        throw new UserErrorException("invalid " + optionLiteral + " value: \"" + s + "\"");
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
