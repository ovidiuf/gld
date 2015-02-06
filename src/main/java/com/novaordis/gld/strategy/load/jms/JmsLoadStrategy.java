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
import com.novaordis.gld.service.jms.EndpointPolicy;
import com.novaordis.gld.strategy.load.LoadStrategyBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class JmsLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean reuseSession;
    private Destination destination;

    private AtomicLong remainingOperations;

    private EndpointPolicy endpointPolicy;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected JmsLoadStrategy()
    {
        this.reuseSession = false;
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
    public Operation next(Operation last, String lastWrittenKey) throws Exception
    {
        checkConfigured();

        if (!decrementAndCheckIfRemaining())
        {
             return null;
        }

        return createInstance();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return whether to reuse the session for successive operations. The default behavior is to create a session
     *         instance and then a producer/consumer instance per each message to be sent/received, and then tear
     *         those instances down.
     *
     *         Setting "reuseSession" to true instructs gld to keep the session and reuse it (on the same thread,
     *         in compliance with JMS specifications).
     */
    public boolean isReuseSession()
    {
        return reuseSession;
    }

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
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processContextRelevantArguments(List<String> arguments, int from) throws UserErrorException
    {
        String queueName = Util.extractString("--queue", arguments, from);
        String topicName = Util.extractString("--topic", arguments, from);

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

        Long mo = getConfiguration().getMaxOperations();

        if (mo != null)
        {
            remainingOperations = new AtomicLong(mo);
        }

        endpointPolicy = EndpointPolicy.NEW_SESSION_AND_ENDPOINT_PER_OPERATION;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
