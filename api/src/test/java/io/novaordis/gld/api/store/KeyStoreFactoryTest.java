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

import io.novaordis.gld.api.configuration.MockStoreConfiguration;
import io.novaordis.utilities.UserErrorException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class KeyStoreFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(KeyStoreFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void build_NullConfiguration() throws Exception {

        try {

            KeyStoreFactory.build(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("null store configuration", msg);
        }
    }

    @Test
    public void build_MissingStoreType() throws Exception {

        MockStoreConfiguration mc = new MockStoreConfiguration();
        mc.setStoreType(null);

        try {

            KeyStoreFactory.build(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("missing key store type", msg);
        }
    }

    @Test
    public void build_UnknownStoreType() throws Exception {

        MockStoreConfiguration mc = new MockStoreConfiguration();
        mc.setStoreType("i-am-sure-there-is-no-such-store-type");

        try {

            KeyStoreFactory.build(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("unknown key store type \"i-am-sure-there-is-no-such-store-type\"", msg);

            ClassNotFoundException cause = (ClassNotFoundException)e.getCause();
            assertNotNull(cause);
        }
    }

    @Test
    public void build_KnownStoryType() throws Exception {

        MockStoreConfiguration mc = new MockStoreConfiguration();

        mc.setStoreType(HierarchicalStore.STORY_TYPE_LABEL);
        mc.setPath(HierarchicalStore.DIRECTORY_CONFIGURATION_LABEL, ".");

        HierarchicalStore s = (HierarchicalStore)KeyStoreFactory.build(mc);

        assertFalse(s.isStarted());
    }

    @Test
    public void build_StoryInstanceSpecifiedByClassName_NotAKeyStore() throws Exception {

        MockStoreConfiguration mc = new MockStoreConfiguration();

        mc.setStoreType(Object.class.getName());


        try {

            KeyStoreFactory.build(mc);
            fail("should have thrown exception");
        }
        catch(UserErrorException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals( "\"" + Object.class.getName() + "\" cannot be used to instantiate a KeyStore", msg);
        }
    }

    @Test
    public void build_StoryInstanceSpecifiedByClassName() throws Exception {

        MockStoreConfiguration mc = new MockStoreConfiguration();

        mc.setStoreType(MockKeyStore.class.getName());

        MockKeyStore s = (MockKeyStore)KeyStoreFactory.build(mc);

        assertFalse(s.isStarted());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
