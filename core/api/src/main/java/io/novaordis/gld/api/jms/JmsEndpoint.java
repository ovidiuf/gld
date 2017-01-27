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

package io.novaordis.gld.api.jms;

import javax.jms.Session;

/**
 * This interface was introduced to allow for flexibility in implementing the load strategy. A JMS load strategy can
 * choose to recycle Connections, Sessions or even MessageProducers and MessageConsumers. The JmsEndpoint abstracts
 * out all these things: the load driver does not care how a message is sent by the JmsService implementation, as long
 * as it complies with the load strategy.
 *
 */
public interface JmsEndpoint {

    /**
     * Closes the endpoint and the associated resources, according to the load strategy in effect.
     */
    void close() throws Exception;

    Session getSession();

}
