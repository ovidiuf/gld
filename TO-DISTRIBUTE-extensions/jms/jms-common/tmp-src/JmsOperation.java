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

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import com.novaordis.gld.service.jms.JmsEndpoint;
import com.novaordis.gld.strategy.load.jms.Destination;
import com.novaordis.gld.strategy.load.jms.JmsLoadStrategy;


public abstract class JmsOperation implements Operation
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private JmsLoadStrategy jmsLoadStrategy;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected JmsOperation(JmsLoadStrategy jmsLoadStrategy)
    {
        this.jmsLoadStrategy = jmsLoadStrategy;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    /**
     * @see Operation#perform(Service)
     */
    @Override
    public void perform(Service s) throws Exception
    {
        s.perform(this);
    }

    @Override
    public JmsLoadStrategy getLoadStrategy()
    {
        return jmsLoadStrategy;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Externalize the job of choosing what endpoint to use for operation to JmsResourceManager.
     *
     * @see com.novaordis.gld.service.jms.JmsEndpoint
     * @see com.novaordis.gld.service.jms.JmsResourceManager
     */
    public abstract void perform(JmsEndpoint endpoint) throws Exception;

    public Destination getDestination()
    {
        return jmsLoadStrategy.getDestination();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

//    protected javax.jms.Destination getJmsDestination(Session session) throws Exception
//    {
//        Destination destination = getDestination();
//
//        if (destination instanceof Queue)
//        {
//            return session.createQueue(destination.getName());
//        }
//        else if (destination instanceof Topic)
//        {
//            return session.createTopic(destination.getName());
//        }
//        else
//        {
//            throw new IllegalStateException("unknown destination type " + destination);
//        }
//    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
