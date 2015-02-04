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

package com.novaordis.gld.mock;

import com.novaordis.gld.Operation;
import com.novaordis.gld.Statistics;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MockStatistics implements Statistics
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockStatistics.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private boolean weAreDone;

    private int recordsToGo;

    private List<OperationThrowablePair> recorded;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockStatistics()
    {
        this(true);
    }

    public MockStatistics(boolean weAreDone)
    {
        this.weAreDone = weAreDone;
        this.recordsToGo = -1;
        this.recorded = new ArrayList<>();
    }

    // Statistics implementation ---------------------------------------------------------------------------------------

    @Override
    public boolean areWeDone()
    {
        return weAreDone;
    }

    @Override
    public void record(long t0Ms, long t0Nano, long t1Nano, Operation op, Throwable t)
    {
        OperationThrowablePair p = new OperationThrowablePair(op, t);
        recorded.add(p);
        log.info("mock \"recorded\" " + p);

        if (recordsToGo != -1)
        {
            if (--recordsToGo == 0)
            {
                weAreDone = true;
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setDoneAfterNRecords(int countOfRecordsAfterWhichWeAreDone)
    {
        this.recordsToGo = countOfRecordsAfterWhichWeAreDone;
    }

    public List<OperationThrowablePair> getRecorded()
    {
        return recorded;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    public class OperationThrowablePair
    {
        public Operation operation;
        public Throwable throwable;

        OperationThrowablePair(Operation operation, Throwable throwable)
        {
            this.operation = operation;
            this.throwable = throwable;
        }

        @Override
        public String toString()
        {
            return "OperationThrowablePair[" + operation + "," + throwable + "]";
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
