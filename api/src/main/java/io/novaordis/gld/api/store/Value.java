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

package io.novaordis.gld.api.store;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class Value {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private byte[] content;
    private boolean notStored;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Use when storing and when having to handle variable length argument lists.
     */
    public Value(byte[][] args) {

        if (args.length == 0) {

            //
            // the API user does not want to store values
            //
            notStored = true;
        }
    }

    /**
     * Use when retrieving content from storage.
     */
    public Value(byte[] content) {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    /**
     * Use for testing
     */
    Value(String content) {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public boolean isNull() {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    public boolean notStored() {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    public byte[] getBytes() {

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
