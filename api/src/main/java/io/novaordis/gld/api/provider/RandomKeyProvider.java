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

package io.novaordis.gld.api.provider;

import io.novaordis.gld.api.KeyProvider;
import io.novaordis.gld.api.RandomContentGenerator;
import io.novaordis.gld.api.ServiceConfiguration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class RandomKeyProvider implements KeyProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;

    private int keySize;

    private AtomicLong remainingKeyCount;

    private RandomContentGenerator contentGenerator;

    // Constructors ----------------------------------------------------------------------------------------------------

    public RandomKeyProvider() {

        this.keySize = ServiceConfiguration.DEFAULT_KEY_SIZE;
        this.contentGenerator = new RandomContentGenerator();
    }

    // KeyProvider implementation --------------------------------------------------------------------------------------

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        if (started) {

            return;
        }

        started = true;
    }

    @Override
    public void stop() throws Exception {

        if (!started) {

            return;
        }

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    // accessors -------------------------------------------------------------------------------------------------------

    @Override
    public void setKeySize(int size) {

        if (started) {

            throw new IllegalStateException("cannot configure a started instance");
        }

        keySize = size;
    }

    @Override
    public int getKeySize() {

        return keySize;
    }

    @Override
    public void setKeyCount(Long l) {

        if (started) {

            throw new IllegalStateException("cannot configure a started instance");
        }

        if (l == null) {

            remainingKeyCount = null;
        }
        else {

            remainingKeyCount = new AtomicLong(l);
        }
    }

    @Override
    public Long getRemainingKeyCount() {

        if (remainingKeyCount == null) {

            return null;
        }

        return remainingKeyCount.get();
    }

    @Override
    public String next() {

        if (remainingKeyCount != null && remainingKeyCount.getAndDecrement() <= 0) {

            remainingKeyCount.set(0);
            return null;
        }

        if (!started) {

            throw new IllegalStateException(this + " not started");
        }

        //noinspection UnnecessaryLocalVariable
        String result = contentGenerator.getRandomString(ThreadLocalRandom.current(), keySize);
        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
