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

package io.novaordis.gld.api;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public abstract class OperationBase implements Operation {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String key;
    private LoadStrategy loadStrategy;
    private volatile boolean performed;
    private volatile boolean successful;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected OperationBase(String key) {

        this.key = key;
    }

    // Operation implementation ----------------------------------------------------------------------------------------

    public String getKey() {

        return key;
    }

    public boolean wasPerformed() {

        return performed;
    }

    public boolean wasSuccessful() {

        return successful;
    }

    public LoadStrategy getLoadStrategy() {

        return loadStrategy;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setPerformed(boolean b) {

        this.performed = b;
    }

    protected void setSuccessful(boolean b) {

        this.successful = b;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
