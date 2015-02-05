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

public enum ContentType
{
    KEYVALUE,
    MESSAGE;

    /**
     * @throws java.lang.IllegalArgumentException on null argument.
     * @throws UserErrorException
     */
    public static ContentType fromString(String s) throws UserErrorException
    {
        if (s == null)
        {
            throw new IllegalArgumentException("null content type");
        }

        String ucs = s.toUpperCase();

        try
        {
            return ContentType.valueOf(ucs);
        }
        catch(Exception e)
        {
            // we're fine for now, we'll try alternatives before completely bailing out
        }

        throw new UserErrorException("unknown content type '" + s + "'");
    }
}
