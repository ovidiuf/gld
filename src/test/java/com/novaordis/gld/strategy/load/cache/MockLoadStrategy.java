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

package com.novaordis.gld.strategy.load.cache;

import com.novaordis.gld.Configuration;
import com.novaordis.gld.KeyStore;
import com.novaordis.gld.Operation;
import com.novaordis.gld.strategy.load.LoadStrategyBase;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * We keep this class in this package ("com.novaordis.gld.strategy.load") and not in the mock package
 * com.novaordis.gld.mock because, among other things, we test reflection-based instantiation, and for that we need
 * to be in certain packages.
 */
public class MockLoadStrategy extends LoadStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockLoadStrategy.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String mockArgument;
    private String mockLoadArgument;

    // Constructors ----------------------------------------------------------------------------------------------------

    // LoadStrategy implementation -------------------------------------------------------------------------------------

    @Override
    public void configure(Configuration config, List<String> arguments, int from) throws Exception
    {
        super.configure(config, arguments, from);

        for(int i = 0; i < arguments.size(); i ++)
        {
            if ("--mock-argument".equals(arguments.get(i)))
            {
                arguments.remove(i);
                mockArgument = arguments.remove(i --);
            }
            else if ("--mock-load-argument".equals(arguments.get(i)))
            {
                arguments.remove(i);
                mockLoadArgument = arguments.remove(i --);
            }
        }
    }

    @Override
    public Operation next(Operation lastOperation, String lastWrittenKey)
    {
        return new MockOperation();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setKeyStore(KeyStore ks)
    {
        super.setKeyStore(ks);
    }

    public String toString()
    {
        return "MockLoadStrategy[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    public String getMockArgument()
    {
        return mockArgument;
    }

    public String getMockLoadArgument()
    {
        return mockLoadArgument;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
