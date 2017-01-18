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

import io.novaordis.gld.api.configuration.LowLevelConfigurationBase;
import io.novaordis.gld.api.sampler.SamplerConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/12/17
 */
public class MockSamplerConfiguration extends LowLevelConfigurationBase implements SamplerConfiguration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int samplingInterval;
    private int samplingTaskRunInterval;
    private File file;
    private List<String> metrics;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockSamplerConfiguration() {

        this(new HashMap<>());
    }

    public MockSamplerConfiguration(Map<String, Object> raw) {

        super(raw, new File("."));

        this.samplingTaskRunInterval = 1;
        this.samplingInterval = 2;
        setFile(new File("/dev/null"));
        this.metrics = Collections.emptyList();
    }

    // SamplerConfiguration implementation -----------------------------------------------------------------------------

    @Override
    public String getFormat() {

        return "csv";
    }

    @Override
    public File getFile() throws UserErrorException {

        return file;
    }

    @Override
    public int getSamplingInterval() {

        return samplingInterval;
    }

    @Override
    public int getSamplingTaskRunInterval() {

        return samplingTaskRunInterval;
    }

    @Override
    public List<String> getMetrics() {

        return metrics;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setSamplingInterval(int i) {

        this.samplingInterval = i;
    }

    public void setSamplingTaskRunInterval(int i) {

        this.samplingTaskRunInterval = i;
    }

    public void setFile(File f) {

        this.file = f;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
