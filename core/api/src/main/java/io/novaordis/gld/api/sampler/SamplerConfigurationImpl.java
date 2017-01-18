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

import io.novaordis.gld.api.configuration.LowLevelConfigurationBase;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/18/17
 */
public class SamplerConfigurationImpl extends LowLevelConfigurationBase implements SamplerConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param configurationDirectory represents the directory the configuration file the map was extracted from lives
     *                               in. It is needed to resolve the configuration elements that are relative file
     *                               paths. All relative file paths will be resolved relatively to the directory that
     *                               contains the configuration file. The directory must exist, otherwise the
     *                               constructor will fail with IllegalArgumentException.
     *
     * @throws IllegalArgumentException if the configuration file directory is not null, it does not exist or
     *                                  it is not a directory.
     * @exception UserErrorException on configuration errors.
     */
    public SamplerConfigurationImpl(Map<String, Object> raw, File configurationDirectory) throws UserErrorException {

        super(raw, configurationDirectory);
    }

    // SamplerConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public String getFormat() {

        String s = get(String.class, FORMAT_LABEL);
        s = s == null ? DEFAULT_FORMAT : s;
        return s;
    }

    @Override
    public File getFile() throws UserErrorException {

        File f = getFile(FILE_LABEL);

        if (f == null) {

            throw new UserErrorException("missing required statistics output file");
        }

        return f;
    }

    @Override
    public int getSamplingInterval() {

        Integer i = get(Integer.class, SAMPLING_INTERVAL_LABEL);
        i = i == null ? DEFAULT_SAMPLING_INTERVAL_MS : i;
        return i;
    }

    @Override
    public int getSamplingTaskRunInterval() {

        Integer i = get(Integer.class, SAMPLING_TASK_RUN_INTERVAL_LABEL);
        i = i == null ? DEFAULT_SAMPLING_TASK_RUN_INTERVAL : i;
        return i;
    }

    @Override
    public List<String> getMetrics() {

        List<Object> l = getList(METRICS_LABEL);
        List<String> result = new ArrayList<>();
        for(Object o: l) {

            if (!(o instanceof String)) {
                throw new IllegalStateException("expecting a list of strings and got a " + o.getClass().getSimpleName());
            }

            result.add((String)o);
        }

        return result;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
