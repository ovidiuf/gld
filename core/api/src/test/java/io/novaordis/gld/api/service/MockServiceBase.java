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

package io.novaordis.gld.api.service;

import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/11/17
 */
public class MockServiceBase extends ServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private RuntimeException isStartedFailure;

    // Constructors ----------------------------------------------------------------------------------------------------

    // ServiceBase overrides -------------------------------------------------------------------------------------------

    @Override
    public boolean isStarted() {

        if (isStartedFailure != null) {
            throw isStartedFailure;
        }

        return super.isStarted();
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        throw new NotYetImplementedException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public ServiceType getType() {

        throw new NotYetImplementedException("getType() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void makeFail(String methodName, RuntimeException exception) {

        if ("isStarted".equals(methodName)) {

            isStartedFailure = exception;
        }
        else {

            throw new NotYetImplementedException("we don't know how to make " + methodName + "(...) fail");
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
