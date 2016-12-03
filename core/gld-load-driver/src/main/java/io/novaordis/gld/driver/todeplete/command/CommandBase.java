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
import io.novaordis.utilities.UserErrorException;

import java.util.ArrayList;
import java.util.List;

abstract class CommandBase implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration conf;
    private List<String> arguments;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected CommandBase(Configuration conf)
    {
        this.conf = conf;

        // establish bidirectional relationship

        throw new RuntimeException("NOT YET IMPLEMENTED: bidirectional relationship not implemented");
//        conf.setCommand(this);
//
//        this.arguments = new ArrayList<>();
    }

    // Command implementation ------------------------------------------------------------------------------------------

    /**
     * Some commands do not need initialization, so make this a noop.
     */
    @Override
    public void initialize() throws Exception
    {
        // noop
    }

    @Override
    public void addArgument(String s)
    {
        arguments.add(s);
    }

    @Override
    public boolean isRemote()
    {
        return true;
    }

    @Override
    public void execute() throws Exception
    {
        insureInitialized();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Configuration getConfiguration() {

        return conf;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected List<String> getArguments()
    {
        return arguments;
    }

    /**
     * @exception IllegalStateException if the command is not initialized.
     */
    protected void insureInitialized()
    {
        if (!isInitialized())
        {
            throw new IllegalStateException(this + " was not initialized, thus it cannot be executed");
        }
    }

    protected void failOnUnknownArguments() throws UserErrorException
    {
        if (!arguments.isEmpty())
        {
            throw new UserErrorException("unknown argument: \"" + arguments.get(0) + "\"");
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
