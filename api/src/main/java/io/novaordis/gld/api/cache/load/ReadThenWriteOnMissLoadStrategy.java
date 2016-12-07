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

package io.novaordis.gld.api.cache.load;

import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.ServiceConfiguration;
import io.novaordis.gld.api.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Read;
import io.novaordis.gld.api.cache.operation.Write;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A load strategy that implements the following behavior:
 *
 * It first attempts a read.
 *
 * If it's a hit, it keeps reading.
 *
 * If it's a miss, it writes the key and a random value. It gets the keys from the configured key store or it randomly
 * generates them if there is not key store.
 *
 * It is supposed to be thread-safe, the intention is to be accessed concurrently from different threads. This implies
 * that the KeyStore implementation is also thread-safe.
 */
public class ReadThenWriteOnMissLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static {

        Set<Class<? extends Operation>> s = new HashSet<>();
        s.add(Read.class);
        s.add(Write.class);
        OPERATION_TYPES = Collections.unmodifiableSet(s);
    }

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String syntheticValue;
    private volatile boolean configured;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ReadThenWriteOnMissLoadStrategy()
    {
        configured = false;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return "read-then-write-on-miss";
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    @Override
    public void init(ServiceConfiguration sc) throws Exception {

        CacheServiceConfiguration csc = (CacheServiceConfiguration)sc;


//        int keySize = conf.getKeySize();
//        int valueSize = conf.getValueSize();
//        boolean useDifferentValues = conf.isUseDifferentValues();
//
//        // TODO this is fishy, refactor both here and in JmsLoadStrategy
//        // Load load = (Load)getConfiguration().getCommand();
//        Object load = null;
//
//        if (load == null) {
//            throw new RuntimeException("RETURN HERE");
//        }

//        Long maxOperations = null;
//
//        if (load != null)
//        {
//            maxOperations = load.getMaxOperations();
//        }
//
//        String keyStoreFile = conf.getKeyStoreFile();
//        KeyStore keyStore;
//
//        if (keyStoreFile == null)
//        {
//            keyStore = new RandomKeyGenerator(keySize, maxOperations);
//        }
//        else
//        {
//            keyStore = new ReadOnlyFileKeyStore(keyStoreFile);
//            keyStore.start();
//        }
//
//        setKeyStore(keyStore);
//
//        syntheticValue = Util.getRandomValue(
//            new Random(System.currentTimeMillis() + 17L), valueSize, useDifferentValues);
//
//        configured = true;
    }

    /**
     * @see io.novaordis.gld.api.LoadStrategy#next(Operation, String, boolean)
     */
    public Operation next(Operation lastOperation, String lastWrittenKey, boolean runtimeShuttingDown)
    {
        if (!configured)
        {
            throw new IllegalStateException(this + " not properly configured");
        }

        Operation result;

        if (lastOperation == null)
        {
            // start with a read
            result = getNextRead();
        }
        else if (lastOperation instanceof Read)
        {
            Read lastRead = (Read)lastOperation;

            // if the last read is a hit, generate another read

            if (lastRead.getValue() != null)
            {
                result = getNextRead();
            }
            else
            {
                result = new Write(lastRead.getKey(), syntheticValue);
            }
        }
        else if (lastOperation instanceof Write)
        {
            // read follows a write
            result = getNextRead();
        }
        else
        {
            throw new IllegalArgumentException("unknown last operation " + lastOperation);
        }

        return result;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        return OPERATION_TYPES;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        return getName();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @return a non-null next Read if the key store has not run out of read operations, or null if it has.
     */
    private Read getNextRead() {

        String key =  getKeyStore().get();

        if (key == null) {
            // the key store ran out of keys, so we ran out of operations, return null

            return null;
        }

        return new Read(key);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
