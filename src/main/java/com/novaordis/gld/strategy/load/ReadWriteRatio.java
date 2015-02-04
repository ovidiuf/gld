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

package com.novaordis.gld.strategy.load;

import com.novaordis.gld.UserErrorException;

/**
 * In-line documentation (if the logic changes, also change the documentation):
<pre>

 --read-to-write <ratio>. The positive or zero integer read to write ratio. Default is 1:
 for each write there is a read. Zero means only writes, no reads. If you want more writes
 than reads, use --write-to-read (described below). --read-to-write and --write-to-read are
 mutually exclusive (unless they're both 1).

 --write-to-read <ratio>. The positive or zero integer write to read ratio. Default is 1:
 for each read there is a write. Zero means only reads, no writes. If you want more reads
 than writes, use --read-to-write (described above). --read-to-write and --write-to-read are
 mutually exclusive (unless they're both 1).

 </pre>

 The default behavior in absence of any configuration is: one write (isWrite() == true) followed by a series of 1 read.
 */
public class ReadWriteRatio
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean write;
    private int followUpSeriesSize;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param readToWrite - the command line string that follows the '--read-to-write' option. May be null if no
     *        '--read-to-write' is specified on command line.
     * @param writeToRead - the command line string that follows the '--read-to-write' option. May be null if no
     *        '--read-to-write' is specified on command line.
     */
    public ReadWriteRatio(String readToWrite, String writeToRead) throws Exception
    {
        this.write = true;
        this.followUpSeriesSize = 1;

        Integer rtw = null;
        Integer wtr = null;

        if (readToWrite != null)
        {
            try
            {
                rtw = new Integer(readToWrite);
            }
            catch(Exception e)
            {
                throw new UserErrorException("invalid --read-to-write value '" + readToWrite + "'");
            }

            if (rtw < 0)
            {
                throw new UserErrorException("only positive or zero integers can be --read-to-write ratios");
            }

        }

        if (writeToRead != null)
        {
            try
            {
                wtr = new Integer(writeToRead);
            }
            catch(Exception e)
            {
                throw new UserErrorException("invalid --write-to-read value '" + writeToRead + "'");
            }

            if (wtr < 0)
            {
                throw new UserErrorException("only positive or zero integers can be --write-to-read ratios");
            }

        }

        if (rtw != null)
        {
            if (rtw == 1)
            {
                write = true;
                followUpSeriesSize = 1;

                if (wtr != null && wtr != 1)
                {
                    throw new UserErrorException("incompatible --read-to-write/--write-to-read values: " + rtw + "/" + wtr);
                }

                return;
            }

            if (rtw == 0)
            {
                write = true;
                followUpSeriesSize = 0;
            }
            else
            {
                write = true;
                followUpSeriesSize = rtw;
            }

            if (wtr != null)
            {
                throw new UserErrorException("incompatible --read-to-write/--write-to-read values: " + rtw + "/" + wtr);
            }
        }
        else
        {
            if (wtr == null || wtr == 1)
            {
                write = true;
                followUpSeriesSize = 1;
            }
            else if (wtr == 0)
            {
                write = false;
                followUpSeriesSize = 0;
            }
            else
            {
                write = false;
                followUpSeriesSize = wtr;
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public boolean doesWritingTakePlace()
    {
        return write || followUpSeriesSize > 0;
    }

    @Override
    public String toString()
    {
        String s;
        if (write)
        {
            if (followUpSeriesSize == 0)
            {
                s = "writes only";
            }
            else
            {
                s = "write followed by " + (followUpSeriesSize == 1 ? "read" : followUpSeriesSize + " reads");
            }
        }
        else
        {
            if (followUpSeriesSize == 0)
            {
                s = "reads only";
            }
            else
            {
                s = "read followed by " + (followUpSeriesSize == 1 ? "write" : followUpSeriesSize + " writes");
            }
        }

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    boolean isRead()
    {
        return !write;
    }

    boolean isWrite()
    {
        return write;
    }

    int getFollowUpSeriesSize()
    {
        return followUpSeriesSize;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
