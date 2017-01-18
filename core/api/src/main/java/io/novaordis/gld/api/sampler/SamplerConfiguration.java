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

package io.novaordis.gld.api.sampler;

import io.novaordis.gld.api.configuration.LowLevelConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.List;

/**
 * Typed access to the sampler configuration. The implementations of this interface also allow low-level typed access
 * (typed access to specific points into the configuration structure) via LowLevelConfiguration.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/12/17
 */
public interface SamplerConfiguration extends LowLevelConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    String FORMAT_LABEL = "format";
    String DEFAULT_FORMAT = "csv";

    String FILE_LABEL = "file";

    String SAMPLING_INTERVAL_LABEL = "sampling-interval";
    int DEFAULT_SAMPLING_INTERVAL_MS = 1000;

    String SAMPLING_TASK_RUN_INTERVAL_LABEL = "sampling-task-run-interval";
    int DEFAULT_SAMPLING_TASK_RUN_INTERVAL = 250;

    String METRICS_LABEL = "metrics";

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The output format. By default, if not specified is "csv" (comma-separated value).
     *
     * https://kb.novaordis.com/index.php/Gld_Configuration#format
     * @return
     */
    String getFormat();

    /**
     * The name of the file to write the output to. If relative, the path will be considered relative to the location of
     * the configuration file. This is a required value, and there is no default. Will return a non-null value.
     *
     * @exception UserErrorException on missing required value.
     * @exception IllegalStateException on invalid type
     */
    File getFile() throws UserErrorException;

    /**
     * The interval (in milliseconds) between successive samples. The default value is 1,000 ms.
     *
     * https://kb.novaordis.com/index.php/Gld_Configuration#sampling-interval
     * @return
     */
    int getSamplingInterval();

    /**
     * The interval (in milliseconds) between successive runs of the sampling task. The default value is 250 ms.
     *
     * https://kb.novaordis.com/index.php/Gld_Configuration#sampling-task-run-interval
     * @return
     */
    int getSamplingTaskRunInterval();

    /**
     * The list of metrics to be read in addition to those configured dynamically. May return an empty list but never
     * null.
     *
     * https://kb.novaordis.com/index.php/Gld_Configuration#metrics
     */
    List<String> getMetrics();

}
