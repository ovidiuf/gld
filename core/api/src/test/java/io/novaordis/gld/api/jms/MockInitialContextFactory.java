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

package io.novaordis.gld.api.jms;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public class MockInitialContextFactory implements InitialContextFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static void reset() {

        validJndiUrl = null;
        listFails = false;
        jndiSpace.clear();
    }

    public static void setListFails(boolean b) {

        listFails = b;
    }

    /**
     * Installs a "JNDI object" into the JNDI space.
     */
    public static void install(String jndiName, Object o) {

        jndiSpace.put(jndiName, o);
    }

    public static Map<String, Object> getJndiSpace() {

        return jndiSpace;
    }

    public static void setValidJndiUrl(String s) {

        validJndiUrl = s;
    }

    public static String getValidJndiUrl() {

        return validJndiUrl;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private static boolean listFails;

    private static final Map<String, Object> jndiSpace = new Hashtable<>();

    private static String validJndiUrl;

    // Constructors ----------------------------------------------------------------------------------------------------

    // InitialContextFactory implementation ----------------------------------------------------------------------------

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {

        String providerUrl = (String)environment.get(Context.PROVIDER_URL);

        if (providerUrl == null) {

            fail("no '" + Context.PROVIDER_URL + "' found in the JNDI environment map");
        }

        //
        // we don't check here whether the JNDI URL is valid, it will be checked in MockContext operations
        //

        MockContext mc = new MockContext(providerUrl, jndiSpace);

        mc.setListFails(listFails);

        return mc;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
