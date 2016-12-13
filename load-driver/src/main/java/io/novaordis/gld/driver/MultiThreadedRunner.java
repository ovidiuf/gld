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

package io.novaordis.gld.driver;

import io.novaordis.gld.api.Runner;
import io.novaordis.utilities.time.Duration;

public interface MultiThreadedRunner extends Runner {

    /**
     * @see ExitGuard
     */
    ExitGuard getExitGuard();

    Duration getDuration();

    void setDuration(Duration d);

    boolean isWaitForConsoleQuit();

    void setWaitForConsoleQuit(boolean b);

}
