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

package io.novaordis.gld.api.mock.configuration;

import io.novaordis.gld.api.configuration.LoadConfigurationImpl;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockLoadConfiguration extends LoadConfigurationImpl {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Integer keySize;
    private Integer valueSize;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockLoadConfiguration(ServiceType st) throws Exception {

        super(st, new HashMap<>(), new File("."));

        Map<String, Object> root = get();

        root.put(THREAD_COUNT_LABEL, 1);
    }

    // MockLoadConfiguration implementation ----------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    public void setOperations(Integer i) {

        Map<String, Object> root = get();
        root.put(OPERATION_COUNT_LABEL, i);
    }

    @Override
    public Integer getKeySize() throws UserErrorException {

        return keySize;
    }

    @Override
    public Integer getValueSize() throws UserErrorException {

        return valueSize;
    }

    @Override
    public Integer getMessageSize() throws UserErrorException {

        throw new RuntimeException("getMessageSize() NOT YET IMPLEMENTED");
    }

    public void setKeySize(Integer i) {

        this.keySize = i;

    }

    public void setValueSize(Integer i) {

        this.valueSize = i;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
