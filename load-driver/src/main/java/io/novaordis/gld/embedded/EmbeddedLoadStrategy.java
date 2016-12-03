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

package io.novaordis.gld.embedded;

import io.novaordis.gld.api.LoadStrategyBase;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.Service;
import io.novaordis.gld.embedded.operation.SyntheticDelete;
import io.novaordis.gld.embedded.operation.SyntheticRead;
import io.novaordis.gld.embedded.operation.SyntheticWrite;

import java.util.HashSet;
import java.util.Set;

/**
 * Simulates the interaction with a cache (SyntheticRead, SyntheticWrite and SyntheticDelete).
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class EmbeddedLoadStrategy extends LoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Service service;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedLoadStrategy(Service service) {

        this.service = service;

        setKeyStore(service.getLoadDriver().getKeyStore());
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        Set<Class<? extends Operation>> result = new HashSet<>();
        result.add(SyntheticRead.class);
        result.add(SyntheticWrite.class);
        result.add(SyntheticDelete.class);
        return result;
    }

    @Override
    public Operation next(Operation last, String lastWrittenKey, boolean runtimeShuttingDown) throws Exception {

        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
