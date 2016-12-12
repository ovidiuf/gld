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

package io.novaordis.gld.api;

import io.novaordis.gld.api.configuration.LoadConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.util.Iterator;
import java.util.Map;

public abstract class LoadStrategyBase implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private KeyProvider keyProvider;
    private RandomContentGenerator valueGenerator;
    private volatile boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected LoadStrategyBase() {

        this.valueGenerator = new RandomContentGenerator();
    }

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        if (started) {

            return;
        }

        started = true;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public void stop() {

        if (!started) {

            return;
        }

        started = false;
    }

    @Override
    public KeyProvider getKeyProvider() {

        return keyProvider;
    }

    /**
     * Perform consistency checks and fail with IllegalStateException if the configuration is inconsistent. If all is
     * good, extract the load strategy specific configuration map and pass it to configuration logic. Once the
     * configuration logic is executed, the method checks to make sure no unknown configuration elements are left in
     * the map, and fails if those are found.
     */
    @Override
    public final void init(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        Map<String, Object> loadStrategyRawConfig = sc.getMap(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

        //
        // check name consistency
        //

        String n = (String) loadStrategyRawConfig.remove(ServiceConfiguration.LOAD_STRATEGY_NAME_LABEL);

        if (!getName().equals(n)) {

            throw new IllegalStateException(
                    "inconsistent load strategy name, expected '" + getName() + "' but found '" + n + "'");
        }

        init(sc, loadStrategyRawConfig, lc);

        if (!loadStrategyRawConfig.isEmpty()) {

            String msg = "unknown " + getName() + " load strategy configuration option(s): ";

            for(Iterator<String> ki = loadStrategyRawConfig.keySet().iterator(); ki.hasNext(); ) {

                msg += "\"" + ki.next() + "\"";

                if (ki.hasNext()) {

                    msg += ", ";
                }
            }

            throw new UserErrorException(msg);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * The method encapsulates the logic that knows how to handle load strategy-specific configuration and
     * initialization. Upon processing, the implementation must remove the configuration options it recognizes.
     *
     * @param sc the service configuration. Subclasses may cast it to more specific types.
     *
     * @param loadStrategyRawConfig the underlying configuration map that contains load strategy-specific configuration.
     *                              The implementation must deplete it while processing it.
     *
     * @param lc the load characteristics.
     */
    protected abstract void init(
            ServiceConfiguration sc, Map<String, Object> loadStrategyRawConfig, LoadConfiguration lc) throws Exception;

    /**
     * For the use of subclasses - they should use this to install the key providers they create.
     */
    protected void setKeyProvider(KeyProvider kp) {

        this.keyProvider = kp;
    }

    /**
     * @return the generator to use to build random values. The keys are generated by the key provider
     */
    protected RandomContentGenerator getValueGenerator() {

        return valueGenerator;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
