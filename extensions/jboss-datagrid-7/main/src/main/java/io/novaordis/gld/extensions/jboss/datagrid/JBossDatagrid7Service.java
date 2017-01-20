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

import io.novaordis.gld.extensions.jboss.datagrid.common.HotRodEndpointAddress;
import io.novaordis.gld.extensions.jboss.datagrid.common.InfinispanCache;
import io.novaordis.gld.extensions.jboss.datagrid.common.JBossDatagridServiceBase;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.version.VersionUtilities;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This version is coded under the assumption that the cache container name does not make any difference, and all that
 * matters is the cache name. A cursory examination of the API did not seem to allow for specifying the "cache container
 * name". If that is indeed possible, this code will have to be refactored.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/14/16
 */
public class JBossDatagrid7Service extends JBossDatagridServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(JBossDatagrid7Service.class);

    public static final String EXTENSION_VERSION_METADATA_FILE_NAME = "jboss-datagrid-7-extension-version";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    public String getVersion() {

        return VersionUtilities.getVersion(EXTENSION_VERSION_METADATA_FILE_NAME);
    }

    // JBossDatagridServiceBase overrides ------------------------------------------------------------------------------

    @Override
    public InfinispanCache configureAndStartInfinispanCache() throws UserErrorException {

        try {

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

            for (HotRodEndpointAddress n : getNodes()) {

                configurationBuilder.addServer().host(n.getHost()).port(n.getPort());
            }

            Configuration infinispanConfiguration = configurationBuilder.build();

            RemoteCacheManager remoteCacheManager = new RemoteCacheManager(infinispanConfiguration);

            String cn = getCacheName();

            RemoteCache rc;

            if (cn == null) {

                rc = remoteCacheManager.getCache();

            } else {

                rc = remoteCacheManager.getCache(cn);
            }

            if (rc == null) {

                throw new Exception("no such cache: " + cn);
            }

            return new InfinispanCacheImpl(rc);
        }
        catch (UserErrorException e) {

            throw e;
        }
        catch (Throwable e) {

            throw new UserErrorException("failed to start jboss datagrid 7 service", e);
        }

    }

    @Override
    protected void stopInfinispanCache(InfinispanCache cache) {

        try {

            Object delegate = cache.getDelegate();
            RemoteCache remoteCache = (RemoteCache)delegate;

            remoteCache.stop();
            remoteCache.getRemoteCacheManager().stop();
            cache = null;
        }
        catch(Throwable t) {

            log.warn("failed to stop cache " + cache);
            log.debug("cache stop failure cause", t);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
