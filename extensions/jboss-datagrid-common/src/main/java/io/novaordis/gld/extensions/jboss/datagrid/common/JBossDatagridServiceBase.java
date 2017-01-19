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

package io.novaordis.gld.extensions.jboss.datagrid.common;

import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.gld.api.configuration.ImplementationConfiguration;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.gld.api.service.ServiceFactory;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public abstract class JBossDatagridServiceBase extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagridServiceBase.class);

    public static final String NODES_LABEL = "nodes";
    public static final String CACHE_NAME_LABEL = "cache";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * We are exposing this as a static JBossDatagridServiceBase method because we don't want to make the extension
     * modules directly dependent on io.novaordis.gld.api.service.ServiceFactory (API).
     */
    public static String extensionNameToExtensionServiceFullyQualifiedClassName(String extensionName)
            throws UserErrorException {

        return ServiceFactory.extensionNameToExtensionServiceFullyQualifiedClassName(extensionName);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<HotRodEndpointAddress> nodes;
    private InfinispanCache cache;

    //
    // null means "default cache"
    //
    private String cacheName;

    // Constructors ----------------------------------------------------------------------------------------------------

    public JBossDatagridServiceBase() {

        this.nodes = new ArrayList<>();
        log.debug(this + " constructed");
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        ImplementationConfiguration ic = serviceConfiguration.getImplementationConfiguration();

        List<Object> list = ic.getList(NODES_LABEL);

        if (list.isEmpty()) {

            throw new UserErrorException("at least one JDG node must be specified");
        }

        //
        // nodes
        //

        for(Object o: list) {

            if (!(o instanceof String)) {

                throw new UserErrorException(
                        "'" + NODES_LABEL + "' should be a String list, but it was found to contain " +
                                o.getClass().getSimpleName() + "s");
            }

            String nodeSpecification = (String)o;
            HotRodEndpointAddress n = new HotRodEndpointAddress(nodeSpecification);
            addNode(n);
        }

        //
        // cache name - also turn invalid values in UserErrorExceptions
        //

        try {

            this.cacheName = ic.get(String.class, CACHE_NAME_LABEL);

        } catch (Exception e) {

            throw new UserErrorException(e);
        }

        log.debug(this + " configured");
    }

    @Override
    public synchronized void start() throws Exception {

        if (isStarted()) {

            //
            // noop
            //
            log.debug(this + " already started");

            return;
        }

        super.start();

        if (nodes.isEmpty()) {

            throw new IllegalStateException(this + ": no nodes");
        }

        cache = configureAndStartInfinispanCache();
    }

    @Override
    public synchronized boolean isStarted() {

        return cache != null;
    }

    @Override
    public synchronized void stop() {

        if (!isStarted()) {

            //
            // noop
            //
            log.debug(this + " already stopped");
            return;
        }

        super.stop();
        stopInfinispanCache(cache);
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    public String get(String key) throws Exception {

        checkStarted();

        return cache.get(key);
    }

    public void put(String key, String value) throws Exception {

        checkStarted();

        cache.put(key, value);
    }

    public void remove(String key) throws Exception {

        checkStarted();

        cache.remove(key);
    }

    public Set<String> keys() throws Exception {

        checkStarted();

        return cache.keys();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the actual storage. May return an empty list, but never null.
     */
    public List<HotRodEndpointAddress> getNodes() {

        return nodes;
    }

    /**
     * @return the cache name.
     *
     * If the instance was configured but not started, a null value means "default cache".
     *
     * After the instance was started successfully, and thus a cache retrieved, the method will never return null,
     * as there cannot be a no-name cache.
     */
    public String getCacheName() {

        if (cache != null) {

            //
            // "real" name takes precedence
            //

            return cache.getName();
        }

        return cacheName;
    }

    @Override
    public String toString() {

        if (nodes == null || nodes.isEmpty()) {

            return "unconfigured jboss datagrid 7 service";
        }

        String s = "";

        for(Iterator<HotRodEndpointAddress> i = getNodes().iterator(); i.hasNext(); ) {

            s += i.next();

            if (i.hasNext()) {

                s += ", ";
            }
        }

        String n = getCacheName();

        if (n == null) {

            n = "DEFAULT CACHE";
        }

        s += " (" + n + ")";

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    void addNode(HotRodEndpointAddress n) {

        nodes.add(n);
    }

    // Protected -------------------------------------------------------------------------------------------------------

    protected  abstract InfinispanCache configureAndStartInfinispanCache() throws UserErrorException;

    protected abstract void stopInfinispanCache(InfinispanCache cache);

    // Private ---------------------------------------------------------------------------------------------------------

    private void checkStarted() throws IllegalStateException {

        if (!isStarted()) {
            throw new IllegalStateException(this + " not started");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
