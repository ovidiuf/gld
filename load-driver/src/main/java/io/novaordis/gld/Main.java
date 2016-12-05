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

import io.novaordis.gld.api.Configuration;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.gld.api.configuration.YamlBasedConfiguration;
import io.novaordis.gld.driver.LoadDriverImpl;
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

        LoadDriver ld = new LoadDriverImpl();

        try {

            List<String> arguments = new ArrayList<>(Arrays.asList(args));
            File configurationFile = extractConfigurationFile(arguments);
            Configuration c = new YamlBasedConfiguration(configurationFile);

            ld.init(c);

            log.debug(ld + " initialized");

            ld.run();
        }
        catch(Throwable t) {

            log.debug("load driver failure: " + t.getMessage(), t);
            ld.error(t);
        }
    }

    // Package protected static ----------------------------------------------------------------------------------------

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

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
