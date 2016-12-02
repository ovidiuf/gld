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
import com.novaordis.gld.command.Start;
import com.novaordis.gld.command.Status;
import com.novaordis.gld.command.Stop;
import com.novaordis.gld.command.Test;
import com.novaordis.gld.command.Version;
import com.novaordis.gld.sampler.Sampler;
import com.novaordis.gld.service.EmbeddedGenericService;
import com.novaordis.gld.service.cache.EmbeddedCacheService;
import com.novaordis.gld.service.jms.activemq.ActiveMQService;
import com.novaordis.gld.service.cache.infinispan.InfinispanService;
import com.novaordis.gld.statistics.SamplerConfigurator;
import com.novaordis.gld.strategy.load.LoadStrategyFactory;
import io.novaordis.utilities.time.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_THREAD_COUNT= 1;
    public static final int DEFAULT_MAX_TOTAL = 100;
    public static final long DEFAULT_MAX_WAIT_MILLIS = 1000L;
    public static final long DEFAULT_SLEEP = 0;

    public static final int DEFAULT_KEY_SIZE = 70;

    // based on production data analysis
    public static final int DEFAULT_VALUE_SIZE = 5000;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Command command;

    private int threads;
    private int maxTotal;
    private long maxWaitMillis;
    private long sleep;
    private int keySize;
    private int valueSize;
    private boolean useDifferentValues;
    private String outputFileName;
    private List<Node> nodes;
    private String password;
    private long keyExpirationSecs;
    private Properties configurationFileContent;
    private String keyStoreFile;
    private String username;
    private LoadStrategy loadStrategy;
    private StorageStrategy storageStrategy;
    private Service service;
    private Sampler sampler;
    private String serviceString;
    private boolean waitForConsoleQuit;
    private boolean background;
    private Duration duration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ConfigurationImpl(String[] args) throws Exception {
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
     * @see Configuration#getNodes()
     */
    @Override
    public List<Node> getNodes()
    {
        return nodes;
    }

    /**
     * @see Configuration#getSleepMs()
     */
    @Override
    public long getSleepMs()
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
     * @see Configuration#getOutputFile()
     */
    @Override
    public String getOutputFile()
    {
        return outputFileName;
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
        return null;
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

    @Override
    public Sampler getSampler()
    {
        return sampler;
    }

    @Override
    public void setSampler(Sampler sampler)
    {
        this.sampler = sampler;
    }

    @Override
    public boolean inBackground()
    {
        return background;
    }

    @Override
    public Properties getConfigurationFileContent()
    {
        return configurationFileContent;
    }

    @Override
    public boolean waitForConsoleQuit()
    {
        return waitForConsoleQuit;
    }

    @Override
    public void setWaitForConsoleQuit(boolean b)
    {
        this.waitForConsoleQuit = b;
    }

    @Override
    public Duration getDuration() {

        return duration;
    }

    @Override
    public void setDuration(Duration d) {

        this.duration = d;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return
            "Configuration: " +
                "threads=" + threads + ", " +
                "maxTotal="  + maxTotal + ", " +
                "maxWaitMillis="  + maxWaitMillis + ", " +
                "sleep=" + (sleep > 0 ? (sleep + " ms") : "no") + ", " +
                "keySize=" + keySize + ", " +
                "valueSize=" + valueSize + ", " +
                "expiration=" + (keyExpirationSecs == -1L ? "NEVER" : keyExpirationSecs + " sec(s)") + ", " +
                "useDifferentValues=" + useDifferentValues + ", " +
                "outputFileName=" + outputFileName + ", " +
                "password=" + (password == null ? "N/A" : "***") + ", " +
                "cache service=" + service;

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    private void parseCommandLine(String[] args) throws Exception  {

        if (args.length == 0) {
            command = new Help(this);
            command.initialize();
            return;
        }

        // defaults

        threads = -1;
        maxTotal = -1;
        maxWaitMillis = -1L;
        sleep = -1L;
        keySize = -1;
        valueSize = -1;
        useDifferentValues = false;

        StringBuilder nodesSb = null;
        String nodesString = null;

        // default - don't expire
        keyExpirationSecs = -1L;

        String proxyString = null;

        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        String statisticsString = Util.extractString("--statistics", arguments, 0);

        for(int i = 0; i < arguments.size(); i ++) {

            String crt = arguments.get(i);

            if (nodesSb != null) {

                if (crt.startsWith("--")) {
                    nodesString = nodesSb.toString();
                    nodesSb = null;
                }
                else {
                    nodesSb.append(crt);
                    continue;
                }
            }

            if ("--help".equals(crt) || "help".equals(crt)) {
                command = new Help(this);
                return;
            }
            else if ("version".equals(crt)) {
                command = new Version(this);
                command.initialize();
                return;
            }
            else if (command == null && "test".equals(crt)) {
                command = new Test(this);
                return;
            }
            else if ("load".equals(crt)) {
                command = new Load(this, arguments, i + 1);
            }
            else if ("content".equals(crt)) {
                command = new Content(this);
            }
            else if ("connect".equals(crt)) {
                command = new Connect(this);
            }
            else if ("generate-keys".equals(crt)) {
                command = new GenerateKeys(this);
            }
            else if ("delete".equals(crt)) {
                command = new Delete(this);
            }
            else if ("start".equals(crt)) {
                command = new Start(this);
            }
            else if ("stop".equals(crt)) {
                command = new Stop(this);
            }
            else if ("status".equals(crt)) {
                command = new Status(this);
            }
            else if ("--conf".equals(crt)) {
                // load configuration from the specified file but overlay the command line values, if provided.
                // command line takes precedence.
                String confFileName = null;

                if (i < arguments.size() - 1) {

                    confFileName = arguments.get(++i);

                    if (confFileName.startsWith("--")) {
                        throw new UserErrorException("a file name (and not another option) must follow --conf");
                    }
                }

                if (confFileName == null) {
                    throw new UserErrorException("a file name must follow --conf");
                }

                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader(confFileName));
                    configurationFileContent = new Properties();
                    configurationFileContent.load(br);
                }
                finally {
                    if (br != null) {
                        br.close();
                    }
                }
            }
            else if ("--nodes".equals(crt)) {
                // this is special, as the list of nodes may come in different commandLineArguments, comma-separated, etc.
                if (nodesSb != null){
                    throw new UserErrorException("--nodes specified twice on command line");
                }

                nodesSb = new StringBuilder();
            }
            else if ("--threads".equals(crt)) {
                if (i < arguments.size() - 1) {
                    threads = Integer.parseInt(arguments.get(++i));
                }
            }
            else if ("--max-total".equals(crt)) {
                if (i < arguments.size() - 1) {
                    maxTotal = Integer.parseInt(arguments.get(++i));
                }
            }
            else if ("--max-wait-millis".equals(crt)) {
                if (i < arguments.size() - 1) {
                    maxWaitMillis = Long.parseLong(arguments.get(++i));
                }
            }
            else if ("--sleep".equals(crt)) {
                if (i < arguments.size() - 1) {
                    sleep = Long.parseLong(arguments.get(++i));
                }
            }
            else if ("--key-size".equals(crt)) {
                if (i < arguments.size() - 1) {
                    keySize = Integer.parseInt(arguments.get(++i));
                }
            }
            else if ("--value-size".equals(crt) || "--payload-size".equals(crt)) {
                if (i < arguments.size() - 1) {
                    valueSize = Integer.parseInt(arguments.get(++i));
                }
            }
            else if ("--use-different-values".equals(crt)) {
                useDifferentValues = true;
            }
            else if ("--output".equals(crt) && !(command instanceof Content)) {
                if (i < arguments.size() - 1) {
                    outputFileName = arguments.get(++i);
                    if (outputFileName.startsWith("--")) {
                        i--;
                    }
                }
            }
            else if ("--expiration".equals(crt)) {
                if (i < arguments.size() - 1) {
                    keyExpirationSecs = Long.parseLong(arguments.get(++i));
                }
            }
            else if ("--exception-file".equals(crt)) {
                if (i < arguments.size() - 1) {
                    String arg = arguments.get(++i);
                    if (arg.startsWith("--")) {
                        throw new UserErrorException("a file name (and not another option) must follow --exception-file");
                    }
                    setExceptionFile(arg);
                }
            }
            else if ("--keystore-file".equals(crt) || "--key-store-file".equals(crt)) {
                if (i < arguments.size() - 1) {
                    keyStoreFile = arguments.get(++i);
                    if (keyStoreFile.startsWith("--")) {
                        throw new UserErrorException("a file name (and not another option) must follow --keystore-file");
                    }
                }
            }
            else if ("--username".equals(crt)) {
                if (i == arguments.size() - 1) {
                    throw new UserErrorException("a user name should follow --username");
                }
                if (i < arguments.size() - 1) {
                    username = arguments.get(++i);
                }
            }
            else if ("--password".equals(crt)) {
                if (i == arguments.size() - 1) {
                    throw new UserErrorException("a password name should follow --password");
                }
                if (i < arguments.size() - 1) {
                    password = arguments.get(++i);
                }
            }
            else if ("--service".equals(crt)) {
                if (i == arguments.size() - 1) {
                    throw new UserErrorException("a service name should follow --service");
                }
                if (i < arguments.size() - 1) {
                    serviceString = arguments.get(++i);
                }
            }
            else if ("--background".equals(crt)) {
                background = true;
            }
            else if (command != null) {
                // give it one more chance, pass it to the command, maybe it's a command argument?
                command.addArgument(crt);
            }
            else {
                throw new UserErrorException("unknown command '" + crt + "'");
            }
        }

        if (nodesSb != null) {
            nodesString = nodesSb.toString();
        }

        //
        // overlay configuration file properties only if they weren't specified on the command line
        //

        if (keyExpirationSecs == -1L &&
                configurationFileContent != null &&
                configurationFileContent.get("expiration") != null) {
            keyExpirationSecs = Long.parseLong((String)configurationFileContent.get("expiration"));
        }

        if (password == null &&
                configurationFileContent != null &&
                configurationFileContent.get("password") != null) {
            password = ((String)configurationFileContent.get("password"));
        }

        if (threads == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("threads") != null) {
            threads = Integer.parseInt((String)configurationFileContent.get("threads"));
        }

        if (threads == -1) {
            threads = DEFAULT_THREAD_COUNT;
        }

        if (maxTotal == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("max-total") != null) {
            maxTotal = Integer.parseInt((String)configurationFileContent.get("max-total"));
        }

        if (maxTotal == -1) {
            maxTotal = DEFAULT_MAX_TOTAL;
        }

        if (maxWaitMillis == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("max-wait-millis") != null) {
            maxWaitMillis = Long.parseLong((String)configurationFileContent.get("max-wait-millis"));
        }

        if (maxWaitMillis == -1) {
            maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;
        }

        if (sleep == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("sleep") != null) {
            sleep = Long.parseLong((String)configurationFileContent.get("sleep"));
        }

        if (sleep == -1) {
            sleep = DEFAULT_SLEEP;
        }

        if (keySize == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("key-size") != null) {
            keySize = Integer.parseInt((String)configurationFileContent.get("key-size"));
        }

        if (keySize == -1) {
            keySize = DEFAULT_KEY_SIZE;
        }

        if (valueSize == -1 &&
                configurationFileContent != null &&
                configurationFileContent.get("value-size") != null) {
            valueSize = Integer.parseInt((String)configurationFileContent.get("value-size"));
        }

        if (valueSize == -1) {
            valueSize = DEFAULT_VALUE_SIZE;
        }

        if (!useDifferentValues &&
                configurationFileContent != null &&
                configurationFileContent.get("use-different-values") != null) {
            useDifferentValues = Boolean.parseBoolean((String)configurationFileContent.get("use-different-values"));
        }

        // --outputFileName can only be set on command line

        if (configurationFileContent != null && configurationFileContent.get("output") != null) {
            outputFileName = configurationFileContent.getProperty("output");
        }

        String arg;
        if (configurationFileContent != null &&
            ((arg = (String)configurationFileContent.get("exception-file")) != null)) {
            setExceptionFile(arg);
        }

        // we should get either "nodes" (which means the service is Sharded Jedis-based) or "proxy", which means
        // the service is proxy-based

        if (proxyString == null &&
                configurationFileContent != null &&
                configurationFileContent.get("proxies") != null) {
            proxyString = (String)configurationFileContent.get("proxies");
        }

        if (loadStrategy == null &&
                configurationFileContent != null &&
                configurationFileContent.get("load-strategy") != null) {
            String strategyName = (String)configurationFileContent.get("load-strategy");
            ContentType contentType = command != null && command instanceof Load ? ((Load)command).getContentType() : null;
            loadStrategy = LoadStrategyFactory.fromString(this, strategyName, contentType, null, -1);
        }

        if (username == null &&
                configurationFileContent != null &&
                configurationFileContent.get("username") != null) {
            username = (String)configurationFileContent.get("username");
        }

        if (password == null &&
                configurationFileContent != null &&
                configurationFileContent.get("password") != null) {
            password = (String)configurationFileContent.get("password");
        }

        //
        // by now we should have a command, if we don't, we're broken
        //

        if (command == null) {
            throw new UserErrorException("no command specified");
        }

        if (command.isRemote()) {
            nodes = buildNodeList(proxyString, nodesString);
        }

        service = buildService(serviceString, this, nodes, arguments);

        command.initialize();

        this.sampler = SamplerConfigurator.getSampler(getOutputFile(), statisticsString);
    }

    /**
     * @throws Exception if problems are encountered while building the node list.
     */
    private List<Node> buildNodeList(String proxyString, String nodesString) throws Exception {

        List<Node> result;

        if (proxyString != null) {
            // we-re proxy-based

            //List<Node> proxies = Node.toNodeList(proxyString);
            //service = new HARedisService(proxies, getKeyExpirationSecs(), getMaxTotal());

            throw new RuntimeException("NOT YET IMPLEMENTED - RETURN TO THIS");
        }

        // we connect directly to nodes, process nodes

        if (nodesString == null && configurationFileContent != null) {
            // fallback to the configuration file if exists
            nodesString = (String) configurationFileContent.get("nodes");
        }

        if (nodesString == null) {

            throw new UserErrorException(
                "no target nodes specified, set --nodes host1:port1,host2:port2,... on the command line or \"nodes=host1:port1,host2:port2,...\" in the configuration file");
        }

        result = Node.toNodeList(nodesString);

        if (result.isEmpty()){
            throw new IllegalStateException("empty nodes list after non-null string: " + nodesString);
        }

        return result;
    }

    /**
     * @param serviceFullyQualifiedClassName null if no --service was specified on command line. Command line takes
     *                                       priority over inferences.
     *
     * @param commandLineArguments - command line arguments (whatever is left after the upper layer processing) that may
     *                             contain configuration relevant to the service. The service is supposed to recognize
     *                             them, extract them configure itself with them and remove them from the list.
     *
     * @throws Exception
     */
    private Service buildService(String serviceFullyQualifiedClassName, Configuration configuration,
                                 List<Node> nodes, List<String> commandLineArguments) throws Exception {
        Service result;

        if (serviceString != null) {

            Service service;

            // shortcuts
            if ("embedded-generic".equalsIgnoreCase(serviceString)) {
                service = new EmbeddedGenericService();
            }
            else if ("embedded-cache".equalsIgnoreCase(serviceString)) {
                service = new EmbeddedCacheService();
            }
            else if ("activemq".equalsIgnoreCase(serviceString)) {
                service = new ActiveMQService();
            }
            else if ("infinispan".equalsIgnoreCase(serviceString)) {
                service = new InfinispanService();
            }
            else {

                // try to locate the class and instantiate it
                Class c = getClass().getClassLoader().loadClass(serviceFullyQualifiedClassName);
                //noinspection UnnecessaryLocalVariable
                service = (Service) c.newInstance();
            }

            service.setConfiguration(configuration);
            service.setTarget(nodes);
            service.configure(commandLineArguments);
            return service;
        }

        //
        // heuristics
        //

        // TODO we handle embedded situation differently for cache and for JMS for historical reasons
        //      (for cache, we have a top-level EmbeddedCacheService, while for JMS, we have an ActiveMQ
        //      service that delegates to an EmbeddedJMSConnection factory). We need to unify to one
        //      consistent solution.

        ContentType contentType;

        if (command instanceof Load) {
            Load loadCommand = (Load)command;
            contentType = loadCommand.getContentType();
        }
        else {
            contentType = ContentType.valueOf(configurationFileContent.getProperty("content-type"));
        }

        if (nodes.get(0) instanceof EmbeddedNode && !ContentType.JMS.equals(contentType)) {
            EmbeddedNode en = (EmbeddedNode)nodes.get(0);
            result = new EmbeddedCacheService(en.getCapacity());
        }
        else {

            if (ContentType.TEST.equals(contentType)) {
                result = new EmbeddedGenericService();
            }
            else if (ContentType.KEYVALUE.equals(contentType)) {
                throw new DeprecatedException("a cache service initialization must be done via --service");
            }
            else if (ContentType.JMS.equals(contentType)) {
                result = new ActiveMQService(this, nodes);
            }
            else {
                throw new UserErrorException("unknown content type: " + contentType + ", use --service");
            }
        }

        return result;
    }

    private void setExceptionFile(String s) {
        throw new UserErrorException(
            "currently we don't support dumping specific exception information into a file (" + s +
                "), but this feature can be revived if needed. See com.novaordis.gld.statistics.ThrowableHandler");
    }


    // Inner classes ---------------------------------------------------------------------------------------------------

}