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

package com.novaordis.gld.operations.jms;

import com.novaordis.gld.Operation;
import com.novaordis.gld.Service;
import com.novaordis.gld.strategy.load.jms.Destination;
import com.novaordis.gld.strategy.load.jms.Queue;
import com.novaordis.gld.strategy.load.jms.Topic;


import javax.jms.Session;

public abstract class JmsOperation implements Operation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Destination destination;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected JmsOperation(Destination destination)
    {
        this.destination = destination;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.Operation#perform(com.novaordis.gld.Service)
     */
    @Override
    public void perform(Service s) throws Exception
    {
        s.perform(this);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public abstract void perform(Session jmsSession) throws Exception;

    public Destination getDestination()
    {
        return destination;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected javax.jms.Destination getJmsDestination(Session session) throws Exception
    {
        if (destination instanceof Queue)
        {
            return session.createQueue(destination.getName());
        }
        else if (destination instanceof Topic)
        {
            return session.createTopic(destination.getName());
        }
        else
        {
            throw new IllegalStateException("unknown destination type " + destination);
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
