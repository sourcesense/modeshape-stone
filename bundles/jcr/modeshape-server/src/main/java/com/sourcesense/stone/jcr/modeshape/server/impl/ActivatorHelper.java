package com.sourcesense.stone.jcr.modeshape.server.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ActivatorHelper {

    boolean verifyConfiguration( ServiceReference configurationAdminServiceReference ) {
        return false;
    }

    AccessManagerFactoryTracker createAccessManagerFactoryTracker( BundleContext bundleContext ) {
        return new AccessManagerFactoryTracker(bundleContext);
    }

}
