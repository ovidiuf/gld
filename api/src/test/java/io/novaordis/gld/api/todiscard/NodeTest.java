/*
 * Copyright (c) 2015 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api.todiscard;

import org.slf4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(NodeTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void embeddedUppercase() throws Exception
    {
        List<Node> nl = Node.toNodeList("EMBEDDED");
        assertEquals(1, nl.size());
        assertTrue(nl.get(0) instanceof EmbeddedNode);
    }

    @Test
    public void embeddedLowercase() throws Exception
    {
        List<Node> nl = Node.toNodeList("embedded");
        assertEquals(1, nl.size());
        assertTrue(nl.get(0) instanceof EmbeddedNode);
    }

    @Test
    public void embeddedWithPortThatWillBeIgnored() throws Exception
    {
        List<Node> nl = Node.toNodeList("embedded:10001");
        assertEquals(1, nl.size());
        Node n = nl.get(0);
        assertTrue(n instanceof EmbeddedNode);
        assertEquals(0, n.getPort());
    }

    @Test
    public void embeddedWithPortThatWillBeIgnored2() throws Exception
    {
        List<Node> nl = Node.toNodeList("embedded:10001,localhost:10002");
        assertEquals(2, nl.size());

        Node n = nl.get(0);
        assertTrue(n instanceof EmbeddedNode);
        assertEquals(0, n.getPort());

        Node n2 = nl.get(1);
        assertEquals("localhost", n2.getHost());
        assertEquals(10002, n2.getPort());
    }

    @Test
    public void embedded_PrePopulated() throws Exception
    {
        List<Node> nl = Node.toNodeList("embedded[5]");
        assertEquals(1, nl.size());

        EmbeddedNode n = (EmbeddedNode)nl.get(0);

        assertEquals(5, n.getCapacity());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
