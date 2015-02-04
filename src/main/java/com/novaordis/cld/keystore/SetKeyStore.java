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

package com.novaordis.cld.keystore;

import com.novaordis.cld.KeyStore;

import java.util.Iterator;
import java.util.Set;

/**
 * This implementation reads the entire key space in memory on startup and then keeps cycling through it.
 */

public class SetKeyStore implements KeyStore
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;

    private Iterator<String> iterator;

    private int size;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Auto-starting.
     */
    public SetKeyStore(Set<String> keys) throws Exception
    {
        this.size = keys.size();
        this.iterator = keys.iterator();
        start();
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    /**
     * @see com.novaordis.cld.KeyStore#store(String)
     */
    @Override
    public void store(String key) throws Exception
    {
        throw new IllegalStateException("this is a read-only keystore, cannot store");
    }

    /**
     * @see com.novaordis.cld.KeyStore#get()
     */
    @Override
    public synchronized String get()
    {
        if (!iterator.hasNext())
        {
            return null;
        }

        size --;
        return iterator.next();
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

    public int size()
    {
        return size;
    }

    @Override
    public String toString()
    {
        return "SetKeyStore[" + size + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
