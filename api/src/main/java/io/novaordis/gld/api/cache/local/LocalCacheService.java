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

package io.novaordis.gld.api.cache.local;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.Operation;
import io.novaordis.gld.api.ServiceBase;
import io.novaordis.gld.api.cache.CacheService;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.gld.api.todiscard.Node;
import io.novaordis.gld.api.LoadDriver;
import io.novaordis.utilities.UserErrorException;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/2/16
 */
public class LocalCacheService extends ServiceBase implements CacheService {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------


    private volatile boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    public LocalCacheService(LoadStrategy loadStrategy, LoadDriver loadDriver) {

        super(loadStrategy, loadDriver);
    }

    // Service implementation ------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception {

        if (started) {

            return;
        }

        checkStateConsistency();

        started = true;
    }

    @Override
    public void stop() throws Exception {

        if (!started) {

            return;
        }

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    // execution -------------------------------------------------------------------------------------------------------

    @Override
    public void perform(Operation o) throws Exception {
        throw new RuntimeException("perform() NOT YET IMPLEMENTED");
    }


    @Override
    public void setConfiguration(Configuration c) {
        throw new RuntimeException("setConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setTarget(List<Node> nodes) {
        throw new RuntimeException("setTarget() NOT YET IMPLEMENTED");
    }

    @Override
    public void configure(List<String> commandLineArguments) throws UserErrorException {
        throw new RuntimeException("configure() NOT YET IMPLEMENTED");
    }

    @Override
    public ContentType getContentType() {
        throw new RuntimeException("getContentType() NOT YET IMPLEMENTED");
    }

    @Override
    public String get(String key) throws Exception {
        throw new RuntimeException("get() NOT YET IMPLEMENTED");
    }

    @Override
    public void put(String key, String value) throws Exception {
        throw new RuntimeException("put() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
