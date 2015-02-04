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

package com.novaordis.gld;

public class EmbeddedNode extends Node
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final String EMBEDDED_LABEL = "EMBEDDED";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int capacity;

    // Constructors ----------------------------------------------------------------------------------------------------

    public EmbeddedNode(String tok)
    {
        super(EMBEDDED_LABEL, 0);

        if (tok == null)
        {
            throw new IllegalArgumentException("null label");
        }

        if (!tok.toUpperCase().startsWith("EMBEDDED"))
        {
            throw new IllegalArgumentException("invalid embedded label: " + tok);
        }


        int i = tok.indexOf('[');
        int j = tok.indexOf(']');

        if (i != -1 && j != -1 && i < j)
        {
            String cs = tok.substring(i + 1, j);
            capacity = Integer.parseInt(cs);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public String toString()
    {
        return "EmbeddedNode(" + capacity + ")";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
