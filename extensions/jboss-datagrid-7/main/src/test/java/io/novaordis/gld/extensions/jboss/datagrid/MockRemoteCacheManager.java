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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.utilities.NotYetImplementedException;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class MockRemoteCacheManager extends RemoteCacheManager {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;
    private RuntimeException getCacheFailureCause;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockRemoteCacheManager() {

        this.started = false;
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public RemoteCache getCache() {

        if (getCacheFailureCause != null) {

            throw getCacheFailureCause;
        }

        return new MockRemoteCache(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoteCache getCache(String cacheName) {

        if (getCacheFailureCause != null) {

            throw getCacheFailureCause;
        }

        return new MockRemoteCache(this);
    }

    @Override
    public void start() {

        this.started = true;
    }

    @Override
    public void stop() {

        this.started = false;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void makeFail(String methodName, RuntimeException cause) {

        if ("getCache".equals(methodName)) {

            getCacheFailureCause = cause;
        }
        else {

            throw new NotYetImplementedException("we don't know how to make fail " + methodName + "(...)");
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
