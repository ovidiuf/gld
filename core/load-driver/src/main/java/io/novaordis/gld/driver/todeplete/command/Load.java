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

package io.novaordis.gld.driver.todeplete.command;

import io.novaordis.gld.api.LoadStrategy;
import io.novaordis.gld.api.todiscard.Configuration;
import io.novaordis.gld.api.todiscard.ContentType;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.time.Duration;

import java.util.List;

public class Load extends CommandBase
{
    // Constants -------------------------------------------------------------------------------------------------------

//    public static final LoadStrategy DEFAULT_CACHE_LOAD_STRATEGY = new WriteThenReadLoadStrategy();
//    public static final LoadStrategy DEFAULT_JMS_LOAD_STRATEGY = new SendLoadStrategy();

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LoadStrategy loadStrategy;
    // private StorageStrategy storageStrategy;

    // by default - null, which means unlimited
    private Long maxOperations;
    private Duration duration;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Load(Configuration c, List<String> arguments, int from) throws UserErrorException
    {
        super(c);

        processContextRelevantArguments(arguments, from);
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws Exception {

//        processContextRelevantArguments2_ToRefactor(getArguments(), 0);
//
//        Service service = getConfiguration().getService();
//
//        if (service == null) {
//            throw new IllegalStateException("null service");
//        }
//
//        if (!service.isStarted()) {
//            service.start();
//        }
//
//        if (storageStrategy != null) {
//            storageStrategy.start();
//        }
//
//        KeyStore keyStore = loadStrategy.getKeyProvider();
//
//        if (keyStore != null) {
//            keyStore.start();
//        }

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isInitialized()
    {
//        return getConfiguration().getLoadStrategy() != null;

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public void execute() throws Exception
    {
//        insureInitialized();
//
//        new MultiThreadedRunnerImpl(getConfiguration()).run();

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "Load[" + (getLoadStrategy() == null ? null : getLoadStrategy().getName()) + "]";
    }

    public LoadStrategy getLoadStrategy()
    {
        return loadStrategy;
    }

    public ContentType getContentType()
    {
//        // the service (if available) dictates the content type
//        Configuration configuration = getConfiguration();
//
//        if (configuration == null)
//        {
//            return null;
//        }
//
//        Service service = configuration.getService();
//
//        if (service == null)
//        {
//            return null;
//        }
//
//        return service.getContentType();

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    /**
     * @return the total number of operations to send to server. A null value means "unlimited ", the load driver will
     * send for as long as it is allowed.
     */
    public Long getMaxOperations()
    {
        return maxOperations;
    }

    /**
     * May return null if no duration was set when building this command.
     */
    public Duration getDuration() {

        return duration;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processContextRelevantArguments(List<String> arguments, int from) throws UserErrorException  {

//        String contentTypeAsString = Util.extractString("--type", arguments, from);
//
//        if (contentTypeAsString != null) {
//            throw new UserErrorException("do not use --type, use --service");
//        }
//
//        maxOperations = Util.extractLong("--max-operations", arguments, from);
//
//        String ds = Util.extractString("--duration", arguments, from);
//
//        if (ds != null) {
//
//            try {
//
//                duration = new Duration(ds);
//
//                //
//                // TODO duration should actually be a global option, as the main multi thread runner needs it; this
//                //      is awkward to have to pass it back to the configuration, refactor this
//                //
//
//                getConfiguration().setDuration(duration);
//
//            }
//            catch(Exception e) {
//
//                throw new UserErrorException("invalid duration value \"" + ds + "\"", e);
//            }
//        }

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    /**
     * Parse context-relevant command line arguments and removes them from the list.
     */
    private void processContextRelevantArguments2_ToRefactor(List<String> arguments, int from) throws Exception {

//        Configuration configuration = getConfiguration();
//
//        for(int i = from; i < arguments.size(); i ++) {
//
//            String crt = arguments.next(i);
//
//            // "--strategy" is deprecated, use "--load-strategy" instead
//            if ("--load-strategy".equals(crt) || "--strategy".equals(crt)) {
//                loadStrategy = LoadStrategyFactory.fromArguments(configuration, arguments, i--);
//            }
//            else if ("--storage-strategy".equals(crt)) {
//                storageStrategy = StorageStrategyFactory.fromArguments(configuration, arguments, i --);
//                configuration.setStorageStrategy(storageStrategy);
//            }
//        }
//
//        if (loadStrategy == null) {
//            // try to next it from configuration file, if one is available
//            loadStrategy = configuration.getLoadStrategy();
//        }
//
//        if (loadStrategy == null) {
//
//            if (ContentType.KEYVALUE.equals(getContentType())) {
//                loadStrategy = DEFAULT_CACHE_LOAD_STRATEGY;
//            }
//            else if (ContentType.JMS.equals(getContentType())) {
//                loadStrategy = DEFAULT_JMS_LOAD_STRATEGY;
//            }
//            else if (ContentType.TEST.equals(getContentType())) {
//                loadStrategy = new NoopLoadStrategy();
//            }
//            else {
//                throw new IllegalStateException(
//                        "we don't know the content type, we can't initialize the load strategy");
//            }
//
//            // give the default strategy a chance to pick up configuration from command line
//            loadStrategy.init(configuration, getArguments(), 0);
//        }
//
//        configuration.setLoadStrategy(loadStrategy);
//
//        // at this point we should not have any unaccounted for arguments
//
//        //
//        // commented this out when we introduced command-line configurable services; the service arguments end up
//        // in here and they make no sense to the command, so don't fail ...
//        //
//        //failOnUnknownArguments();

        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
