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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/8/17
 */
public class MockNamingEnumeration<T> implements NamingEnumeration<T> {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockNamingEnumeration(String contextName) {
    }

    // NamingEnumeration implementation --------------------------------------------------------------------------------

    @Override
    public T next() throws NamingException {

        throw new RuntimeException("next() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean hasMore() throws NamingException {

        throw new RuntimeException("hasMore() NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws NamingException {

        throw new RuntimeException("close() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean hasMoreElements() {

        throw new RuntimeException("hasMoreElements() NOT YET IMPLEMENTED");
    }

    @Override
    public T nextElement() {

        throw new RuntimeException("nextElement() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
