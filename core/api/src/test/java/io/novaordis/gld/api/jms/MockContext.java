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

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/6/17
 */
public class MockContext implements Context {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String providerUrl;

    private boolean listFails;

    private Map<String, Object> jndiSpace;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockContext(String providerUrl, Map<String, Object> jndiSpace) {

        this.providerUrl = providerUrl;
        this.jndiSpace = jndiSpace;
        this.listFails = true;
    }

    // Context implementation ------------------------------------------------------------------------------------------

    @Override
    public Object lookup(Name name) throws NamingException {
        throw new RuntimeException("lookup() NOT YET IMPLEMENTED");
    }

    @Override
    public Object lookup(String name) throws NamingException {

        Object o = jndiSpace.get(name);

        if (o == null) {

            throw new NameNotFoundException();
        }

        return o;
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        throw new RuntimeException("bind() NOT YET IMPLEMENTED");
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        throw new RuntimeException("bind() NOT YET IMPLEMENTED");
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        throw new RuntimeException("rebind() NOT YET IMPLEMENTED");
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        throw new RuntimeException("rebind() NOT YET IMPLEMENTED");
    }

    @Override
    public void unbind(Name name) throws NamingException {
        throw new RuntimeException("unbind() NOT YET IMPLEMENTED");
    }

    @Override
    public void unbind(String name) throws NamingException {
        throw new RuntimeException("unbind() NOT YET IMPLEMENTED");
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        throw new RuntimeException("rename() NOT YET IMPLEMENTED");
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        throw new RuntimeException("rename() NOT YET IMPLEMENTED");
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {

        if (listFails) {

            throw new NamingException("SYNTHETIC");
        }

        throw new RuntimeException("list() NOT YET IMPLEMENTED");
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {

        if (listFails) {

            throw new NamingException("SYNTHETIC");
        }

        return new NamingEnumeration<NameClassPair>() {

            @Override
            public NameClassPair next() throws NamingException {
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
            public NameClassPair nextElement() {
                throw new RuntimeException("nextElement() NOT YET IMPLEMENTED");
            }
        };
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {

        throw new RuntimeException("listBindings() NOT YET IMPLEMENTED");
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        throw new RuntimeException("listBindings() NOT YET IMPLEMENTED");
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        throw new RuntimeException("destroySubcontext() NOT YET IMPLEMENTED");
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        throw new RuntimeException("destroySubcontext() NOT YET IMPLEMENTED");
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        throw new RuntimeException("createSubcontext() NOT YET IMPLEMENTED");
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        throw new RuntimeException("createSubcontext() NOT YET IMPLEMENTED");
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        throw new RuntimeException("lookupLink() NOT YET IMPLEMENTED");
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        throw new RuntimeException("lookupLink() NOT YET IMPLEMENTED");
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        throw new RuntimeException("getNameParser() NOT YET IMPLEMENTED");
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        throw new RuntimeException("getNameParser() NOT YET IMPLEMENTED");
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new RuntimeException("composeName() NOT YET IMPLEMENTED");
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        throw new RuntimeException("composeName() NOT YET IMPLEMENTED");
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        throw new RuntimeException("addToEnvironment() NOT YET IMPLEMENTED");
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new RuntimeException("removeFromEnvironment() NOT YET IMPLEMENTED");
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new RuntimeException("getEnvironment() NOT YET IMPLEMENTED");
    }

    @Override
    public void close() throws NamingException {
        throw new RuntimeException("close() NOT YET IMPLEMENTED");
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        throw new RuntimeException("getNameInNamespace() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getProviderUrl() {

        return providerUrl;
    }

    public void setListFails(boolean b) {

        this.listFails = b;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
