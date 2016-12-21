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

package io.novaordis.gld.command;

import io.novaordis.utilities.UserErrorException;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/21/16
 */
public interface Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Attempt to identify on the command line a command that does not require configuration file parsing or a driver
     * instance.
     *
     * Return null if no command is identified.
     */
    static Command toCommand(List<String> commandLineArguments) {

        for(String s: commandLineArguments) {

            if (Version.LITERAL.equals(s)) {

                return new Version();
            }
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Must not throw exception, but log directly to stderr.
     */
    void execute();

}
