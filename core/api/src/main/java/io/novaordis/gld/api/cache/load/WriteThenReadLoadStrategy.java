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

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.gld.api.cache.CacheServiceConfiguration;
import io.novaordis.gld.api.cache.operation.Read;
import io.novaordis.gld.api.cache.operation.Write;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.provider.RandomKeyProvider;
import io.novaordis.utilities.UserErrorException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A load strategy that attempts to write a key (either randomly generated or read from a embedded key store) then
 * read it back from the cache. The strategy generates writes and reads in series: one write followed by a constant
 * number of reads (or vice-versa), depending on the configured read-to-write or write-to-read ratio.
 *
 * This load strategy is designed to be operated on a single thread, without any interaction and synchronization with
 * other load strategy instances that may be operating on other threads. An instance of such a load strategy may be
 * accessed by a thread and one thread only; if this constraint is not observed, the instance may throw
 * ConcurrentModificationException.
 *
 * Configuration documentation:
 *
 * @{linktourl https://kb.novaordis.com/index.php/Gld_Configuration#read-then-write-on-miss_Load_Strategy_Configuration}
 */
public class WriteThenReadLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final byte READ = 1;
    private static final byte WRITE = 2;

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static {

        Set<Class<? extends Operation>> s = new HashSet<>();
        s.add(Read.class);
        s.add(Write.class);
        OPERATION_TYPES = Collections.unmodifiableSet(s);
    }

    public static final String NAME = "write-then-read";

    public static final String READ_TO_WRITE_LABEL = "read-to-write";
    public static final String WRITE_TO_READ_LABEL = "write-to-read";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean read;
    private int seriesSize;
    private int indexInSeries;
    private volatile boolean initialized;
    private ReadWriteRatio readWriteRatio;

    // Constructors ----------------------------------------------------------------------------------------------------

    public WriteThenReadLoadStrategy() {

        this.indexInSeries = -1;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return NAME;
    }

    @Override
    public ServiceType getServiceType() {

        return ServiceType.cache;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        return OPERATION_TYPES;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public ReadWriteRatio getReadWriteRatio() {

        return readWriteRatio;
    }

    @Override
    public String toString() {

        return getName() + " (" + readWriteRatio + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected void init(ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc)
            throws Exception {

        Integer rtw = null;
        Integer wtr = null;

        Object o = loadStrategyRawConfig.remove(READ_TO_WRITE_LABEL);

        if (o != null) {

            if (!(o instanceof Integer)) {
                throw new UserErrorException(
                        "illegal '" +  READ_TO_WRITE_LABEL + "' " + o.getClass().getSimpleName() + " value");
            }

            rtw = (Integer)o;
        }

        o = loadStrategyRawConfig.remove(WRITE_TO_READ_LABEL);

        if (o != null) {

            if (!(o instanceof Integer)) {
                throw new UserErrorException(
                        "illegal '" +  WRITE_TO_READ_LABEL + "' " + o.getClass().getSimpleName() + " value");
            }

            wtr = (Integer)o;
        }

        //
        // read to write/write to read ratio
        //

        readWriteRatio = new ReadWriteRatio(rtw, wtr);

        if (!(sc instanceof CacheServiceConfiguration)) {

            throw new IllegalArgumentException(sc + " not a CacheServiceConfiguration");
        }

        CacheServiceConfiguration cc = (CacheServiceConfiguration)sc;

        //
        // create and configure the key provider
        //

        RandomKeyProvider keyProvider = new RandomKeyProvider();

        int keySize = cc.getKeySize();
        keyProvider.setKeySize(keySize);

        Long keyCount = lc.getOperations();
        keyProvider.setKeyCount(keyCount);

        //
        // install the provider ...
        //
        setKeyProvider(keyProvider);

        //
        // ... and start it
        //

        keyProvider.start();

        this.read = readWriteRatio.isRead();
        this.seriesSize = readWriteRatio.getFollowUpSeriesSize();
        this.indexInSeries = 0;
        this.initialized = true;
    }

    @Override
    protected Operation nextInternal(Operation last, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception {

        if (!initialized) {

            throw new IllegalStateException(this + " was not initialized");
        }

        if (indexInSeries == -1) {
            throw new IllegalStateException(this + " not properly configured");
        }

        // we ignore the last operation, the result come in pre-determined series

        byte keyType;

        if (indexInSeries == 0) {
            keyType = read ? READ : WRITE;
        }
        else {
            keyType = read ?  WRITE : READ;
        }

        String key;
        Operation o;
        KeyProvider keyProvider = getKeyProvider();

        if (keyType == READ) {

            key = keyProvider.next();

            if (key == null) {

                return null;
            }

            //
            // TODO refactor below
            //

//            else if (lastWrittenKey != null) {
//
//                // last successfully written key - the method should be prepared for the situation the key is null.
//                // Use this unless we have been passed a ReadOnly key keystore. A ReadOnly key keystore takes precedence.
//
//                key = lastWrittenKey;
//            }

            o = new Read(key);
        }
        else //noinspection ConstantConditions
            if (keyType == WRITE) {

                key = keyProvider.next();

                if (key == null) {

                    return null;
                }

                String value = computeValue();

                //
                // TODO refactor below
                //
//                if (keyProvider instanceof ExperimentalKeyStore) {
//                    key = keyProvider.get();
//
//                    if (key == null) {
//                        return null;
//                    }
//
//                    value = ((ExperimentalKeyStore) keyProvider).getValue(key);
//                }
//                else {
//
//                    key = Util.getRandomKey(ThreadLocalRandom.current(), keySize);
//                    value = Util.getRandomValue(ThreadLocalRandom.current(), valueSize, useDifferentValues);
//                }

                o = new Write(key, value);
            }
            else {

                throw new IllegalArgumentException("unknown key type " +keyType);
            }

        indexInSeries = (indexInSeries + 1) % (seriesSize + 1);

        return o;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
