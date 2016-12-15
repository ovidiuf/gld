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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.todiscard.Command;
import io.novaordis.utilities.UserErrorException;

@Deprecated
public class Main {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        try {

            // ConfigurationImpl c = new ConfigurationImpl(args);

            // Command command = c.getCommand();

            Command command = null;

            command.execute();

            System.exit(0);
        }
        catch(UserErrorException e) {

            System.out.println("[error]: " + e.getMessage());

            System.exit(1);
        }
        catch(Throwable t) {

            System.out.println("");
            System.out.println("unexpected failure, send an error report to ovidiu@novaordis.com");
            System.out.println("");
            t.printStackTrace();

            System.exit(2);
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
