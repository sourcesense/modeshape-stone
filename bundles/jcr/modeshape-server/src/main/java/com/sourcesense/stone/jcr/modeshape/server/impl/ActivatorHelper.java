package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.util.Hashtable;
import org.apache.sling.jcr.base.util.RepositoryAccessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivatorHelper {

    private static final Logger log = LoggerFactory.getLogger(ActivatorHelper.class);
    
    boolean verifyConfiguration( ServiceReference configurationAdminServiceReference, BundleContext bundleContext ) {
//        ConfigurationAdmin ca = (ConfigurationAdmin) bundleContext.getService(configurationAdminServiceReference);
//        if (ca == null) {
//            log.error("verifyConfiguration: Failed to get Configuration Admin Service from Service Reference");
//            return;
//        }
//
//        try {
//            // find a configuration for theses properties...
//            Configuration[] cfgs = ca.listConfigurations("("
//                + ConfigurationAdmin.SERVICE_FACTORYPID + "="
//                + SERVER_REPOSITORY_FACTORY_PID + ")");
//            if (cfgs != null && cfgs.length > 0) {
//                log.info(
//                    "verifyConfiguration: {} Configurations available for {}, nothing to do",
//                    new Object[] { new Integer(cfgs.length),
//                        SERVER_REPOSITORY_FACTORY_PID });
//                return;
//            }
//
//            // No config, create a default one.
//            Hashtable<String, String> defaultConfig = new Hashtable<String, String>();
//            final String overrideUrl = bundleContext.getProperty(RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY);
//            if(overrideUrl != null && overrideUrl.length() > 0) {
//                // Ignore other parameters if override URL (SLING-254) is set
//                defaultConfig.put(RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY, overrideUrl);
//                log.info(RepositoryAccessor.REPOSITORY_URL_OVERRIDE_PROPERTY + "=" + overrideUrl +
//                    ", using it to create the default configuration");
//
//            } else {
//               initDefaultConfig(defaultConfig, bundleContext);
//            }
//
//            // create the factory and set the properties
//            Configuration config = ca.createFactoryConfiguration(SERVER_REPOSITORY_FACTORY_PID);
//            config.update(defaultConfig);
//
//            log.info("verifyConfiguration: Created configuration {} for {}",
//                config.getPid(), config.getFactoryPid());
//
//        } catch (Throwable t) {
//            log.error(
//                "verifyConfiguration: Cannot check or define configuration", t);
//        } finally {
//            bundleContext.ungetService(ref);
//        }
        return false;
    }

    AccessManagerFactoryTracker createAccessManagerFactoryTracker( BundleContext bundleContext ) {
        return new AccessManagerFactoryTracker(bundleContext);
    }

}
