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

package com.novaordis.gld;

import java.util.List;

public interface Service
{
    void setConfiguration(Configuration c);

    void setTarget(List<Node> nodes);

    ContentType getContentType();

    /**
     * Starting an already started service should throw IllegalStateException.
     *
     * @throws IllegalStateException on attempt to start an already started service instance.
     */
    void start() throws Exception;

    /**
     * Stopping an already stopped service instance should be a noop.
     */
    void stop() throws Exception;

    boolean isStarted();

    void perform(Operation o) throws Exception;
}
