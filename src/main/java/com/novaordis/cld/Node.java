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

package com.novaordis.cld;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Node
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static List<Node> toNodeList(String arg) throws Exception
    {
        List<Node> result = new ArrayList<Node>();

        for(StringTokenizer st = new StringTokenizer(arg, ","); st.hasMoreTokens(); )
        {
            String tok = st.nextToken();
            Node n;

            if (tok.toUpperCase().startsWith(EmbeddedNode.EMBEDDED_LABEL))
            {
                n = new EmbeddedNode(tok);
            }
            else
            {
                n = new Node(tok);
            }

            result.add(n);
        }
        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String host;
    private int port;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Node(String uparsedPair) throws Exception
    {
        int i = uparsedPair.indexOf(":");
        if (i == -1)
        {
            throw new Exception("no ':' found in \"" + uparsedPair + "\"");
        }

        this.host = uparsedPair.substring(0, i);
        this.port = Integer.parseInt(uparsedPair.substring(i + 1));
    }

    public Node(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public String toString()
    {
        return host + ":" + port;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
