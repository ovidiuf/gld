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

package com.novaordis.gld.strategy.load.cache.http;

import com.novaordis.gld.strategy.load.cache.HttpSessionLoadStrategy;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;

/**
 * A HttpSessionSimulation instance that is intended to be associated with a thread in an one-to-one relationship.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/23/16
 */
public class HttpSessionPerThread extends HttpSessionSimulation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static final ThreadLocal<HttpSessionPerThread> threadLocal = new ThreadLocal<>();

    /**
     * @return the HttpSessionSimulation instance associated with this thread or null if there isn't any.
     */
    public static HttpSessionPerThread getCurrentInstance() {

        return threadLocal.get();
    }

    /**
     * Create a new instance and associate it with the thread.
     *
     * @exception IllegalStateException if the instance is already initialized.
     */
    public static HttpSessionPerThread initializeInstance() {

        if (threadLocal.get() != null) {
            throw new IllegalStateException("instance already associated with the current thread");
        }

        //
        // just create the instance and associate it with the thread, it will be configured by the caller
        //
        HttpSessionPerThread instance = new HttpSessionPerThread();
        threadLocal.set(instance);
        return instance;
    }

    /**
     * Noop if no instance is associated with the thread.
     */
    public static HttpSessionPerThread destroyInstance() {

        HttpSessionPerThread i = threadLocal.get();
        threadLocal.remove();
        return i;
    }

    public static HttpSessionOperation next(HttpSessionLoadStrategy strategy, boolean runtimeShuttingDown) {

        HttpSessionPerThread s = getCurrentInstance();

        if (s == null) {

            if (runtimeShuttingDown) {

                //
                // we're shutting down anyway, no point in sending anything
                //
                return null;
            }

            s = initializeInstance();

            //
            // configure it
            //

            s.setWriteCount(strategy.getWriteCount());
        }


        HttpSessionOperation nextOperation;


        if (runtimeShuttingDown) {

            //
            // we're shutting down, cleanup, invalidate the session in the cache
            //

            nextOperation = new HttpSessionInvalidate(s);
        }
        else {

            nextOperation = s.next();
        }

        if (nextOperation instanceof HttpSessionInvalidate) {

            destroyInstance();
        }

        return nextOperation;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
