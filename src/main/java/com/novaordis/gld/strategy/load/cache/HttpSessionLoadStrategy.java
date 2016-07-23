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
import com.novaordis.gld.strategy.load.LoadStrategyBase;
import com.novaordis.gld.strategy.load.cache.http.HttpSessionSimulation;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionCreate;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
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

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int writeCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpSessionLoadStrategy() {

        this.writeCount = HttpSessionSimulation.DEFAULT_WRITE_COUNT;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(Configuration conf, List<String> arguments, int from) throws Exception
    {
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
        }

//        int keySize = conf.getKeySize();
//        int valueSize = conf.getValueSize();
//        boolean useDifferentValues = conf.isUseDifferentValues();
//        Load load = (Load)getConfiguration().getCommand();
//        Long maxOperations = null;
//        if (load != null)
//        {
//            maxOperations = load.getMaxOperations();
//        }
//        String keyStoreFile = conf.getKeyStoreFile();
//        KeyStore keyStore;
//        if (keyStoreFile == null)
//        {
//            keyStore = new RandomKeyGenerator(keySize, maxOperations);
//        }
//        else
//        {
//            keyStore = new ReadOnlyFileKeyStore(keyStoreFile);
//            keyStore.start();
//        }
//        setKeyStore(keyStore);
    }

    /**
     * @see com.novaordis.gld.LoadStrategy#next(Operation, String, boolean)
     */
    public Operation next(Operation lastOperation, String lastWrittenKey, boolean runtimeShuttingDown) {

        HttpSessionSimulation s = HttpSessionSimulation.getCurrentInstance();

        if (s == null) {

            if (runtimeShuttingDown) {

                //
                // we're shutting down anyway, no point in sending anything
                //
                return null;
            }

            s = HttpSessionSimulation.initializeInstance();

            //
            // configure it
            //

            s.setWriteCount(getWriteCount());
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

            HttpSessionSimulation.destroyInstance();
        }

        return nextOperation;
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}