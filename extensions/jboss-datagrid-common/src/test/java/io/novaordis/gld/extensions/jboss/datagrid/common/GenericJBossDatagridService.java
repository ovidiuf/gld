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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic implementation that mocks JBoss-specific aspects but allows testing the JBossDatagridServiceBase
 * functionality.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/19/17
 */
public class GenericJBossDatagridService extends JBossDatagridServiceBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(GenericJBossDatagridService.class);

    public static final String DEFAULT_CACHE_NAME = "N2ETG-4H34W-4534H";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private RuntimeException configureAndStartInfinispanCacheException;

    // Constructors ----------------------------------------------------------------------------------------------------

    // JBossDatagridServiceBase implementation -------------------------------------------------------------------------

    @Override
    protected InfinispanCache configureAndStartInfinispanCache() throws Exception {

        if (configureAndStartInfinispanCacheException != null) {

            throw configureAndStartInfinispanCacheException;
        }


        return new MockInfinispanCache(DEFAULT_CACHE_NAME);
    }

    @Override
    protected void stopInfinispanCache(InfinispanCache cache) {

        //
        // noop
        //
        log.info("mock stop " + cache);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void makeConfigureAndStartInfinispanCacheFail(RuntimeException e) {

        this.configureAndStartInfinispanCacheException = e;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
