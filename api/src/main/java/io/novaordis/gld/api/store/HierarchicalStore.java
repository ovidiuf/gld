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
import java.io.UnsupportedEncodingException;
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

    public static final byte[] NOT_STORED_MARKER =
            ((char)2 + "" + (char)3 + "NOT_STORED (do not edit this manually as it contains special characters)").
                    getBytes();

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

    static byte[] toFileContent(String key, StoredValue value) throws KeyStoreException {

        byte[] keyBytes;

        try {

            keyBytes = key.getBytes("utf8");
        }
        catch(UnsupportedEncodingException e) {

            throw new KeyStoreException(e);
        }

        byte[] valueBytes = value.getBytes();

        int length = keyBytes.length;

        if (value.notStored()) {

            length += 1 + NOT_STORED_MARKER.length; // '\n' between key and value
            valueBytes = NOT_STORED_MARKER;
        }
        else if (!value.isNull()) {

            length += 1 + valueBytes.length; // '\n' between key and value
        }

        byte[] content = new byte[length];

        System.arraycopy(keyBytes, 0, content, 0, keyBytes.length);

        if (!value.isNull()) {

            content[keyBytes.length] = (byte) '\n';
            System.arraycopy(valueBytes, 0, content, keyBytes.length + 1, valueBytes.length);
        }

        return content;
    }

    /**
     * The reverse of toFileContent().
     *
     * @see HierarchicalStore#toFileContent(String, StoredValue)
     */
    static KeyValuePair fromFileContent(byte[] content) {

        int i = 0;

        for(; i < content.length; i ++) {

            if ((byte)'\n' == content[i]) {
                break;
            }
        }

        KeyValuePair p = new KeyValuePair();

        if (i == content.length) {

            //
            // '\n' not found, that is a null value stored
            //

            p.setKey(new String(content));
            return p;
        }

        String key = new String(content, 0, i);
        p.setKey(key);

        outer: if (content.length == i + 1 + NOT_STORED_MARKER.length) {

            int offset = i + 1;
            for(int j = 0; j < content.length - offset; j ++) {

                if (NOT_STORED_MARKER[j] != content[j + offset]) {

                    break outer;
                }
            }

            //
            // NOT_STORED marker
            //

            p.setValue(NotStored.INSTANCE);
            return p;
        }

        //
        // normal value
        //

        byte[] value = new byte[content.length - i - 1];
        System.arraycopy(content, i + 1, value, 0, value.length);
        StoredValue v =  StoredValue.getInstance(value);
        p.setValue(v);
        return p;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private File directory;

    private volatile boolean started;

    private boolean overwrite;

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
        this.overwrite = false;
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
        else {

            //
            // the directory exists and starting and operating this store will most likely overwrite the content,
            // so protect against that
            //

            if (!isOverwrite()) {

                throw new KeyStoreException(
                        "directory " + directory + " already exists and the store is not configured to overwrite it");
            }
        }

        started = true;

        log.debug(this + " started");
    }

    @Override
    public void stop() throws KeyStoreException {

        if (!started) {

            return;
        }

        started = false;

        log.debug(this + " stopped");
    }

    @Override
    public boolean isStarted() {

        return started;
    }

    @Override
    public void store(String key, byte[]... v) throws KeyStoreException {

        File keyFile = getKeyLocation(key);
        writeKeyValue(keyFile, key, StoredValue.getInstance(v));
    }

    @Override
    public StoredValue retrieve(String key) throws KeyStoreException {

        File keyFile = getKeyLocation(key);

        if (!keyFile.isFile()) {

            return Null.INSTANCE;
        }

        byte[] content;

        try {

            content = Files.readBytes(keyFile);
        }
        catch (Exception e) {

            throw new KeyStoreException(e);
        }

        KeyValuePair p = fromFileContent(content);

        if (!key.equals(p.getKey())) {

            throw new IllegalArgumentException("key file format error - key value mismatch: " + keyFile);
        }

        return p.getValue();
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

    /**
     * Inefficient implementation, we call getKeys() underneath. We should implement something better.
     */
    @Override
    public long getKeyCount() throws KeyStoreException {

        return getKeys().size();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the directory in which the hierarchy is stored.
     */
    public File getDirectory() {

        return directory;
    }

    /**
     * @return true if the store will overwrite the content of an existing directory, at startup. If this method
     * return false, and the directory exists, start() will fail.
     */
    public boolean isOverwrite() {

        return overwrite;
    }

    /**
     * @see HierarchicalStore#isOverwrite()
     */
    public void setOverwrite(boolean b) {

        this.overwrite = b;
    }

    @Override
    public String toString() {

        return directory == null ? "null" : "hierarchical store " + directory.toString();
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
    static File writeKeyValue(File keyFile, String key, StoredValue value) throws KeyStoreException {

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

            byte[] content = toFileContent(key, value);
            FileOutputStream fos = new FileOutputStream(keyFile);
            fos.write(content);
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
