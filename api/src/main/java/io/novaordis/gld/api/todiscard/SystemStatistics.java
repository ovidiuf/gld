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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import com.sun.management.OperatingSystemMXBean;

public class SystemStatistics
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private OperatingSystemMXBean osStats;
    private MemoryMXBean memoryStats;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SystemStatistics()
    {
        osStats = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        memoryStats = ManagementFactory.getMemoryMXBean();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // OS --------------------------------------------------------------------------------------------------------------

    /**
     * Returns the load as a double value in the [0.0,1.0] interval, with 0.0 representing no load to 1.0 representing
     * 100% CPU load for the whole system.
     */
    public double getSystemCpuLoad()
    {
        return osStats.getSystemCpuLoad();
    }

    /**
     * Returns the load as a double value in the [0.0,1.0] interval, with 0.0 representing no load to 1.0 representing
     * 100% CPU load for the current JVM process.
     */
    public double getProcessCpuLoad()
    {
        return osStats.getProcessCpuLoad();
    }

    /**
     * Returns the system load average for the last minute. The system load average is the sum of the number of runnable
     * entities queued to the available processors and the number of runnable entities running on the available
     * processors averaged over a period of time. The way in which the load average is calculated is operating system
     * specific but is typically a damped time-dependent average.
     */
    public double getSystemLoadAverage()
    {
        return osStats.getSystemLoadAverage();
    }

    public long getProcessCpuTime()
    {
        return osStats.getProcessCpuTime();
    }

    public long getFreePhysicalMemorySize()
    {
        return osStats.getFreePhysicalMemorySize();
    }

    public long getTotalPhysicalMemorySize()
    {
        return osStats.getTotalPhysicalMemorySize();
    }

    public long getCommittedVirtualMemorySize()
    {
        return osStats.getCommittedVirtualMemorySize();
    }

    public long getTotalSwapSpaceSize()
    {
        return osStats.getTotalSwapSpaceSize();
    }

    public long getFreeSwapSpaceSize()
    {
        return osStats.getFreeSwapSpaceSize();
    }

    // Memory ----------------------------------------------------------------------------------------------------------

    /**
     * In bytes.
     */
    public long getHeapUsed()
    {
        return memoryStats.getHeapMemoryUsage().getUsed();
    }

    /**
     * In bytes.
     */
    public long getHeapCommitted()
    {
        return memoryStats.getHeapMemoryUsage().getCommitted();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
