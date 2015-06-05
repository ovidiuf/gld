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

package com.novaordis.gld;

import org.apache.log4j.Logger;

import java.util.List;

public class MockService implements Service
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockService.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    private boolean wasStarted;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public void setConfiguration(Configuration c)
    {
        throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes)
    {
        throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException
    {
        throw new RuntimeException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public ContentType getContentType()
    {
        throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
    }

    @Override
    public void start() throws Exception
    {
        started = true;
        wasStarted = true;
        log.info(this + " started");
    }

    @Override
    public void stop() throws Exception
    {
        started = false;
        log.info(this + " stopped");
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    @Override
    public void perform(Operation o) throws Exception
    {
        throw new RuntimeException("perform() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if start() method was called at least once
     */
    public boolean wasStarted()
    {
        return wasStarted;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
