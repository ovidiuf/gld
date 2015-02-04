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

public interface Command
{
    /**
     * Call it before executing the command. We separate initialize() from execute() because there are
     * cases where we may want to fail fast if the configuration is incorrect.
     *
     * This is where the command-specific argument processing and starting of resources should be done.
     *
     * Throw UserErrorException on configuration errors.
     *
     * @see Command#execute()
     *
     * @throws com.novaordis.gld.UserErrorException on configuration errors.
     */
    void initialize() throws Exception;

    boolean isInitialized();

    /**
     * Call initialize() first otherwise the method will throw an IllegalStateException.
     *
     * @see Command#initialize()
     *
     * @throws java.lang.IllegalStateException if the command was not initialized.
     */
    void execute() throws Exception;

    void addArgument(String arg);

    /**
     * @return whether this command is remote - interacts with a remote cache node - or it does not need to
     *         initiate a remote connections.
     */
    boolean isRemote();
}
