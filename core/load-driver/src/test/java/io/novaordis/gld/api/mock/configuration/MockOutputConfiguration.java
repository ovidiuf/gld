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

package io.novaordis.gld.api.mock.configuration;

import io.novaordis.gld.api.configuration.OutputConfiguration;
import io.novaordis.gld.api.sampler.SamplerConfiguration;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/9/16
 */
public class MockOutputConfiguration implements OutputConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // OutputConfiguration implementation ------------------------------------------------------------------------------

    @Override
    public <T> T get(Class<? extends T> type, String... path) {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public File getFile(String... path) {
        throw new RuntimeException("getFile() NOT YET IMPLEMENTED");
    }

    @Override
    public Map<String, Object> get(String... path) {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public List<Object> getList(String... path) {
        throw new NotYetImplementedException("getList() NOT YET IMPLEMENTED");
    }

    @Override
    public File getConfigurationDirectory() {
        throw new NotYetImplementedException("getConfigurationDirectory() NOT YET IMPLEMENTED");
    }

    @Override
    public SamplerConfiguration getSamplerConfiguration() throws UserErrorException {
        throw new NotYetImplementedException("getSamplerConfiguration() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
