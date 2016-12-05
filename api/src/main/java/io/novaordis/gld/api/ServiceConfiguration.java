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

import java.util.Map;

/**
 * Typed and untyped access to underlying service configuration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public interface ServiceConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String TYPE_LABEL = "type";
    String IMPLEMENTATION_LABEL = "implementation";
    String LOAD_STRATEGY_NAME_LABEL = "load-strategy";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Typed Access ----------------------------------------------------------------------------------------------------

    ServiceType getType();

    String getImplementation();

    String getLoadStrategyName();

    // Untyped Access --------------------------------------------------------------------------------------------------

    /**
     * @return the underlying raw configuration map corresponding to the given path, or null if the path does not
     * match anything in the underlying configuration structure.
     */
    Map<String, Object> getMap(String ... path);

}
