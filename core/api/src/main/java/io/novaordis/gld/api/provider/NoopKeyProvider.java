/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.gld.api.provider;

import io.novaordis.gld.api.KeyProvider;

public class NoopKeyProvider implements KeyProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private volatile boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // KeyProvider implementation --------------------------------------------------------------------------------------

    // lifecycle -------------------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        started = true;
    }

    @Override
    public void stop() {

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public Long getRemainingKeyCount() {

        return null;
    }

    @Override
    public String next() {

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
