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

package io.novaordis.gld.driver.todeplete.storage;

import io.novaordis.gld.api.KeyStore;
import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.UserErrorException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A strategy that stores each key/value pair in its own separated file. The files are maintained in a hierarchical
 * directory structure to avoid overloading the file table of a single flat directory in case of a large number of keys.
 */
public class HierarchicalStorageStrategy extends StorageStrategyBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(HierarchicalStorageStrategy.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static byte[] toSha1(String s) throws Exception
    {
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
    public static String toHex(byte[] buffer)
    {
        StringBuilder sb = new StringBuilder();

        for(byte b: buffer)
        {
            int i = b & 0x00F0;
            i = i >>> 4;
            sb.append(Integer.toHexString(i));

            i = b & 0x000F;
            sb.append(Integer.toHexString(i));
        }

        return sb.toString();
    }

    public static String getFirstLevelDirectory(String fortyDigitKeyHexSha1)
    {
        return fortyDigitKeyHexSha1.substring(0, 2);
    }

    public static String getSecondLevelDirectory(String fortyDigitKeyHexSha1)
    {
        return fortyDigitKeyHexSha1.substring(2, 4);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String rootDirectoryName;

    private File rootDirectory;

    // Constructors ----------------------------------------------------------------------------------------------------

    // StorageStrategy implementation ----------------------------------------------------------------------------------

    /**
     * @see StorageStrategy#configure(Configuration, java.util.List, int)
     */
    @Override
    public void configure(Configuration configuration, List<String> arguments, int from) throws Exception
    {
        super.configure(configuration, arguments, from);

        for(int i = from; i < arguments.size(); i ++)
        {
            String crt = arguments.get(i);

            if ("--root".equals(crt))
            {
                arguments.remove(crt);

                if (i >= arguments.size())
                {
                    throw new UserErrorException("a directory name must follow --root");
                }

                rootDirectoryName = arguments.remove(i);
            }
        }

        if (rootDirectoryName == null)
        {
            throw new UserErrorException(
                "Hierarchical storage strategy invalid configuration: missing root directory name, use --root <dir-name>");
        }
    }

    @Override
    public boolean isConfigured()
    {
        return rootDirectoryName != null;
    }

    /**
     * @see StorageStrategy#start()
     */
    @Override
    public void start() throws Exception
    {
        if (!isConfigured())
        {
            throw new IllegalStateException(this + " not configured");
        }

        //
        // TODO to review this
        //

        // figure out whether we're going to be reading, writing or both

        Configuration c = getConfiguration();

        //LoadStrategy loadStrategy = c.getLoadStrategy();
        LoadStrategy loadStrategy = null;
        if (loadStrategy == null) {
            throw new RuntimeException("RETURN HERE");
        }


        if (loadStrategy != null)
        {
            KeyStore ks = loadStrategy.getKeyStore();
            if (ks.isReadOnly())
            {
                setWrite(false);
            }
        }
//        else if (c.getCommand() instanceof Content)
//        {
//            setWrite(true);
//            setRead(false);
//        }

        //
        //
        //

        // create the root directory if it does not exist

        rootDirectory = new File(rootDirectoryName);

        if (rootDirectory.exists())
        {
            if (rootDirectory.isDirectory())
            {
                if (isWrite())
                {
                    // this mean the strategy can be used to write, so we are overly cautious and we don't
                    // overwrite the content of the directory unless we're forced
                    throw new UserErrorException(rootDirectory + " already exists, and because the load strategy " + loadStrategy + " is allowed to write content locally, we stop here from excess of caution");
                }
            }
            else
            {
                throw new UserErrorException(rootDirectory + " exists and it is not a directory");
            }
        }
        else
        {
            // create the directory, but only if we're going to write
            if (isWrite())
            {
                boolean rootCreated = Files.mkdir(rootDirectory);

                if (!rootCreated)
                {
                    throw new UserErrorException("failed to create root directory " + rootDirectory);
                }
            }

            if (isRead())
            {
                throw new UserErrorException("root directory " + rootDirectory + " does not exist");
            }
        }
    }

    @Override
    public void stop() throws Exception
    {
        rootDirectory = null;
    }

    @Override
    public boolean isStarted()
    {
        return rootDirectory != null;
    }

    /**
     * @see StorageStrategy#store(String, String)
     */
    @Override
    public void store(String key, String value) throws Exception
    {
        File keyFile = getKeyLocation(key);

        writeKeyValue(keyFile, key, value);
    }

    /**
     * @see StorageStrategy#retrieve(String)
     */
    @Override
    public String retrieve(String key) throws Exception
    {
        File keyFile = getKeyLocation(key);

        if (!keyFile.isFile())
        {
            return null;
        }

        String content = Files.read(keyFile);

        int i = content.indexOf('\n');

        if (i == -1)
        {
            throw new IllegalStateException("key file format error - no new line: " + keyFile);
        }

        String storedKey = content.substring(0, i);

        if (!key.equals(storedKey))
        {
            throw new IllegalArgumentException("key file format error - key value mismatch: " + keyFile);
        }

        return content.substring(i + 1);
    }

    @Override
    public Set<String> getKeys() throws Exception
    {
        Set<String> keys = new HashSet<>();

        File[] firstLevel = rootDirectory.listFiles();

        long t0 = System.currentTimeMillis();
        System.out.println("loading keys in memory ...");

        //noinspection ConstantConditions
        for(File d: firstLevel)
        {
            if (d.isDirectory())
            {
                File[] secondLevel = d.listFiles();

                //noinspection ConstantConditions
                for(File d2: secondLevel)
                {
                    if (d2.isDirectory())
                    {
                        File[] files = d2.listFiles();

                        //noinspection ConstantConditions
                        for(File f: files)
                        {
                            if (f.isFile())
                            {
                                BufferedReader br = null;

                                try
                                {
                                    br = new BufferedReader(new FileReader(f));
                                    String key = br.readLine();
                                    keys.add(key);
                                }
                                finally
                                {
                                    if (br != null)
                                    {
                                        br.close();;
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


    // Public ----------------------------------------------------------------------------------------------------------

    public String getRootDirectoryName()
    {
        return rootDirectoryName;
    }

    private File getKeyLocation(String key) throws Exception
    {
        byte[] keySha1 = toSha1(key);
        String keyHexSha1 = toHex(keySha1);

        String firstLevelDirName = getFirstLevelDirectory(keyHexSha1);
        String secondLevelDirName = getSecondLevelDirectory(keyHexSha1);
        String fileName = keyHexSha1 + ".txt";

        return new File(rootDirectory, firstLevelDirName + "/" + secondLevelDirName + "/" + fileName);
    }


    @Override
    public String toString()
    {
        return "HierarchicalStorageStrategy[" + rootDirectoryName + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Writes the key/value pair into a file into the target directory. The file name is the SHA1 has of the key (we
     * trust that the SHA1 value is correct, for performance reasons).
     *
     * @param key - not allowed to contain '\n'. If it does contain '\n', we fail with IllegalArgumentException.
     *
     * @return the written file
     *
     */
    static File writeKeyValue(File keyFile, String key, String value) throws Exception
    {
        if (key == null)
        {
            throw new IllegalArgumentException("null key");
        }

        if (key.indexOf('\n') != -1)
        {
            throw new IllegalArgumentException("key containing new line characters: " + key);
        }

        File storageDir = keyFile.getParentFile();

        if (!storageDir.isDirectory())
        {
            boolean directoryCreated = Files.mkdir(storageDir);

            if (!directoryCreated)
            {
                throw new IllegalArgumentException("failed to create directory " + storageDir);
            }
        }

        // what if the file exists already

        if (keyFile.exists())
        {
            log.warn(keyFile + " exists and it will be overwritten");
        }

        byte[] keyBytes = key.getBytes("utf8");
        byte[] valueBytes = value.getBytes("utf8");
        byte[] buffer = new byte[keyBytes.length + valueBytes.length + 1]; // '\n' between key and value

        System.arraycopy(keyBytes, 0, buffer, 0, keyBytes.length);
        System.arraycopy(valueBytes, 0, buffer, keyBytes.length + 1, valueBytes.length);
        buffer[keyBytes.length] = (byte)'\n';

        FileOutputStream fos = new FileOutputStream(keyFile);
        fos.write(buffer);
        fos.close();

        return keyFile;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------


    // Inner classes ---------------------------------------------------------------------------------------------------


}
