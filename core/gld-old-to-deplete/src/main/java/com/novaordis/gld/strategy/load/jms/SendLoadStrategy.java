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
import com.novaordis.gld.Util;
import com.novaordis.gld.operations.jms.Send;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

    private int messageSize;
    private String cachedMessagePayload;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy/JmsLoadStrategy overrides --------------------------------------------------------------------------

    /**
     * For the time being we use the global configuration option --value-size as the message size.
     *
     * TODO: this must be refactored, it should be a JMS specific configuration item (like --message-size).
     *
     * @see com.novaordis.gld.LoadStrategy#configure(com.novaordis.gld.Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);
        setMessageSize(configuration.getValueSize());
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        return OPERATION_TYPES;
    }

    @Override
    protected Send createInstance()
    {
        return new Send(this);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setMessageSize(int messageSize)
    {
        this.messageSize = messageSize;
    }

    public int getMessageSize()
    {
        return messageSize;
    }

    /**
     * Allow the strategy to provide a message payload (presumably cached) to speed the operation generation.
     */
    public String getMessagePayload()
    {
        if (cachedMessagePayload == null)
        {
            if (messageSize <= 0)
            {
                cachedMessagePayload = "";
            }
            else
            {
                cachedMessagePayload = Util.getRandomString(new Random(System.currentTimeMillis()), messageSize, 5);
            }
        }

        return cachedMessagePayload;
    }

    @Override
    public String toString()
    {
        long remainingOperations = getRemainingOperations();
        return "SendLoadStrategy[remaining=" +
            (remainingOperations == Long.MAX_VALUE ? "unlimited" : remainingOperations ) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
