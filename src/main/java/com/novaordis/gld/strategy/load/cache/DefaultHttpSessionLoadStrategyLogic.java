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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The instance that implements the default HTTP session load strategy (mode).
 *
 * See HELP.txt --http-session-mode
 *
 * The implementation must be thread safe, as it will be used in a concurrent environment.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/23/16
 */
public class DefaultHttpSessionLoadStrategyLogic {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int writesPerSession;

    private final HttpSessionSimulation[] sessions;

    private int last;

    // in bytes - null means don't chage
    private Integer initialSessionSize;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param initialSessionSize in bytes. If null, use the HttpSessionSimulation built-in default.
     *
     * @exception IllegalArgumentException on invalid session count.
     */
    public DefaultHttpSessionLoadStrategyLogic(int sessionCount, int writesPerSession, Integer initialSessionSize) {

        if (sessionCount <= 0) {
            throw new IllegalArgumentException("invalid session count " + sessionCount);
        }

        if (writesPerSession <= 0) {
            throw new IllegalArgumentException("invalid writes per session count " + writesPerSession);
        }

        // null initialSessionSize is legal, we'll use the HttpSessionSimulation built-in default

        this.writesPerSession = writesPerSession;
        this.initialSessionSize = initialSessionSize;
        sessions = new HttpSessionSimulation[sessionCount];

        last = 0;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getConfiguredSessionCount() {

        return sessions.length;
    }

    public int getActiveSessionCount() {

        synchronized (sessions) {

            return last;
        }
    }

    public int getWritesPerSession() {

        return writesPerSession;
    }

    /**
     * @return null means use HttpSessionSimulation's default.
     */
    public Integer getInitialSessionSize() {
        return initialSessionSize;
    }

    public HttpSessionOperation next(boolean runtimeShuttingDown) {

        HttpSessionSimulation session;

        HttpSessionOperation nextOperation;

        synchronized (sessions) {

            if (runtimeShuttingDown) {

                //
                // issue only invalidation operations
                //

                if (last == 0) {

                    //
                    // no more sessions
                    //
                    return null;
                }
                else {

                    //
                    // pop and invalidate the last one
                    //

                    session = sessions[--last];
                    nextOperation = new HttpSessionInvalidate(session);
                }
            }
            else {

                //
                // the normal lifecycle: create, write for a set number of time, invalidate
                //

                int i;

                if (last < sessions.length) {

                    session = new HttpSessionSimulation();
                    session.setWriteCount(writesPerSession);
                    session.setInitialSessionSize(initialSessionSize);
                    i = last++;
                    sessions[i] = session;
                }
                else {

                    //
                    // all sessions are created, pick a random one and either write it or invalidate it
                    //

                    Random random = ThreadLocalRandom.current();
                    i = random.nextInt(last);
                    session = sessions[i];
                }

                nextOperation = session.next();

                if (nextOperation instanceof HttpSessionInvalidate) {

                    //
                    // remove the session we are going to invalidate from the array
                    //

                    sessions[i] = sessions[--last];
                }
            }
        }

        return nextOperation;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
