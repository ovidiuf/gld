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

import com.novaordis.gld.command.Connect;
import com.novaordis.gld.command.Content;
import com.novaordis.gld.command.Delete;
import com.novaordis.gld.command.GenerateKeys;
import com.novaordis.gld.command.Help;
import com.novaordis.gld.command.Load;
import com.novaordis.gld.command.Test;
import com.novaordis.gld.command.Version;
import com.novaordis.gld.service.cache.EmbeddedCacheService;
import com.novaordis.gld.service.jms.activemq.ActiveMQService;
import com.novaordis.gld.service.cache.infinispan.InfinispanService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationImpl implements Configuration
{
    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_THREAD_COUNT= 1;
    public static final int DEFAULT_MAX_TOTAL = 100;
    public static final long DEFAULT_MAX_WAIT_MILLIS = 1000L;
    public static final long DEFAULT_SLEEP = 0;

    public static final int DEFAULT_KEY_SIZE = 70;

    // based on production data analysis
    public static final int DEFAULT_VALUE_SIZE = 6000;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Command command;

    private int threads;
    private int maxTotal;
    private long maxWaitMillis;
    private long maxOperations;
    private long sleep;
    private int keySize;
    private int valueSize;
    private boolean useDifferentValues;
    private String output;
    private List<Node> nodes;
    private String password;
    private long keyExpirationSecs;
    private Properties configurationFileContent;
    private String exceptionFile;
    private String cacheName;
    private String keyStoreFile;
    private String username;

    private LoadStrategy loadStrategy;

    private StorageStrategy storageStrategy;

    private Service service;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ConfigurationImpl(String[] args) throws Exception
    {
        parseCommandLine(args);
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    /**
     * @see Configuration#getService()
     */
    @Override
    public Service getService()
    {
        return service;
    }

    /**
     * @see Configuration#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return command;
    }

    @Override
    public void setCommand(Command c)
    {
        this.command = c;
    }

    /**
     * @see Configuration#getThreads()
     */
    @Override
    public int getThreads()
    {
        return threads;
    }

    /**
     * @see Configuration#getMaxTotal()
     */
    @Override
    public int getMaxTotal()
    {
        return maxTotal;
    }

    /**
     * @see Configuration#getMaxWaitMillis()
     */
    @Override
    public long getMaxWaitMillis()
    {
        return maxWaitMillis;
    }

    /**
     * @see Configuration#getMaxOperations()
     */
    @Override
    public Long getMaxOperations()
    {
        return maxOperations;
    }

    /**
     * @see Configuration#getNodes()
     */
    @Override
    public List<Node> getNodes()
    {
        return nodes;
    }

    /**
     * @see Configuration#getSleep()
     */
    @Override
    public long getSleep()
    {
        return sleep;
    }

    /**
     * @see Configuration#getKeySize()
     */
    @Override
    public int getKeySize()
    {
        return keySize;
    }

    /**
     * @see Configuration#getValueSize()
     */
    @Override
    public int getValueSize()
    {
        return valueSize;
    }

    /**
     * @see Configuration#isUseDifferentValues()
     */
    @Override
    public boolean isUseDifferentValues()
    {
        return useDifferentValues;
    }

    /**
     * @see Configuration#getOutput()
     */
    @Override
    public String getOutput()
    {
        return output;
    }

    /**
     * @see Configuration#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }

    /**
     * @see Configuration#getKeyExpirationSecs()
     */
    @Override
    public long getKeyExpirationSecs()
    {
        return keyExpirationSecs;
    }

    /**
     * @see Configuration#getExceptionFile()
     */
    @Override
    public String getExceptionFile()
    {
        return exceptionFile;
    }

    /**
     * @see Configuration#getCacheName()
     */
    @Override
    public String getCacheName()
    {
        return cacheName;
    }

    /**
     * @see Configuration#getKeyStoreFile()
     */
    @Override
    public String getKeyStoreFile()
    {
        return keyStoreFile;
    }

    @Override
    public LoadStrategy getLoadStrategy()
    {
        return loadStrategy;
    }

    @Override
    public void setLoadStrategy(LoadStrategy ls)
    {
        this.loadStrategy = ls;
    }

    @Override
    public StorageStrategy getStorageStrategy()
    {
        return storageStrategy;
    }

    @Override
    public void setStorageStrategy(StorageStrategy storageStrategy)
    {
        this.storageStrategy = storageStrategy;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return
            "Configuration: " +
                "cacheName=" + cacheName + ", " +
                "threads=" + threads + ", " +
                "maxTotal="  + maxTotal + ", " +
                "maxWaitMillis="  + maxWaitMillis + ", " +
                "maxOperations=" + (maxOperations == -1 ? "UNLIMITED" : maxOperations) + ", " +
                "sleep=" + (sleep > 0 ? (sleep + " ms") : "no") + ", " +
                "keySize=" + keySize + ", " +
                "valueSize=" + valueSize + ", " +
                "expiration=" + (keyExpirationSecs == -1L ? "NEVER" : keyExpirationSecs + " sec(s)") + ", " +
                "useDifferentValues=" + useDifferentValues + ", " +
                "output=" + output + ", " +
                "password=" + (password == null ? "N/A" : "***") + ", " +
                "cache service=" + service;

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    private void parseCommandLine(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            command = new Help(this);
            command.initialize();
            return;
        }

        // defaults

        threads = -1;
        maxTotal = -1;
        maxWaitMillis = -1L;
        maxOperations = -1L; // unlimited
        sleep = -1L;
        keySize = -1;
        valueSize = -1;
        useDifferentValues = false;

        StringBuilder nodesSb = null;
        String nodesString = null;

        // default - don't expire
        keyExpirationSecs = -1L;

        boolean hasPassword = false;

        String proxyString = null;

        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        for(int i = 0; i < arguments.size(); i ++)
        {
            String crt = arguments.get(i);

            if (nodesSb != null)
            {
                if (crt.startsWith("--"))
                {
                    nodesString = nodesSb.toString();
                    nodesSb = null;
                }
                else
                {
                    nodesSb.append(crt);
                    continue;
                }
            }

            if ("--help".equals(crt) || "help".equals(crt))
            {
                command = new Help(this);
                return;
            }
            else if ("version".equals(crt))
            {
                command = new Version(this);
                command.initialize();
                return;
            }
            else if (command == null && "test".equals(crt))
            {
                command = new Test(this);
                return;
            }
            else if ("load".equals(crt))
            {
                command = new Load(this, arguments, i + 1);
            }
            else if ("content".equals(crt))
            {
                command = new Content(this);
            }
            else if ("connect".equals(crt))
            {
                command = new Connect(this);
            }
            else if ("generate-keys".equals(crt))
            {
                command = new GenerateKeys(this);
            }
            else if ("delete".equals(crt))
            {
                command = new Delete(this);
            }
            else if ("--conf".equals(crt))
            {
                // load configuration from the specified file but overlay the command line values, if provided.
                // command line takes precedence.
                String confFileName = null;

                if (i < args.length - 1)
                {
                    confFileName = args[++i];

                    if (confFileName.startsWith("--"))
                    {
                        throw new UserErrorException("a file name (and not another option) must follow --conf");
                    }
                }

                if (confFileName == null)
                {
                    throw new UserErrorException("a file name must follow --conf");
                }

                BufferedReader br = null;

                try
                {
                    br = new BufferedReader(new FileReader(confFileName));
                    configurationFileContent = new Properties();
                    configurationFileContent.load(br);
                }
                finally
                {
                    if (br != null)
                    {
                        br.close();
                    }
                }
            }
            else if ("--nodes".equals(crt))
            {
                // this is special, as the list of nodes may come in different arguments, comma-separated, etc.

                if (nodesSb != null)
                {
                    throw new UserErrorException("--nodes specified twice on command line");
                }

                nodesSb = new StringBuilder();
            }
            else if ("--password".equals(crt))
            {
                hasPassword = true;
            }
            else if ("--threads".equals(crt))
            {
                if (i < args.length - 1)
                {
                    threads = Integer.parseInt(args[++i]);
                }
            }
            else if ("--max-total".equals(crt))
            {
                if (i < args.length - 1)
                {
                    maxTotal = Integer.parseInt(args[++i]);
                }
            }
            else if ("--max-wait-millis".equals(crt))
            {
                if (i < args.length - 1)
                {
                    maxWaitMillis = Long.parseLong(args[++i]);
                }
            }
            else if ("--max-operations".equals(crt))
            {
                if (i == args.length - 1)
                {
                    throw new UserErrorException("a positive integer should follow --max-operations");
                }
                if (i < args.length - 1)
                {
                    maxOperations = Long.parseLong(args[++i]);
                }
            }
            else if ("--sleep".equals(crt))
            {
                if (i < args.length - 1)
                {
                    sleep = Long.parseLong(args[++i]);
                }
            }
            else if ("--key-size".equals(crt))
            {
                if (i < args.length - 1)
                {
                    keySize = Integer.parseInt(args[++i]);
                }
            }
            else if ("--value-size".equals(crt))
            {
                if (i < args.length - 1)
                {
                    valueSize = Integer.parseInt(args[++i]);
                }
            }
            else if ("--use-different-values".equals(crt))
            {
                useDifferentValues = true;
            }
            else if ("--output".equals(crt) && !(command instanceof Content))
            {
                if (i < args.length - 1)
                {
                    output = args[++i];

                    if (output.startsWith("--"))
                    {
                        i--;

                        //noinspection UnnecessaryContinue
                        continue;
                    }
                }
            }
            else if ("--expiration".equals(crt))
            {
                if (i < args.length - 1)
                {
                    keyExpirationSecs = Long.parseLong(args[++i]);
                }
            }
            else if ("--exception-file".equals(crt))
            {
                if (i < args.length - 1)
                {
                    exceptionFile = args[++i];

                    if (exceptionFile.startsWith("--"))
                    {
                        throw new UserErrorException("a file name (and not another option) must follow --exception-file");
                    }
                }
            }
            else if ("--cache".equals(crt))
            {
                if (i < args.length - 1)
                {
                    cacheName = args[++i];

                    if (cacheName.startsWith("--"))
                    {
                        throw new UserErrorException("a cache name (and not another option) must follow --cache");
                    }
                }
            }
            else if ("--keystore-file".equals(crt) || "--key-store-file".equals(crt))
            {
                if (i < args.length - 1)
                {
                    keyStoreFile = args[++i];

                    if (keyStoreFile.startsWith("--"))
                    {
                        throw new UserErrorException("a file name (and not another option) must follow --keystore-file");
                    }
                }
            }
            else if ("--username".equals(crt))
            {
                if (i == args.length - 1)
                {
                    throw new UserErrorException("a user name should follow --username");
                }
                if (i < args.length - 1)
                {
                    username = args[++i];
                }
            }
            else if (command != null)
            {
                // give it one more chance, pass it to the command, maybe it's a command argument?
                command.addArgument(crt);
            }
            else
            {
                throw new UserErrorException("unknown command '" + crt + "'");
            }
        }

        //
        // overlay configuration file properties only if they weren't specified on the command line
        //

        if (keyExpirationSecs == -1L && configurationFileContent != null && configurationFileContent.get("expiration") != null)
        {
            keyExpirationSecs = Long.parseLong((String)configurationFileContent.get("expiration"));
        }

        if (hasPassword)
        {
            // we need to read the password from the current directory's .gld.password file.

            String passwordFileDirectory = System.getProperty("password.file.directory");

            if (passwordFileDirectory == null)
            {
                passwordFileDirectory = ".";
            }

            String passwordFileName = passwordFileDirectory + "/.gld.password";

            File f = new File(passwordFileName);

            if (!f.exists())
            {
                throw new UserErrorException(f + " does not exist");
            }
            if (!f.canRead())
            {
                throw new UserErrorException(f + " exists but cannot be read");
            }

            BufferedReader br = null;

            try
            {
                br = new BufferedReader(new FileReader(f));

                password = br.readLine();
            }
            catch(Exception e)
            {
                throw new UserErrorException("failed to read " + f + ": " + e.getMessage());
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (configurationFileContent != null && configurationFileContent.get("password") != null)
        {
            password = ((String)configurationFileContent.get("password"));
        }

        if (threads == -1 && configurationFileContent != null && configurationFileContent.get("threads") != null)
        {
            threads = Integer.parseInt((String)configurationFileContent.get("threads"));
        }

        if (threads == -1)
        {
            threads = DEFAULT_THREAD_COUNT;
        }

        if (maxTotal == -1 && configurationFileContent != null && configurationFileContent.get("max-total") != null)
        {
            maxTotal = Integer.parseInt((String)configurationFileContent.get("max-total"));
        }

        if (maxTotal == -1)
        {
            maxTotal = DEFAULT_MAX_TOTAL;
        }

        if (maxWaitMillis == -1 && configurationFileContent != null && configurationFileContent.get("max-wait-millis") != null)
        {
            maxWaitMillis = Long.parseLong((String)configurationFileContent.get("max-wait-millis"));
        }

        if (maxWaitMillis == -1)
        {
            maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;
        }

        if (maxOperations == -1 && configurationFileContent != null && configurationFileContent.get("max-operations") != null)
        {
            maxOperations = Long.parseLong((String)configurationFileContent.get("max-operations"));
        }

        if (sleep == -1 && configurationFileContent != null && configurationFileContent.get("sleep") != null)
        {
            sleep = Long.parseLong((String)configurationFileContent.get("sleep"));
        }

        if (sleep == -1)
        {
            sleep = DEFAULT_SLEEP;
        }

        if (keySize == -1 && configurationFileContent != null && configurationFileContent.get("key-size") != null)
        {
            keySize = Integer.parseInt((String)configurationFileContent.get("key-size"));
        }

        if (keySize == -1)
        {
            keySize = DEFAULT_KEY_SIZE;
        }

        if (valueSize == -1 && configurationFileContent != null && configurationFileContent.get("value-size") != null)
        {
            valueSize = Integer.parseInt((String)configurationFileContent.get("value-size"));
        }

        if (valueSize == -1)
        {
            valueSize = DEFAULT_VALUE_SIZE;
        }

        if (!useDifferentValues && configurationFileContent != null && configurationFileContent.get("use-different-values") != null)
        {
            useDifferentValues = Boolean.parseBoolean((String)configurationFileContent.get("use-different-values"));
        }

        // --output can only be set on command line

        if (configurationFileContent != null && configurationFileContent.get("output") != null)
        {
            throw new UserErrorException("'output' can be only set up on command line, and not in the configuration file; use --output <file>");
        }

        if (exceptionFile == null && configurationFileContent != null && configurationFileContent.get("exception-file") != null)
        {
            exceptionFile = (String)configurationFileContent.get("exception-file");
        }

        // we should get either "nodes" (which means the service is Sharded Jedis-based) or "proxy", which means
        // the service is proxy-based

        if (proxyString == null && configurationFileContent != null && configurationFileContent.get("proxies") != null)
        {
            proxyString = (String)configurationFileContent.get("proxies");
        }

        if (command != null && command.isRemote())
        {
            if (proxyString != null)
            {
                // we-re proxy-based

                //List<Node> proxies = Node.toNodeList(proxyString);
                //service = new HARedisService(proxies, getKeyExpirationSecs(), getMaxTotal());

                throw new RuntimeException("NOT YET IMPLEMENTED - RETURN TO THIS");
            }

            // we connect directly to nodes, process nodes

            if (nodesSb != null)
            {
                nodesString = nodesSb.toString();
            }

            if (nodesString == null && configurationFileContent != null)
            {
                // fallback to the configuration file if exists
                nodesString = (String) configurationFileContent.get("nodes");
            }

            if (nodesString == null)
            {
                throw new UserErrorException(
                    "no target redis nodes specified, set --nodes host1:port1,host2:port2,... on the command line or \"nodes=host1:port1,host2:port2,...\" in the configuration file");
            }

            nodes = Node.toNodeList(nodesString);

            if (nodes.isEmpty())
            {
                throw new IllegalStateException("empty nodes list after non-null string: " + nodesString);
            }

            // TODO we handle embedded situation differently for cache and for JMS for historical reasons
            //      (for cache, we have a top-level EmbeddedCacheService, while for JMS, we have an ActiveMQ
            //      service that delegates to an EmbeddedJMSConnection factory). We need to unify to one
            //      consistent solution.

            if (!(command instanceof Load))
            {
                throw new RuntimeException(
                    "NOT YET IMPLEMENTED (1): we temporarily disabled support for ContentType for all commands, except Load. Need to refactor this.");
            }

            Load loadCommand = (Load)command;
            ContentType contentType = loadCommand.getContentType();

            if (nodes.get(0) instanceof EmbeddedNode && !ContentType.JMS.equals(contentType))
            {
                EmbeddedNode en = (EmbeddedNode)nodes.get(0);
                service = new EmbeddedCacheService(en.getCapacity());
            }
            else
            {
                if (ContentType.KEYVALUE.equals(contentType))
                {
                    service =
                        new InfinispanService(getNodes(), getPassword(), getMaxTotal(),
                            getMaxWaitMillis(), getKeyExpirationSecs(), getCacheName());
                }
                else
                {
                    service = new ActiveMQService(this, nodes);
                }
            }
        }

        // other post-processing

        if (command == null)
        {
            throw new UserErrorException("no command specified");
        }

        command.initialize();
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
