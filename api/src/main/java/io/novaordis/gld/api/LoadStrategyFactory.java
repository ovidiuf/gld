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

package io.novaordis.gld.api;

import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface LoadStrategyFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    Logger log = LoggerFactory.getLogger(LoadStrategyFactory.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * A static method that builds the specialized factory dynamically and passes the control to it. We do it this way
     * because we want to be able to add arbitrary service types in the future without modifying the code of this layer
     * of the factory.
     *
     * @param serviceType a ServiceType instance.
     * @param configuration map (typically extracted from the YAML configuration file)
     *
     * @return a LoadStrategy instance
     *
     * @exception io.novaordis.utilities.UserErrorException if the strategy instance cannot be instantiated because
     * of an user error (improperly specified service type, load strategy name, etc).
     *
     * @exception IllegalArgumentException on null service type
     */
    static LoadStrategy buildInstance(ServiceType serviceType, Map<String, Object> configuration) throws Exception {

        if (serviceType == null) {
            throw new IllegalArgumentException("null service type");
        }

        //
        // we instantiate the load strategy factory corresponding to a specific service type dynamically. This
        // is to allow adding new service types without changing this method.
        //

        String serviceTypeName = serviceType.name();

        //
        // we expect the factory class name to match the following pattern:
        //
        // <this-package>.<service-type>.load.<service-type-with-first-letter-capitalized>LoadFactory
        //

        String className = LoadStrategyFactory.class.getPackage().getName();

        className += "." + serviceTypeName;
        className += ".load.";
        className += Character.toUpperCase(serviceTypeName.charAt(0)) + serviceTypeName.substring(1);
        className += "LoadStrategyFactory";

        log.debug("attempting to use load strategy factory class " + className);

        LoadStrategyFactory f;

        try {

            Class c = Class.forName(className);
            f = (LoadStrategyFactory)c.newInstance();
        }
        catch (Exception e) {

            throw new UserErrorException(
                    "failed to instantiate a load strategy factory corresponding to a service of type " + serviceType,
                    e);
        }

        //noinspection UnnecessaryLocalVariable
        LoadStrategy ls = f.buildInstance(configuration);
        return ls;


        //    NOMBP2:gld ovidiu$ grep -r "extends *LoadStrategyBase" *
//    TO-DISTRIBUTE-extensions/cache/cache-common/src/main/java/io/novaordis/gld/extension/cache/strategy/ReadThenWriteOnMissLoadStrategy.java:public class ReadThenWriteOnMissLoadStrategy extends LoadStrategyBase
//            TO-DISTRIBUTE-extensions/cache/cache-common/tmp-src/DeleteLoadStrategy.java:public class DeleteLoadStrategy extends LoadStrategyBase
//            TO-DISTRIBUTE-extensions/cache/cache-common/tmp-src/HttpSessionLoadStrategy.java:public class HttpSessionLoadStrategy extends LoadStrategyBase {
//        TO-DISTRIBUTE-extensions/cache/cache-common/tmp-src/WriteThenReadLoadStrategy.java:public class WriteThenReadLoadStrategy extends LoadStrategyBase
//                TO-DISTRIBUTE-extensions/cache/cache-common/tmp-src-test/FailureToInstantiateLoadStrategy.java:public class FailureToInstantiateLoadStrategy extends LoadStrategyBase
//                TO-DISTRIBUTE-extensions/jms/jms-common/tmp-src/JmsLoadStrategy.java:public abstract class JmsLoadStrategy extends LoadStrategyBase
//                load-driver/src/test/java/io/novaordis/gld/driver/MockLoadStrategy.java:public class MockLoadStrategy extends LoadStrategyBase {



//    public static LoadStrategy fromString(
//            Configuration configuration, String strategyName, ContentType contentType, List<String> arguments, int from)
//            throws Exception {
//
//        String subPackage = ContentType.JMS.equals(contentType) ? "jms" : "cache";
//
//        LoadStrategy result;
//
//        String originalStrategyName = strategyName;
//
//        // user friendliness - if the first letter of the strategy name is not capitalized,
//        // capitalize it for her. This will allow the user to specify --load-strategy read
//
//        if (Character.isLowerCase(strategyName.charAt(0))) {
//            strategyName = Character.toUpperCase(strategyName.charAt(0)) + strategyName.substring(1);
//        }
//
//        try {
//            result = ClassLoadingUtilities.getInstance(LoadStrategy.class,
//                "io.novaordis.gld.strategy.load." + subPackage, strategyName, "LoadStrategy");
//        }
//        catch(Exception e) {
//            // turn all load strategy loading exceptions into UserErrorExceptions and bubble them up
//            String msg = "invalid load strategy \"" + originalStrategyName + "\": " + e.getMessage();
//            Throwable cause = e.getCause();
//            throw new UserErrorException(msg, cause);
//        }
//
//        // provide the rest of the arguments to the strategy, which will pick the ones it needs and
//        // remove them from the list, leaving the rest of the arguments in the list
//        result.configure(configuration, arguments, from);
//        return result;
//    }

    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @param configuration map (typically extracted from the YAML configuration file)
     */
    LoadStrategy buildInstance(Map<String, Object> configuration) throws Exception;

    /**
     * @return the service type the load strategies built by this factory are associated with.
     */
    ServiceType getServiceType();

}
