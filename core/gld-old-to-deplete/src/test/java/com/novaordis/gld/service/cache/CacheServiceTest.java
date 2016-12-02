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

package com.novaordis.gld.service.cache;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.Node;
import com.novaordis.gld.mock.MockConfiguration;
import io.novaordis.gld.driver.ServiceTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 7/23/16
 */
public abstract class CacheServiceTest extends ServiceTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void defaultName() throws Exception {

        MockConfiguration mc = new MockConfiguration();
        CacheService s = getServiceToTest(mc, null);
        assertNull(s.getName());
    }

    @Test
    public void nonDefaultName() throws Exception {

        MockConfiguration mc = new MockConfiguration();
        CacheService s = getServiceToTest(mc, null);

        List<String> args = new ArrayList<>(Arrays.asList("something", "--cache", "test-name", "somethingelse"));
        s.configure(args);

        assertEquals("test-name", s.getName());
        assertEquals(2, args.size());
        assertEquals("something", args.get(0));
        assertEquals("somethingelse", args.get(1));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected abstract CacheService getServiceToTest(Configuration configuration, List<Node> nodes) throws Exception;

    @Override
    protected abstract Node getTestNode();

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
