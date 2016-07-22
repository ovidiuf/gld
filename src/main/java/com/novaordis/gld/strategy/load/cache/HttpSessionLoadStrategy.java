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
import com.novaordis.gld.strategy.load.cache.http.operations.Create;
import com.novaordis.gld.strategy.load.cache.http.operations.HttpSessionOperation;
import com.novaordis.gld.strategy.load.cache.http.operations.Invalidate;

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

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    /**
     * @see com.novaordis.gld.LoadStrategy#configure(Configuration, List, int)
     */
    @Override
    public void configure(Configuration conf, List<String> arguments, int from) throws Exception
    {
        super.configure(conf, arguments, from);

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
     * @see com.novaordis.gld.LoadStrategy#next(Operation, String)
     */
    public Operation next(Operation lastOperation, String lastWrittenKey) {

        HttpSessionSimulation s = HttpSessionSimulation.getCurrentInstance();

        if (s == null) {

            s = HttpSessionSimulation.initializeInstance();
        }

        HttpSessionOperation o = s.next();

        if (o instanceof Invalidate) {

            HttpSessionSimulation.destroyInstance();
        }

        return o;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        Set<Class<? extends Operation>> operations = new HashSet<>();
        operations.add(Create.class);
        operations.add(Invalidate.class);
        return operations;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
