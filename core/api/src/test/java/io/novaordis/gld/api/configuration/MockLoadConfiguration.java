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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class MockLoadConfiguration extends LowLevelConfigurationBase implements LoadConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_KEY_SIZE = 7;
    public static final int DEFAULT_VALUE_SIZE = 77;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long operations;
    private Integer valueSize;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockLoadConfiguration() {

        this(new HashMap<>());
    }

    public MockLoadConfiguration(Map<String, Object> raw) {

        super(raw, new File(System.getProperty("basedir")));

        //
        // default unlimited
        //
        this.operations = null;

        this.valueSize = DEFAULT_VALUE_SIZE;
    }

    // LoadConfiguration implementation --------------------------------------------------------------------------------

    @Override
    public ServiceType getServiceType() {
        throw new RuntimeException("getServiceType() NOT YET IMPLEMENTED");
    }

    @Override
    public int getThreadCount() {
        throw new RuntimeException("getThreadCount() NOT YET IMPLEMENTED");
    }

    @Override
    public Long getOperations() {

        // unlimited
        return operations;
    }

    @Override
    public Long getRequests() {

        return getOperations();
    }

    @Override
    public Long getMessages() {

        return getOperations();
    }

    @Override
    public Integer getKeySize() throws UserErrorException {

        return DEFAULT_KEY_SIZE;
    }

    @Override
    public Integer getValueSize() throws UserErrorException {

        return valueSize;
    }

    @Override
    public Integer getMessageSize() throws UserErrorException {

        return getValueSize();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setValueSize(Integer i) {

        this.valueSize = i;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
