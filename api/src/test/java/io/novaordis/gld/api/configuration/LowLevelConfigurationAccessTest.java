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

package io.novaordis.gld.api.configuration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class LowLevelConfigurationAccessTest extends LowLevelConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LowLevelConfigurationAccessTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void get_NoMatch_FirstElement() throws Exception {

        Map<String, Object> m = new HashMap<>();
        LowLevelConfiguration c = getLowLevelConfigurationToTest(m);

        String s = c.get(String.class, "no-such-top-element");
        assertNull(s);
    }

    @Test
    public void get_NoMatch_PartialMatch() throws Exception {

        Map<String, Object> m = new HashMap<>();
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Object> m2 = new HashMap<>();

        m.put("token1", m2);

        LowLevelConfiguration c = getLowLevelConfigurationToTest(m);

        String s = c.get(String.class, "token1", "token2");
        assertNull(s);
    }

    @Test
    public void get_NoMatch_IntermediateElementNotAMap() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", "a-string-not-a-map");

        LowLevelConfiguration c = getLowLevelConfigurationToTest(m);

        String s = c.get(String.class, "token1", "token2", "token3");
        assertNull(s);
    }

    @Test
    public void get_Match_NotTheExpectedType() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", m3);
        m3.put("token3", 10);

        LowLevelConfiguration c = getLowLevelConfigurationToTest(m);

        try {
            c.get(String.class, "token1", "token2", "token3");
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("expected token1.token2.token3 to be a String but it is a(n) Integer", msg);
        }
    }

    @Test
    public void get() throws Exception {

        Map<String, Object> m = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        Map<String, Object> m3 = new HashMap<>();
        m.put("token1", m2);
        m2.put("token2", m3);
        m3.put("token3", "test-value");

        LowLevelConfiguration c = getLowLevelConfigurationToTest(m);

        String s = c.get(String.class, "token1", "token2", "token3");

        assertEquals("test-value", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected LowLevelConfigurationAccess getLowLevelConfigurationToTest(Map<String, Object> raw) throws Exception {

        return new LowLevelConfigurationAccess(raw);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
