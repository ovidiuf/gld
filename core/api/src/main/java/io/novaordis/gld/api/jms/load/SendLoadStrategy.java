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

package io.novaordis.gld.api.jms.load;

import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.jms.operation.Send;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/17
 */
public class SendLoadStrategy extends JmsLoadStrategyBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String NAME = "send";

    private static final Set<Class<? extends Operation>> OPERATION_TYPES;

    static {

        OPERATION_TYPES = new HashSet<>();
        OPERATION_TYPES.add(Send.class);
    }

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // JmsLoadStrategyBase overrides -----------------------------------------------------------------------------------

    @Override
    public String getName() {

        return NAME;
    }

    @Override
    public Set<Class<? extends Operation>> getOperationTypes() {

        return OPERATION_TYPES;
    }

    @Override
    protected Operation nextInternal(Operation last, String lastWrittenKey, boolean runtimeShuttingDown)
            throws Exception {

        return new Send(this);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        Long remainingOperations = getRemainingOperations();
        return NAME + " " + (remainingOperations == null ? "unlimited" : "(remaining=" + remainingOperations + ")");
    }

    @Override
    protected void initInternal(ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc) throws Exception {
        throw new RuntimeException("initInternal() NOT YET IMPLEMENTED");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
