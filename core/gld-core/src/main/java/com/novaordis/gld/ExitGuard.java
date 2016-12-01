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

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Manages exit policy. Depending on the application configuration, it allows the invoking thread to continue, thus
 * allowing the application to exit, or it puts it on wait, pending the arrival of an exit condition (Ctrl-C, SIGTERM,
 * etc.)
 */
public class ExitGuard {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ExitGuard.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private CountDownLatch latch;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ExitGuard() {

        this.latch = new CountDownLatch(1);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void waitUntilExitIsAllowed() {

        try {

            log.debug("preventing exit until allowed");
            latch.await();
            log.debug("exit allowed");
        }
        catch(InterruptedException e) {
            throw new IllegalStateException("interrupted while awaiting to exit", e);
        }
    }

    public void allowExit() {

        log.debug(this + " configured to allow exit");
        latch.countDown();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
