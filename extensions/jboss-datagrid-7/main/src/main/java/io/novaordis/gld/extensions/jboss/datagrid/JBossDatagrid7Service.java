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

package io.novaordis.gld.extensions.jboss.datagrid;

import io.novaordis.gld.api.cache.CacheServiceBase;
import io.novaordis.gld.api.configuration.ServiceConfiguration;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.version.VersionUtilities;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class JBossDatagrid7Service extends CacheServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagrid7Service.class);

    public static final String EXTENSION_VERSION_METADATA_FILE_NAME = "jboss-datagrid-7-extension-version";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public JBossDatagrid7Service() {

        log.debug(this + " constructed");
    }

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    public String getVersion() {

        return VersionUtilities.getVersion(EXTENSION_VERSION_METADATA_FILE_NAME);
    }

    @Override
    public void configure(ServiceConfiguration serviceConfiguration) throws UserErrorException {

        log.debug(this + " configured");
    }

    @Override
    public void start() throws Exception {

        super.start();

        String host = "localhost";
        int port = 11222;
        String cacheName = null;

        Configuration c = new ConfigurationBuilder().addServer().host(host).port(port).build();

        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(c);

        RemoteCache cache;

        if (cacheName == null) {

            cache = remoteCacheManager.getCache();
        }
        else {

            cache = remoteCacheManager.getCache(cacheName);
        }

        if (cache == null) {

            throw new UserErrorException("no such cache: " + cacheName);
        }
    }

    @Override
    public void stop() {

        super.stop();
    }

    // CacheService implementation -------------------------------------------------------------------------------------

    public String get(String key) throws Exception {

        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    public void put(String key, String value) throws Exception {

        throw new RuntimeException("put() NOT YET IMPLEMENTED");
    }

    public void remove(String key) throws Exception {

        throw new RuntimeException("remove() NOT YET IMPLEMENTED");
    }

    public Set<String> keys() throws Exception {

        throw new RuntimeException("keys() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
