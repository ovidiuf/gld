/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

@Deprecated
public interface Configuration
{
    Service getService();

    LoadStrategy getLoadStrategy();

    void setLoadStrategy(LoadStrategy loadStrategy);

    StorageStrategy getStorageStrategy();

    void setStorageStrategy(StorageStrategy storageStrategy);

    Command getCommand();

    void setCommand(Command c);

    /**
     * Set with --threads
     */
    int getThreads();

    /**
     * The maxTotal value we configure the Apache Commons Pool GenericObjectPool - the maximum number of ShardedJedis
     * instances.
     *
     * Default 100.
     *
     * Set with --max-total
     */
    int getMaxTotal();

    /**
     * The maxWaitMillis value we configure the Apache Commons Pool GenericObjectPool.
     *
     * Default 1000.
     *
     * Set with --max-wait-millis.
     */
    long getMaxWaitMillis();

    List<Node> getNodes();

    /**
     * The sleep time in milliseconds after executing an operation.. Default is 0 - don't sleep, execute next. Negative
     * also means "don't sleep".
     */
    long getSleepMs();

    /**
     * The key size, in characters. Default is 10.
     */
    int getKeySize();

    /**
     * The value size, in characters. Default is 20.
     */
    int getValueSize();

    /**
     * Whether to create a new value every time a random value is requested, or re-use the same value.
     */
    boolean isUseDifferentValues();

    /**
     * May be null which means "do not write statistics to file".
     */
    String getOutputFile();

    /**
     * May return null.
     */
    String getPassword();

    /**
     * The key expiration, in seconds. By default, the keys don't expire (the method returns -1L).
     */
    long getKeyExpirationSecs();

    /**
     * The name of the file to write exception details into. If null, exception details won't be written.
     */
    String getExceptionFile();

    /**
     * The name of the file to read keys from.
     */
    String getKeyStoreFile();

    String getUsername();

    boolean inBackground();

    Properties getConfigurationFileContent();

    boolean waitForConsoleQuit();

    void setWaitForConsoleQuit(boolean b);

    /**
     * @return the duration if the command is time limited, or null otherwise.
     */
    Duration getDuration();

    /**
     * This method is awkward, we must refactor and remove it, for the time being "duration" is a Load configuration
     * parameter and we need a way to pass it back to configuration, so we can pass it to the multi-thread runner.
     *
     * TODO: make duration a global parameter and get rid of this method.
     */
    @Deprecated
    void setDuration(Duration d);

}
