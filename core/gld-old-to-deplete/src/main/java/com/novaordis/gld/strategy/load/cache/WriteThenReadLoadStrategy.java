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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.KeyStore;
import io.novaordis.gld.api.Operation;
import com.novaordis.gld.StorageStrategy;
import com.novaordis.gld.UserErrorException;
import com.novaordis.gld.Util;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.keystore.ReadOnlyFileKeyStore;
import com.novaordis.gld.keystore.ExperimentalKeyStore;
import com.novaordis.gld.keystore.WriteOnlyFileKeyStore;
import com.novaordis.gld.operations.cache.Read;
import com.novaordis.gld.operations.cache.Write;
import com.novaordis.gld.strategy.load.LoadStrategyBase;
import com.novaordis.gld.strategy.storage.HierarchicalStorageStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A load strategy that attempts to write a key (either randomly generated or read from a local reference store) then
 * read it back from the cache. The strategy generates writes and reads in series: one write followed by a constant
 * number of reads (or vice-versa), depending on the configured read-to-write or write-to-read ratio.
 *
 * This load strategy is designed to be operated on a single thread, without any interaction and synchronization with
 * other load strategy instances that may be operating on other threads. An instance of such a load strategy may be
 * accessed by a thread and one thread only; if this constraint is not observed, the instance may throw
 * ConcurrentModificationException.
 */
public class WriteThenReadLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final byte READ = 1;
    private static final byte WRITE = 2;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int indexInSeries;
    private boolean read;
    private int seriesSize;
    private int keySize;
    private int valueSize;
    private boolean useDifferentValues;

    private ReadWriteRatio readWriteRatio;

    private AtomicLong remainingOperations;

    // Constructors ----------------------------------------------------------------------------------------------------

    public WriteThenReadLoadStrategy()
    {
        super();
        this.indexInSeries = -1;
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(Configuration conf, List<String> arguments, int from) throws Exception
    {
        super.configure(conf, arguments, from);

        if (arguments == null)
        {
            return;
        }

        processArguments(arguments, from);

        this.keySize = conf.getKeySize();
        this.valueSize = conf.getValueSize();
        this.useDifferentValues = conf.isUseDifferentValues();

        // TODO this is fishy, refactor both here and in JmsLoadStrategy
        Load load = (Load)getConfiguration().getCommand();
        Long maxOperations;
        if (load != null && (maxOperations = load.getMaxOperations()) != null)
        {
            remainingOperations = new AtomicLong(maxOperations);
        }

        this.indexInSeries = 0;

        this.read = readWriteRatio.isRead();
        this.seriesSize = readWriteRatio.getFollowUpSeriesSize();

        String keyStoreFile = conf.getKeyStoreFile();

        KeyStore keyStore = null;

        if (keyStoreFile != null)
        {
            if (readWriteRatio.doesWritingTakePlace())
            {
                // we do writing, so use a WriteOnlyFileKeyStore
                keyStore = new WriteOnlyFileKeyStore(keyStoreFile);
            }
            else
            {
                // we don't do any writes, use a ReadOnlyFileKeyStore
                keyStore = new ReadOnlyFileKeyStore(keyStoreFile);
            }
        }

        //
        // TODO - need to refactor
        //

        StorageStrategy ss = conf.getStorageStrategy();

        if(readWriteRatio.isWrite() && ss instanceof HierarchicalStorageStrategy)
        {
            // create a new KeyStore that wraps around the Hierarchical Storage Strategy

            keyStore = new ExperimentalKeyStore((HierarchicalStorageStrategy)ss);
        }

        //
        //
        //

        setKeyStore(keyStore);
    }

    @Override
    public Operation next(Operation lastOperation, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception  {

        if (indexInSeries == -1) {
            throw new IllegalStateException(this + " not properly configured");
        }

        if (remainingOperations != null && remainingOperations.getAndDecrement() <= 0) {
            // out of operations
            remainingOperations.set(0);
            return null;
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
        KeyStore keyStore = getKeyStore();

        if (keyType == READ) {

            if (keyStore != null && keyStore instanceof ReadOnlyFileKeyStore) {
                key = keyStore.get();
            }
            else if (lastWrittenKey != null) {
                // last successfully written key - the method should be prepared for the situation the key is null.
                // Use this unless we have been passed a ReadOnly key keystore. A ReadOnly key keystore takes precedence.

                key = lastWrittenKey;
            }
            else {
                key = Util.getRandomKey(ThreadLocalRandom.current(), keySize);
            }

            o = new Read(key);
        }
        else //noinspection ConstantConditions
            if (keyType == WRITE) {
            String value;

            // TODO needs refactoring
            if (keyStore instanceof ExperimentalKeyStore) {
                key = keyStore.get();

                if (key == null) {
                    return null;
                }

                value = ((ExperimentalKeyStore)keyStore).getValue(key);
            }
            else {

                key = Util.getRandomKey(ThreadLocalRandom.current(), keySize);
                value = Util.getRandomValue(ThreadLocalRandom.current(), valueSize, useDifferentValues);
            }

            o = new Write(key, value);
        }
        else {

            throw new IllegalArgumentException("unknown key type " +keyType);
        }

        indexInSeries = (indexInSeries + 1) % (seriesSize + 1);

        return o;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes()
    {
        Set<Class<? extends Operation>> operations = new HashSet<>();

        operations.add(Read.class);
        operations.add(Write.class);

        return operations;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public ReadWriteRatio getReadWriteRatio()
    {
        return readWriteRatio;
    }

    @Override
    public String toString()
    {
        return getName() + "[" + readWriteRatio + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processArguments(List<String> arguments, int from) throws Exception
    {
        String rtw = null;
        String wtr = null;

        for (int i = from; i < arguments.size(); i++)
        {
            String crt = arguments.get(i);

            if ("--read-to-write".equals(crt))
            {
                if (i == arguments.size() - 1)
                {
                    throw new UserErrorException("a positive integer or zero must follow the '--read-to-write' option");
                }

                arguments.remove(i);
                rtw = arguments.remove(i);
                i --;
            }
            else if ("--write-to-read".equals(crt))
            {
                if (i == arguments.size() - 1)
                {
                    throw new UserErrorException("a positive integer or zero must follow the '--write-to-read' option");
                }

                arguments.remove(i);
                wtr = arguments.remove(i);
                i --;
            }
        }

        //
        // read to write/write to read ratio
        //

        readWriteRatio = new ReadWriteRatio(rtw, wtr);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
