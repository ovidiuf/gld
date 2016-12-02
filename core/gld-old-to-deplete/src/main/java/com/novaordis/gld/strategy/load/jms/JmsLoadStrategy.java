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
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.service.jms.EndpointPolicy;
import com.novaordis.gld.strategy.load.LoadStrategyBase;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public abstract class JmsLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    private AtomicLong remainingOperations;

    private EndpointPolicy endpointPolicy;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected JmsLoadStrategy()
    {
        this.endpointPolicy = EndpointPolicy.REUSE_SESSION_NEW_ENDPOINT_PER_OPERATION;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.LoadStrategy#configure(com.novaordis.gld.Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);
        processContextRelevantArguments(arguments, from);
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {

        checkConfigured();

        if (!decrementAndCheckIfRemaining()) {
             return null;
        }

        return createInstance();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Destination getDestination()
    {
        return destination;
    }

    public void setDestination(Destination d)
    {
        this.destination = d;
    }

    public long getRemainingOperations()
    {
        if (remainingOperations == null)
        {
            return Long.MAX_VALUE;
        }

        return remainingOperations.get();
    }

    public EndpointPolicy getEndpointPolicy()
    {
        return endpointPolicy;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void checkConfigured()
    {
        if (destination == null)
        {
            throw new IllegalStateException(this + " not configured");
        }
    }

    /**
     * @return decrements the remaining operations counter and returns true if there are still operations to
     *         process or false if there are no more operations to process.
     */
    protected boolean decrementAndCheckIfRemaining()
    {
        if (remainingOperations == null)
        {
            return true;
        }

        long value = remainingOperations.decrementAndGet();

        return value >= 0;
    }

    protected abstract Operation createInstance();

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse context-relevant command line arguments and removes them from the list. If not finding the arguments
     * we need in the list, try the configuration second. It's not sensitive to null, null is fine and it is ignored.
     */
    private void processContextRelevantArguments(List<String> arguments, int from) throws UserErrorException
    {
        String queueName = Util.extractString("--queue", arguments, from);

        if (queueName == null)
        {
            Properties p = getConfiguration().getConfigurationFileContent();
            queueName = p == null ? null : p.getProperty("queue");
        }

        String topicName = Util.extractString("--topic", arguments, from);

        if (topicName == null)
        {
            Properties p = getConfiguration().getConfigurationFileContent();
            topicName = p == null ? null : p.getProperty("topic");
        }

        if (queueName == null && topicName == null)
        {
            throw new UserErrorException("a destination is required; use --queue|--topic <name>");
        }

        if (queueName != null && topicName != null)
        {
            throw new UserErrorException("both --queue and --topic used; only one must be specified");
        }

        if (queueName != null)
        {
            setDestination(new Queue(queueName));
        }
        else
        {
            setDestination(new Topic(topicName));
        }

        String endpointPolicy = Util.extractString("--endpoint-policy", arguments, from);

        if (endpointPolicy != null)
        {
            try
            {
                this.endpointPolicy = EndpointPolicy.valueOf(endpointPolicy);
            }
            catch(Exception e)
            {
                throw new UserErrorException(
                    "invalid --endpoint-policy value \"" + endpointPolicy + "\"; valid options: " +
                        Arrays.asList(EndpointPolicy.values()), e);
            }
        }

        // TODO this is fishy, refactor both here and in ReadThenWriteOnMissLoadStrategy
        Load load = (Load)getConfiguration().getCommand();

        if (load != null)
        {
            Long maxOperations = load.getMaxOperations();

            if (maxOperations == null)
            {
                // try the configuration file
                // TODO need to refactor this for a consistent command-line/configuration file approach
                Properties p = getConfiguration().getConfigurationFileContent();
                if (p != null)
                {
                    String s = p.getProperty("message-count");
                    if (s != null)
                    {
                        maxOperations = Long.parseLong(s);
                    }
                }
            }

            if (maxOperations != null)
            {
                remainingOperations = new AtomicLong(maxOperations);
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
