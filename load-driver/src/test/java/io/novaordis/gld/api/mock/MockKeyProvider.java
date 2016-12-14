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

package io.novaordis.gld.api.mock;

import io.novaordis.gld.api.KeyProvider;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/13/16
 */
public class MockKeyProvider implements KeyProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    // KeyProvider implementation --------------------------------------------------------------------------------------

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
    public String next() {
        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getRemainingKeyCount() {
        throw new RuntimeException("getRemainingKeyCount() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
