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
import io.novaordis.gld.api.service.Service;
import io.novaordis.gld.api.service.ServiceType;
import io.novaordis.utilities.UserErrorException;

import java.util.Iterator;
import java.util.Map;

public abstract class LoadStrategyBase implements LoadStrategy {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Service service;
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

        if (keyProvider != null) {

            keyProvider.start();
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

        if (keyProvider != null) {

            keyProvider.stop();
        }
    }

    @Override
    public KeyProvider getKeyProvider() {

        return keyProvider;
    }

    @Override
    public void setKeyProvider(KeyProvider kp) {

        this.keyProvider = kp;
    }

    /**
     * Perform consistency checks and fail with IllegalStateException if the configuration is inconsistent. If all is
     * good, extract the load strategy specific configuration map and pass it to configuration logic. Once the
     * configuration logic is executed, the method checks to make sure no unknown configuration elements are left in
     * the map, and fails if those are found.
     */
    @Override
    public final void init(ServiceConfiguration sc, LoadConfiguration lc) throws Exception {

        Map<String, Object> loadStrategyRawConfig = sc.get(ServiceConfiguration.LOAD_STRATEGY_CONFIGURATION_LABEL);

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

    @Override
    public void setService(Service s) {

        if (s == null) {

            //
            // remove the relationship, if any
            //
            this.service = null;
        }
        else {

            //
            // insure the types are the same
            //

            ServiceType thatType = s.getType();

            if (thatType == null) {

                throw new IllegalArgumentException("invalid service " + s + ", it has a null type");
            }

            ServiceType thisType = getServiceType();

            if (!thatType.equals(thisType)) {

                throw new IllegalArgumentException(
                        "cannot associate a " + thatType + " service with a " + thisType + " load strategy");
            }

            this.service = s;
        }
    }

    @Override
    public Service getService() {

        return service;
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
     * @return the generator to use to build random values. The keys are generated by the key provider
     */
    protected RandomContentGenerator getValueGenerator() {

        return valueGenerator;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
