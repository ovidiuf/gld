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

package com.novaordis.cld.mock;

import com.novaordis.ac.Collector;
import com.novaordis.ac.Handler;
import org.apache.log4j.Logger;

/**
 * A mock collector that does NOT collect asynchronously. For testing only.
 */
public class MockCollector implements Collector
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockCollector.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Handler handler;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockCollector()
    {
        this(null);
    }

    public MockCollector(Handler h)
    {
        this.handler = h;
    }

    // Collector implementation ----------------------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getThreadName()
    {
        return null;
    }

    @Override
    public boolean handOver(Object o)
    {
        if (handler == null)
        {
            throw new IllegalStateException("at least a handler must be registered");
        }

        handler.handle(System.currentTimeMillis(), Thread.currentThread().getName(), o);

        return true;
    }

    @Override
    public boolean registerHandler(Handler h)
    {
        if (handler != null)
        {
            throw new IllegalStateException("a handler is already registered");
        }

        this.handler = h;

        return true;
    }

    @Override
    public boolean unregisterHandler(Handler h)
    {
        return false;
    }

    @Override
    public void dispose()
    {

    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
