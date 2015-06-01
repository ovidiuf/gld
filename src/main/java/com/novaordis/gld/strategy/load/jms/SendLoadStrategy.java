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

import com.novaordis.gld.Operation;
import com.novaordis.gld.operations.jms.Send;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SendLoadStrategy extends JmsLoadStrategy
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static
    {
        OPERATION_TYPES = new HashSet<>();
        OPERATION_TYPES.add(Send.class);
    }

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        long remainingOperations = getRemainingOperations();
        return "SendLoadStrategy[remaining=" +
            (remainingOperations == Long.MAX_VALUE ? "unlimited" : remainingOperations ) + "]";
    }

    // JmsLoadStrategy overrides ---------------------------------------------------------------------------------------

    @Override
    protected Send createInstance()
    {
        return new Send(this);
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        return OPERATION_TYPES;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
