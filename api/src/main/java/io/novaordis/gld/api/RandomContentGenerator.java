/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.gld.api;

import java.util.Random;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/7/16
 */
public class RandomContentGenerator {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Assembles a random string by generating a shorter random sequence of 'randomSequenceLength' and then copying
     * the sequence for as many times as necessary to build the desired 'length' string. The implementation is naive,
     * come up with something smarter.
     *
     * @param random - the Random instance to use while generated the content. We are exposing it externally to give
     *        the caller a chance to provide an efficient Random (such as ThreadLocalRandom, which should be used in
     *               order to reduce thread contention).
     *
     * @param length - the total length of the string.
     *
     * @param randomSequenceLength - the length of the random section of the string. If randomSequenceLength
     *       is smaller than length, the final string consists in identically repeated sections; the section that
     *       will be repeated is 'randomSequenceLength' long and it is randomly generated.
     */
    public String getRandomString(Random random, int length, int randomSequenceLength) {

        String randomSection = "";

        int r;

        if (length < randomSequenceLength) {
            randomSequenceLength = length;
        }

        if (randomSequenceLength <= 0) {
            throw new IllegalArgumentException("invalid length " + randomSequenceLength);
        }

        for (int i = 0; i < randomSequenceLength; i ++) {

            r = random.nextInt(122);

            if (r >=0 && r <= 25) {
                r += 65;
            }
            else if (r >=26 && r <= 47) {
                r += 71;
            }
            else if (r >= 58 && r <= 64) {
                r += 10;
            }
            else if (r >= 91 && r <= 96) {
                r += 10;
            }

            randomSection += ((char)r);
        }

        if (length == randomSequenceLength) {
            return randomSection;
        }
        else if (length > randomSequenceLength) {

            char[] src = randomSection.toCharArray();
            int sections = length / randomSequenceLength;
            char[] buffer = new char[length];
            for(int i = 0; i < sections; i ++) {
                System.arraycopy(src, 0, buffer, i * randomSequenceLength, randomSequenceLength);
            }
            int rest = length - sections * randomSequenceLength;
            System.arraycopy(src, 0, buffer, sections * randomSequenceLength, rest);
            return new String(buffer);
        }
        else {

            throw new RuntimeException("NOT YET IMPLEMENTED getRandomString()");
        }
    }

    /**
     * @param random - the Random instance to use while generated the content. We are exposing it externally to give
     *        the caller a chance to provide an efficient Random (such as ThreadLocalRandom, which should be used in
     *               order to reduce thread contention).
     *
     * @param length the length of the string, in characters.
     */
    public String getRandomString(Random random, int length) {

        if (length <= 10) {

            return getRandomString(random, length, length);
        }
        else if (length <= 1024) {

            return getRandomString(random, length, 20);
        }
        else {

            return getRandomString(random, length, 50);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
