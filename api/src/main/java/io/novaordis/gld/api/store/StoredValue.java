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
public class StoredValue {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * The only way to build StoredValue instance.
     *
     * @param args zero, null or one argument acceptable. If more than one argument is provided, the method will throw
     *             an IllegalArgumentException.
     *
     * @exception IllegalArgumentException if more than one argument is provided.
     *
    */
    public static StoredValue getInstance(byte[]... args) {

        if (args == null) {

            return Null.INSTANCE;
        }

        if (args.length == 0) {

            return NotStored.INSTANCE;
        }

        if (args.length > 1) {

            throw new IllegalArgumentException("invalid multiple arguments");
        }

        return new StoredValue(args[0]);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private byte[] content;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Used for normal content and when retrieving content from storage. null and NOT_STORED are handled by the
     * factory function.
     */
    protected StoredValue(byte[] content) {

        this.content = content;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Null/NotStored subclasses will override this.
     */
    public boolean isNull() {

        return false;
    }

    /**
     * Null/NotStored subclasses will override this.
     */
    public boolean notStored() {

        return false;
    }

    /**
     * @return the value bytes for a regular value. Return null of a null value or a "not stored" value.
     */
    public byte[] getBytes() {

        return content;
    }

    @Override
    public String toString() {

        if (isNull()) {

            return "null";
        }

        if (notStored()) {

            return "NOT_STORED";
        }

        if (content.length > 5) {

            return new String(content, 0, 5) + "...";
        }
        else {

            return new String(content);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
