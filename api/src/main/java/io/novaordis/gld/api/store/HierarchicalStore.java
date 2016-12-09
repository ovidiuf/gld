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

package io.novaordis.gld.api.store;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.utilities.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

/**
 * A store that stores each key/value pair in its own separated file. The files are maintained in a hierarchical
 * directory structure to avoid overloading the file table of a single flat directory in case of a large number of keys.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/8/16
 */
public class HierarchicalStore implements KeyStore {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String STORY_TYPE_LABEL = "hierarchical";
    public static final String DIRECTORY_CONFIGURATION_LABEL = "directory";

    private static final Logger log = LoggerFactory.getLogger(HierarchicalStore.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static byte[] toSha1(String s) throws Exception {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        sha1.reset();

        byte[] bytes = s.getBytes("utf8");

        sha1.update(bytes);

        //noinspection UnnecessaryLocalVariable
        byte[] hash = sha1.digest();
        return hash;
    }

    /**
     * TODO - inefficient, can be done better.
     */
    public static String toHex(byte[] buffer) {

        StringBuilder sb = new StringBuilder();

        for(byte b: buffer) {

            int i = b & 0x00F0;
            i = i >>> 4;
            sb.append(Integer.toHexString(i));

            i = b & 0x000F;
            sb.append(Integer.toHexString(i));
        }

        return sb.toString();
    }

    public static String getFirstLevelDirectory(String fortyDigitKeyHexSha1) {

        return fortyDigitKeyHexSha1.substring(0, 2);
    }

    public static String getSecondLevelDirectory(String fortyDigitKeyHexSha1) {

        return fortyDigitKeyHexSha1.substring(2, 4);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private File directory;

    private volatile boolean started;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param directory it is OK if the directory does not exist, the instance will deal with that at start() time. It
     *                  cannot be null, though.
     *
     * @exception IllegalArgumentException
     */
    public HierarchicalStore(File directory) {

        if (directory == null) {
            throw new IllegalArgumentException("null directory");
        }

        this.directory = directory;
    }

    // KeyStore implementation -----------------------------------------------------------------------------------------

    @Override
    public void start() throws KeyStoreException {

        if (started) {

            return;
        }

        if (directory.isFile()) {

            throw new KeyStoreException(directory + " is a file, it should have been a directory");
        }

        if (!directory.isDirectory()) {

            //
            // verify if the directory exists; if it doesn't, it will attempt to create it, but only if the parent
            // exists
            //

            File parent = directory.getParentFile();

            if (!parent.isDirectory()) {

                throw new KeyStoreException("parent directory " + parent + " does not exist");
            }

            if (!directory.mkdir()) {

                throw new KeyStoreException("failed to create directory " + directory);
            }

            log.debug(directory + " created");
        }

        started = true;
    }

    @Override
    public void stop() throws KeyStoreException {

        if (!started) {

            return;
        }

        started = false;
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public void store(String key, byte[]... v) throws KeyStoreException {

        File keyFile = getKeyLocation(key);
        writeKeyValue(keyFile, key, new Value(v));
    }

    @Override
    public Value retrieve(String key) throws KeyStoreException {

        File keyFile = getKeyLocation(key);

        if (!keyFile.isFile()) {

            return NullValue.INSTANCE;
        }

        byte[] content;

        try {

            content = Files.readBytes(keyFile);
        }
        catch (Exception e) {

            throw new KeyStoreException(e);
        }

        int i = 0;

        for(; i < content.length; i ++) {

            if ((byte)'\n' == content[i]) {
                break;
            }
        }

        if (i == content.length) {

            throw new IllegalStateException("key file format error - no new line: " + keyFile);
        }

        String storedKey = new String(content, 0, i);

        if (!key.equals(storedKey)) {

            throw new IllegalArgumentException("key file format error - key value mismatch: " + keyFile);
        }

        byte[] value = new byte[content.length - i - 1];
        System.arraycopy(content, i + 1, value, 0, value.length);
        return new Value(value);
    }

    @Override
    public Set<String> getKeys() throws KeyStoreException {

        Set<String> keys = new HashSet<>();

        File[] firstLevel = directory.listFiles();

        long t0 = System.currentTimeMillis();

        System.out.println("loading keys in memory ...");

        //noinspection ConstantConditions
        for(File d: firstLevel) {

            if (d.isDirectory()) {

                File[] secondLevel = d.listFiles();

                //noinspection ConstantConditions
                for(File d2: secondLevel) {

                    if (d2.isDirectory()) {

                        File[] files = d2.listFiles();

                        //noinspection ConstantConditions
                        for(File f: files) {

                            if (f.isFile()) {

                                BufferedReader br = null;

                                try {

                                    br = new BufferedReader(new FileReader(f));
                                    String key = br.readLine();
                                    keys.add(key);
                                }
                                catch(Exception e) {

                                    throw new KeyStoreException(e);
                                }
                                finally {

                                    if (br != null) {

                                        try {

                                            br.close();
                                        }
                                        catch (Exception e) {

                                            log.warn("failed to close " + f + " reader");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        long elapsedSeconds = (System.currentTimeMillis() - t0) / 1000;
        System.out.println(keys.size() +
                " keys loaded in " + (elapsedSeconds == 0 ? "less than a second" : elapsedSeconds + " seconds"));

        return keys;
    }

    @Override
    public long getKeyCount() {

        throw new RuntimeException("getKeyCount() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the directory in which the hierarchy is stored.
     */
    public File getDirectory() {

        return directory;
    }

    @Override
    public String toString() {

        return directory == null ? "null" : directory.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    File getKeyLocation(String key) throws KeyStoreException {

        byte[] keySha1;

        try {

            keySha1 = toSha1(key);
        }
        catch (Exception e) {

            throw new KeyStoreException(e);
        }

        String keyHexSha1 = toHex(keySha1);

        String firstLevelDirName = getFirstLevelDirectory(keyHexSha1);
        String secondLevelDirName = getSecondLevelDirectory(keyHexSha1);
        String fileName = keyHexSha1 + ".txt";

        return new File(directory, firstLevelDirName + "/" + secondLevelDirName + "/" + fileName);
    }

    /**
     * Writes the key/value pair into a file into the target directory. The file name is the SHA1 has of the key (we
     * trust that the SHA1 value is correct, and we don't check, for performance reasons).
     *
     * @param key - not allowed to contain '\n'. If it does contain '\n', we fail with IllegalArgumentException.
     * @param value can contain a valid byte[], null (which means the target service did not return a value associated
     *              with the key) or it can be "NOT_STORED", which means the API user chose not to store the value.
     *              Must be never null, it it is null the method will throw an IllegalArgumentException
     *
     * @return the written file
     *
     * @exception IllegalArgumentException on null key, invalid key (see above) or null value.
     *
     */
    static File writeKeyValue(File keyFile, String key, Value value) throws KeyStoreException {

        if (key == null) {

            throw new IllegalArgumentException("null key");
        }

        if (value == null) {

            throw new IllegalArgumentException("null value");
        }

        if (key.indexOf('\n') != -1) {

            throw new IllegalArgumentException("key containing new line characters: " + key);
        }

        File storageDir = keyFile.getParentFile();

        if (!storageDir.isDirectory()) {

            boolean directoryCreated = Files.mkdir(storageDir);

            if (!directoryCreated) {

                throw new IllegalArgumentException("failed to create directory " + storageDir);
            }
        }

        // what if the file exists already

        if (keyFile.exists()) {

            log.warn(keyFile + " exists and it will be overwritten");
        }

        try {

            byte[] keyBytes = key.getBytes("utf8");
            byte[] valueBytes = value.getBytes();
            byte[] buffer = new byte[keyBytes.length + valueBytes.length + 1]; // '\n' between key and value

            System.arraycopy(keyBytes, 0, buffer, 0, keyBytes.length);
            System.arraycopy(valueBytes, 0, buffer, keyBytes.length + 1, valueBytes.length);
            buffer[keyBytes.length] = (byte) '\n';

            FileOutputStream fos = new FileOutputStream(keyFile);
            fos.write(buffer);
            fos.close();
        }
        catch(Exception e) {

            throw new KeyStoreException(e);
        }

        return keyFile;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
