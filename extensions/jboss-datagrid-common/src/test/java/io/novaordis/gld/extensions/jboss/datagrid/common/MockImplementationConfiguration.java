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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.LowLevelConfigurationBase;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class MockImplementationConfiguration extends LowLevelConfigurationBase implements ImplementationConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockImplementationConfiguration() {

        super(new HashMap<>(), new File("."));
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

    // Public ----------------------------------------------------------------------------------------------------------

    public void setNodes(List<Object> nodes) {

        set(nodes, JBossDatagridServiceBase.NODES_LABEL);
    }

    public void setCacheName(Object cacheName) {

        set(cacheName, JBossDatagridServiceBase.CACHE_NAME_LABEL);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
