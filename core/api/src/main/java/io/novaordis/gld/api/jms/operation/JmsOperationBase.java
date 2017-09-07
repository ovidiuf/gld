/*
 * Copyright (c) 2017 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.jms.operation;

import io.novaordis.gld.api.jms.Destination;
import io.novaordis.gld.api.jms.JMSEndpoint;
import io.novaordis.gld.api.jms.JMSService;
import io.novaordis.gld.api.jms.load.JMSLoadStrategy;
import io.novaordis.gld.api.service.Service;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/17
 */
public abstract class JmsOperationBase implements JmsOperation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private JMSLoadStrategy loadStrategy;

    private String id;
    private String payload;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected JmsOperationBase(JMSLoadStrategy loadStrategy) {

        if (loadStrategy == null) {

            throw new IllegalArgumentException("null load strategy");
        }

        this.loadStrategy = loadStrategy;
    }

    // JmsOperation implementation -------------------------------------------------------------------------------------

    @Override
    public JMSLoadStrategy getLoadStrategy() {

        return loadStrategy;
    }

    @Override
    public String getKey() {

        return getId();
    }

    @Override
    public String getPayload() {

        return payload;
    }

    @Override
    public Destination getDestination() {

        return loadStrategy.getDestination();
    }

    @Override
    public String getId() {

        return id;
    }

    @Override
    public void perform(Service s) throws Exception {

        if (!(s instanceof JMSService)) {

            throw new IllegalArgumentException("invalid service " + s);

        }

        //
        // pull the appropriate resources from the service and then execute the operations with these resources
        //

        JMSService jmsService = (JMSService)s;

        JMSEndpoint endpoint = jmsService.checkOut(this);

        try {

            perform(endpoint);

        }
        finally {

            //
            // handle the session according to the policy
            //

            jmsService.checkIn(endpoint);

        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Has the same semantics as "key".
     */
    public void setId(String id) {

        this.id = id;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setPayload(String payload) {

        this.payload = payload;
    }

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
