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

package com.novaordis.gld.keystore;

import com.novaordis.gld.KeyStore;
import com.novaordis.gld.Util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class RandomKeyGenerator implements KeyStore
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;
    private int keyLength;
    private AtomicLong remainingKeys;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Auto-starting.
     *
     * @param keyLength - the length of the keys that will be generated.
     * @param maxKeys - the maximum number of keys to return after get() starts to return null. A null, zero or negative
     *                value means "return an unlimited number of keys"
     */
    public RandomKeyGenerator(int keyLength, Long maxKeys) throws Exception
    {
        this.keyLength = keyLength;

        if (maxKeys != null && maxKeys > 0)
        {
            this.remainingKeys = new AtomicLong(maxKeys);
        }

        start();
    }

    /**
     * @see RandomKeyGenerator#RandomKeyGenerator(int, Long)
     */
    public RandomKeyGenerator(int keyLength) throws Exception
    {
        this(keyLength, null);
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    /**
     * @see com.novaordis.gld.KeyStore#store(String)
     */
    @Override
    public void store(String key) throws Exception
    {
        throw new IllegalStateException("this is a read-only keystore, cannot store");
    }

    /**
     * @see com.novaordis.gld.KeyStore#get()
     */
    @Override
    public String get()
    {
        if (remainingKeys != null && remainingKeys.getAndDecrement() <= 0)
        {
            remainingKeys.set(0);
            return null;
        }

        //noinspection UnnecessaryLocalVariable
        String result = Util.getRandomKey(ThreadLocalRandom.current(), keyLength);
        return result;
    }

    @Override
    public void start() throws Exception
    {
        started = true;
    }

    @Override
    public void stop() throws Exception
    {
        started = false;
    }

    @Override
    public boolean isStarted()
    {
        return started;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
