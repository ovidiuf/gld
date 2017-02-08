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

package io.novaordis.gld.extensions.jboss.eap.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/7/17
 */
public class MockInitialContextFactory implements InitialContextFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockInitialContextFactory.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void setValidJndiUrl(String s) {

        validJndiUrl = s;
    }

    public static String getValidJndiUrl() {

        return validJndiUrl;
    }

    public static void bind(String name, Object o) {

        content.put(name, o);
    }

    public static void reset() {

        validJndiUrl = null;
        content.clear();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private static String validJndiUrl;
    private static Map<String, Object> content = new HashMap<>();

    // Constructors ----------------------------------------------------------------------------------------------------

    // InitialContextFactory implementation ----------------------------------------------------------------------------

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {

        String s = (String)environment.get(Context.PROVIDER_URL);

        if (s == null) {

            fail("no '" + Context.PROVIDER_URL + "' found");
        }

        MockContext mc = new MockContext(s, content);

        log.info("created " + mc);

        return mc;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
