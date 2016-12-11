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

package io.novaordis.gld.api.store;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.StoreConfiguration;
import io.novaordis.utilities.UserErrorException;

import java.io.File;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class KeyStoreFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static KeyStore build(StoreConfiguration sc) throws Exception {

        if (sc == null) {

            throw new IllegalArgumentException("null store configuration");
        }

        String type = sc.getStoreType();

        if (type == null) {

            throw new UserErrorException("missing key store type");
        }

        //
        // manual look up - we may want to replace with reflection-based initialization
        //

        if (HierarchicalStore.STORY_TYPE_LABEL.equals(type)) {

            //
            // 'hierarchical'
            //

            //
            // do the logic of extracting the needed configuration here, it could probably be done in the
            // store instance itself
            //

            File directory = sc.getFile(HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL);

            if (directory == null) {

                throw new UserErrorException(
                        "missing \"" + HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL +
                                "\" hierarchical key store configuration element");
            }

            return new HierarchicalStore(directory);
        }
        else {

            //
            // we interpret the store type as the fully qualified class name of the implementation and we attempt
            // to instantiate it via reflection
            //

            Object keyStore;

            try {

                Class c = Class.forName(type);

                keyStore = c.newInstance();

            }
            catch(Exception e) {

                throw new UserErrorException("unknown key store type \"" + type + "\"", e);
            }

            if (!(keyStore instanceof KeyStore)) {

                throw new UserErrorException("\"" + type + "\" cannot be used to instantiate a KeyStore");

            }

            return (KeyStore)keyStore;
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
