package com.sourcesense.stone.jcr.modeshape.server;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sourcesense.stone.jcr.modeshape.server.impl.ActivatorHelper;

public class IntegrationTestUtil {

    /**
     * this class cannot be instantiated
     */
    private IntegrationTestUtil() {
        // do nothing
    }

    public static Bundle getBundle( String bundleSymbolicName,
                             Bundle[] bundles ) {
        for (Bundle bundle : bundles) {
            if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
                return bundle;
            }
        }

        return null;
    }

    public static List<String> symbolicNamesListFor( Bundle[] bundles ) {
        return Lists.transform(Arrays.asList(bundles), new Function<Bundle, String>() {

            @Override
            public String apply( Bundle bundle ) {
                return bundle.getSymbolicName();
            }
        });
    }

    public static int countConfigurationsFor( BundleContext bundleContext,
                                       String factoryPid ) throws IOException, InvalidSyntaxException {
        Configuration[] modeShapeRepositoryConfigurations = getConfigurationsFor(bundleContext, factoryPid);
        return null == modeShapeRepositoryConfigurations ? 0 : modeShapeRepositoryConfigurations.length;
    }

    public static Configuration[] getConfigurationsFor( BundleContext bundleContext,
                                                 String factoryPid ) throws IOException, InvalidSyntaxException {
        ConfigurationAdmin configurationAdminService = getConfigurationAdminService(bundleContext);
        Configuration[] modeShapeRepositoryConfigurations = configurationAdminService.listConfigurations("("
                                                                                                         + ConfigurationAdmin.SERVICE_FACTORYPID
                                                                                                         + "=" + factoryPid + ")");
        return modeShapeRepositoryConfigurations;
    }

    public static ConfigurationAdmin getConfigurationAdminService( BundleContext bundleContext ) {
        ServiceReference configurationAdminServiceReference = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdminService = (ConfigurationAdmin)bundleContext.getService(configurationAdminServiceReference);
        return configurationAdminService;
    }

    public static int countModeShapeRepositoryConfigurations( BundleContext bundleContext ) throws IOException, InvalidSyntaxException {
        return countConfigurationsFor(bundleContext, ActivatorHelper.SERVER_REPOSITORY_FACTORY_PID);
    }

    public static void removeModeShapeRepositoryConfigurations( BundleContext bundleContext ) throws IOException, InvalidSyntaxException {
        Configuration[] modeShapeRepositoryConfigurations = getModeShapeConfigurations(bundleContext);

        for (Configuration modeShapeConfiguration : modeShapeRepositoryConfigurations) {
            modeShapeConfiguration.delete();
        }
    }

    public static Configuration[] getModeShapeConfigurations( BundleContext bundleContext ) throws IOException, InvalidSyntaxException {
        return getConfigurationsFor(bundleContext, ActivatorHelper.SERVER_REPOSITORY_FACTORY_PID);
    }

    public static SlingRepository getSlingRepositoryFromServiceList( BundleContext bundleContext ) {
        ServiceReference slingRepositoryServiceReference = bundleContext.getServiceReference(SlingRepository.class.getName());
        SlingRepository slingRepository = (SlingRepository)bundleContext.getService(slingRepositoryServiceReference);
        return slingRepository;
    }

    public static void deletePreviousRepositoryConfiguration() {
        String slingConfigurationPath = "/tmp/sling";
        String modeshapeConfigurationPath = slingConfigurationPath + "/modeshape";
        String modeshapeConfigurationFilePath = modeshapeConfigurationPath + "/modeshape-repository.xml";
        new File(modeshapeConfigurationFilePath).delete();
        new File(modeshapeConfigurationPath).delete();
        new File(slingConfigurationPath).delete();
    }
}
