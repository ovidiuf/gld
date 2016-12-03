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

package io.novaordis.gld.api.todiscard;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Service;
import io.novaordis.utilities.time.Duration;

import java.util.List;
import java.util.Properties;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/1/16
 */
public class MockConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public Service getService() {
        throw new RuntimeException("getService() NOT YET IMPLEMENTED");
    }

    @Override
    public LoadStrategy getLoadStrategy() {
        throw new RuntimeException("getLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void setLoadStrategy(LoadStrategy loadStrategy) {
        throw new RuntimeException("setLoadStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public StorageStrategy getStorageStrategy() {
        throw new RuntimeException("getStorageStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public void setStorageStrategy(StorageStrategy storageStrategy) {
        throw new RuntimeException("setStorageStrategy() NOT YET IMPLEMENTED");
    }

    @Override
    public Command getCommand() {
        throw new RuntimeException("getCommand() NOT YET IMPLEMENTED");
    }

    @Override
    public void setCommand(Command c) {
        throw new RuntimeException("setCommand() NOT YET IMPLEMENTED");
    }

    @Override
    public int getThreads() {
        throw new RuntimeException("getThreads() NOT YET IMPLEMENTED");
    }

    @Override
    public int getMaxTotal() {
        throw new RuntimeException("getMaxTotal() NOT YET IMPLEMENTED");
    }

    @Override
    public long getMaxWaitMillis() {
        throw new RuntimeException("getMaxWaitMillis() NOT YET IMPLEMENTED");
    }

    @Override
    public List<Node> getNodes() {
        throw new RuntimeException("getNodes() NOT YET IMPLEMENTED");
    }

    @Override
    public long getSleepMs() {
        throw new RuntimeException("getSleepMs() NOT YET IMPLEMENTED");
    }

    @Override
    public int getKeySize() {
        throw new RuntimeException("getKeySize() NOT YET IMPLEMENTED");
    }

    @Override
    public int getValueSize() {
        throw new RuntimeException("getValueSize() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isUseDifferentValues() {
        throw new RuntimeException("isUseDifferentValues() NOT YET IMPLEMENTED");
    }

    @Override
    public String getOutputFile() {
        throw new RuntimeException("getOutputFile() NOT YET IMPLEMENTED");
    }

    @Override
    public String getPassword() {
        throw new RuntimeException("getPassword() NOT YET IMPLEMENTED");
    }

    @Override
    public long getKeyExpirationSecs() {
        throw new RuntimeException("getKeyExpirationSecs() NOT YET IMPLEMENTED");
    }

    @Override
    public String getExceptionFile() {
        throw new RuntimeException("getExceptionFile() NOT YET IMPLEMENTED");
    }

    @Override
    public String getKeyStoreFile() {
        throw new RuntimeException("getKeyStoreFile() NOT YET IMPLEMENTED");
    }

    @Override
    public String getUsername() {
        throw new RuntimeException("getUsername() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean inBackground() {
        throw new RuntimeException("inBackground() NOT YET IMPLEMENTED");
    }

    @Override
    public Properties getConfigurationFileContent() {
        throw new RuntimeException("getConfigurationFileContent() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean waitForConsoleQuit() {
        throw new RuntimeException("waitForConsoleQuit() NOT YET IMPLEMENTED");
    }

    @Override
    public void setWaitForConsoleQuit(boolean b) {
        throw new RuntimeException("setWaitForConsoleQuit() NOT YET IMPLEMENTED");
    }

    @Override
    public Duration getDuration() {
        throw new RuntimeException("getDuration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setDuration(Duration d) {
        throw new RuntimeException("setDuration() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
