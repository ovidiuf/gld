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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/10/17
 */
public class MockImplementationConfiguration implements ImplementationConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Map<String, Object> content;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockImplementationConfiguration() {

        this.content = new HashMap<>();
    }

    // ImplementationConfiguration implementation ----------------------------------------------------------------------


    @Override
    public String getExtensionName() throws UserErrorException {
        throw new NotYetImplementedException("getExtensionName() NOT YET IMPLEMENTED");
    }

    @Override
    public String getExtensionClass() throws UserErrorException {
        throw new NotYetImplementedException("getExtensionClass() NOT YET IMPLEMENTED");
    }

    @Override
    public <T> T get(Class<? extends T> type, String... path) {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public File getFile(String... path) {
        throw new NotYetImplementedException("getFile() NOT YET IMPLEMENTED");
    }

    @Override
    public Map<String, Object> get(String... path) {
        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public List<Object> getList(String... path) {

        if (path.length == 1 && JBossDatagrid7Service.NODES_LABEL.equals(path[0])) {

            Object o = content.get(path[0]);

            if (o == null) {

                return Collections.emptyList();
            }

            //noinspection unchecked
            return (List<Object>)o;
        }

        throw new NotYetImplementedException("getList() NOT YET IMPLEMENTED for " + Arrays.asList(path));
    }

    @Override
    public File getConfigurationDirectory() {
        throw new NotYetImplementedException("getConfigurationDirectory() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setNodes(List<Object> nodes) {

        content.put(JBossDatagrid7Service.NODES_LABEL, nodes);


    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
