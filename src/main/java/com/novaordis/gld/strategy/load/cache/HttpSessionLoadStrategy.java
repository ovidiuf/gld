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

import com.novaordis.gld.Configuration;
import com.novaordis.gld.Operation;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.strategy.load.LoadStrategyBase;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionPerThread;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionInvalidate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionWrite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A load strategy that simulates interaction between a clustered session manager and a cache.
 *
 * It is supposed to be thread-safe, the intention is to be accessed concurrently from different threads.
 *
 */
public class HttpSessionLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    static final String DEFAULT_MODE_LITERAL = "default";
    static final String SESSION_PER_THREAD_MODE_LITERAL = "session-per-thread";

    // See HELP.txt --http-session-mode
    static final byte DEFAULT_MODE = 0;
    static final byte SESSION_PER_THREAD_MODE = 1;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private byte mode;
    private int writeCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionLoadStrategy() {

        this.writeCount = HttpSessionSimulation.DEFAULT_WRITE_COUNT;
        setMode(DEFAULT_MODE);
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(Configuration conf, List<String> arguments, int from) throws Exception {

        super.configure(conf, arguments, from);

        if (arguments == null) {
            return;
        }

        for(int i = from; i < arguments.size(); i ++) {

            String crt = arguments.get(i);

            if ("--write-count".equals(crt)) {

                arguments.remove(i);
                writeCount = Integer.parseInt(arguments.remove(i));
            }
            else if ("--http-session-mode".equals(crt)) {

                arguments.remove(i);
                crt = arguments.remove(i);

                if (DEFAULT_MODE_LITERAL.equals(crt)) {
                    setMode(DEFAULT_MODE);
                }
                else if (SESSION_PER_THREAD_MODE_LITERAL.equals(crt)) {
                    setMode(SESSION_PER_THREAD_MODE);
                }
                else {
                    throw new UserErrorException(
                            "unknown --http-session-mode value \"" + crt + "\", use one of the following: " +
                                    "\"" + DEFAULT_MODE_LITERAL + "\", \"" + SESSION_PER_THREAD_MODE_LITERAL + "\"");
                }
            }
        }
    }

    public Operation next(Operation lastOperation, String lastWrittenKey, boolean runtimeShuttingDown) {

        if (mode == SESSION_PER_THREAD_MODE) {
            return HttpSessionPerThread.next(this, runtimeShuttingDown);
        }
        else if (mode == DEFAULT_MODE) {
            throw new RuntimeException("NOT YET IMPLEMENTED");
        }

        throw new IllegalStateException("unknown HttpSessionLoadStrategy mode " + mode);
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        Set<Class<? extends Operation>> operations = new HashSet<>();
        operations.add(HttpSessionCreate.class);
        operations.add(HttpSessionWrite.class);
        operations.add(HttpSessionInvalidate.class);
        return operations;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getWriteCount() {

        return writeCount;
    }

    public void setWriteCount(int i) {
        this.writeCount = i;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    byte getMode() {
        return mode;
    }

    void setMode(byte m) {
        this.mode = m;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
