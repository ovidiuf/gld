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

package io.novaordis.gld.api.configuration;

import io.novaordis.gld.api.sampler.SamplerConfiguration;
import io.novaordis.utilities.UserErrorException;

/**
 * Typed access to the output configuration. It essentially specifies where to write collected statistics, among other
 * things. The implementations of this interface also allow low-level typed access (typed access to specific points into
 * the configuration structure) via LowLevelConfiguration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/4/16
 */
public interface OutputConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String SAMPLER_CONFIGURATION_LABEL = "statistics";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Typed Access ----------------------------------------------------------------------------------------------------

    /**
     * @return may return null, which usually means we don't want statistics.
     *
     * @throws UserErrorException on any invalid configuration.
     */
    SamplerConfiguration getSamplerConfiguration() throws UserErrorException;

    // Untyped Access --------------------------------------------------------------------------------------------------

}
