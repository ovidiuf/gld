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

package com.novaordis.gld.command;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.UserErrorException;

import java.util.List;

/**
 * Use this command to stop gld running in "background" mode, where it reads a scenario from configuration, and starts
 * sending load into target, writing statistics into local files until it is explicitly stopped with the Stop command
 * or it runs out of load.
 *
 * @see Start
 * @see Status
 */
public class Stop extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public Stop(Configuration c) throws UserErrorException
    {
        super(c);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean isInitialized()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void execute() throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "Stop[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
