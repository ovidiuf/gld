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

package com.novaordis.gld.strategy.load.jms;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;
import com.novaordis.gld.operations.jms.Receive;
import com.novaordis.gld.operations.jms.Send;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReceiveLoadStrategy extends JmsLoadStrategy
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static
    {
        OPERATION_TYPES = new HashSet<>();
        OPERATION_TYPES.add(Receive.class);
    }


    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // null means no timeout
    private Long timeoutMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.LoadStrategy#configure(com.novaordis.gld.Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);

        if (arguments == null)
        {
            return;
        }

        processContextRelevantArguments(arguments, from);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Long getTimeoutMs()
    {
        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs)
    {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String toString()
    {
        long remainingOperations = getRemainingOperations();
        return "ReceiveLoadStrategy[remaining=" +
            (remainingOperations == Long.MAX_VALUE ? "unlimited" : remainingOperations ) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // JmsLoadStrategy overrides ---------------------------------------------------------------------------------------

    @Override
    protected Receive createInstance()
    {
        return new Receive(this);
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        return OPERATION_TYPES;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processContextRelevantArguments(List<String> arguments, int from) throws UserErrorException
    {
        timeoutMs = Util.extractLong("--timeout", arguments, from);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
