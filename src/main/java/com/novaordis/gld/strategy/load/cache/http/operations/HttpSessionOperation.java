/*
 * Copyright (c) 2016 Nova Ordis LLC
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

package com.novaordis.gld.strategy.load.cache.http.operations;

import com.novaordis.gld.Operation;
import com.novaordis.gld.Service;
import com.novaordis.gld.service.cache.infinispan.InfinispanService;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/21/16
 */
public abstract class HttpSessionOperation implements Operation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String sessionId;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected HttpSessionOperation(String sessionId) {
        this.sessionId = sessionId;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    @Override
    public void perform(Service s) throws Exception {

        if (!(s instanceof InfinispanService)) {
            throw new IllegalArgumentException("invalid service type " + s + ", we expect an InfinispanService");
        }

        performInternal((InfinispanService)s);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return sessionId;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract void performInternal(InfinispanService is) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
