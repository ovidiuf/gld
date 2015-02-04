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

package com.novaordis.cld.strategy.load;

import com.novaordis.cld.Configuration;
import com.novaordis.cld.KeyStore;
import com.novaordis.cld.Operation;
import com.novaordis.cld.Util;
import com.novaordis.cld.keystore.RandomKeyGenerator;
import com.novaordis.cld.keystore.ReadOnlyFileKeyStore;
import com.novaordis.cld.operations.Read;
import com.novaordis.cld.operations.Write;

import java.util.List;
import java.util.Random;

/**
 * A load strategy that generates the following sequence: it first attempts a read. If it's a hit, it keeps reading.
 * If it's a miss, it writes the key and a random value. It gets the keys from the configured key store or it randomly
 * generates them if there is not key store.
 *
 * It is supposed to be thread-safe, the intention is to be accessed concurrently from different threads.
 *
 * This implies that the KeyStore implementation is also thread-safe.
 */
public class ReadThenWriteOnMissLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

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

    /**
     * @see com.novaordis.cld.LoadStrategy#configure(Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration conf, List<String> arguments, int from) throws Exception
    {
        super.configure(conf, arguments, from);

        int keySize = conf.getKeySize();
        int valueSize = conf.getValueSize();
        boolean useDifferentValues = conf.isUseDifferentValues();

        String keyStoreFile = conf.getKeyStoreFile();
        KeyStore keyStore;

        if (keyStoreFile == null)
        {
            keyStore = new RandomKeyGenerator(keySize);
        }
        else
        {
            keyStore = new ReadOnlyFileKeyStore(keyStoreFile);
            keyStore.start();
        }

        setKeyStore(keyStore);

        syntheticValue = Util.getRandomValue(
            new Random(System.currentTimeMillis() + 17L), valueSize, useDifferentValues);

        configured = true;
    }

    /**
     * @see com.novaordis.cld.LoadStrategy#next(Operation, String)
     */
    public Operation next(Operation lastOperation, String lastWrittenKey)
    {
        if (!configured)
        {
            throw new IllegalStateException(this + " not properly configured");
        }

        KeyStore keyStore = getKeyStore();

        Operation result;

        if (lastOperation == null)
        {
            // start with a read
            result = new Read(keyStore.get());
        }
        else if (lastOperation instanceof Read)
        {
            Read lastRead = (Read)lastOperation;

            // if the last read is a hit, generate another read

            if (lastRead.getValue() != null)
            {
                result = new Read(keyStore.get());
            }
            else
            {
                result = new Write(lastRead.getKey(), syntheticValue);
            }
        }
        else if (lastOperation instanceof Write)
        {
            // read follows a write
            result = new Read(keyStore.get());
        }
        else
        {
            throw new IllegalArgumentException("unknown last operation " + lastOperation);
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
