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
import com.novaordis.gld.KeyStore;
import com.novaordis.gld.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.operations.jms.Receive;
import com.novaordis.gld.operations.jms.Send;
import com.novaordis.gld.strategy.load.LoadStrategyBase;

import java.util.List;

public class DefaultJmsLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long remainingOperations;
    private Destination destination;
    private boolean send = true;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return "default";
    }

    /**
     * @see com.novaordis.gld.LoadStrategy#configure(com.novaordis.gld.Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);

        processArguments(arguments, from);

        this.remainingOperations = configuration.getMaxOperations();
    }

    /**
     * @see com.novaordis.gld.LoadStrategy#next(com.novaordis.gld.Operation, String)
     */
    public Operation next(Operation lastOperation, String lastWrittenKey)
    {
        if (getConfiguration() == null)
        {
            throw new IllegalStateException(this + " not configured");
        }

        synchronized (this)
        {
            if (remainingOperations == 0)
            {
                return null;
            }


            remainingOperations --;
        }

        if (send)
        {
            return new Send(destination);
        }
        else
        {
            return new Receive(destination);
        }
    }

    @Override
    public KeyStore getKeyStore()
    {
        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Destination getDestination()
    {
        return destination;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void processArguments(List<String> arguments, int from) throws Exception
    {
        String queue = null;
        String topic = null;

        for (int i = from; i < arguments.size(); i++)
        {
            String crt = arguments.get(i);

            if ("--queue".equals(crt))
            {
                if (i == arguments.size() - 1)
                {
                    throw new UserErrorException("a queue name must follow the '--queue' option");
                }

                arguments.remove(i);
                queue = arguments.remove(i);
                i --;
            }
            else if ("--topic".equals(crt))
            {
                if (i == arguments.size() - 1)
                {
                    throw new UserErrorException("a topic name must follow the '--topic' option");
                }

                arguments.remove(i);
                topic = arguments.remove(i);
                i --;
            }
            else if ("--tmp-receive".equals(crt))
            {
                arguments.remove(i);
                i --;
                send = false;
            }
        }

        if (queue == null && topic == null)
        {
            throw new UserErrorException("a queue or a topic must be specified; use --queue <queue-name>|--topic <topic-name>");
        }

        if (queue != null && topic != null)
        {
            throw new UserErrorException("specify either a queue or a topic, not both");
        }

        if (queue != null)
        {
            destination = new Queue(queue);
        }
        else
        {
            destination = new Topic(topic);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
