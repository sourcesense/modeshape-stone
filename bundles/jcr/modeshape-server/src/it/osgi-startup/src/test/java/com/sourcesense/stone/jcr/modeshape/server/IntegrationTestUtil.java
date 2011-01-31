package com.sourcesense.stone.jcr.modeshape.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    static Bundle getBundle( String bundleSymbolicName,
                             Bundle[] bundles ) {
        for (Bundle bundle : bundles) {
            if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
                return bundle;
            }
        }

        return null;
    }

    static List<String> symbolicNamesListFor( Bundle[] bundles ) {
        return Lists.transform(Arrays.asList(bundles), new Function<Bundle, String>() {

            @Override
            public String apply( Bundle bundle ) {
                return bundle.getSymbolicName();
            }
        });
    }

    static int countConfigurationsFor( BundleContext bundleContext,
                                       String factoryPid ) throws IOException, InvalidSyntaxException {
        ServiceReference configurationAdminServiceReference = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdminService = (ConfigurationAdmin)bundleContext.getService(configurationAdminServiceReference);

        Configuration[] modeShapeRepositoryConfigurations = configurationAdminService.listConfigurations("("
                                                                                                         + ConfigurationAdmin.SERVICE_FACTORYPID
                                                                                                         + "=" + factoryPid + ")");
        return modeShapeRepositoryConfigurations.length;
    }
    
    static int countModeShapeRepositoryConfigurations(BundleContext bundleContext) throws IOException, InvalidSyntaxException {
        return countConfigurationsFor(bundleContext, ActivatorHelper.SERVER_REPOSITORY_FACTORY_PID);
    }
}
