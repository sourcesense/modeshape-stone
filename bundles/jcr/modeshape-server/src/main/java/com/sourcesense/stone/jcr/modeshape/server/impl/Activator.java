package com.sourcesense.stone.jcr.modeshape.server.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

public class Activator implements BundleActivator, ServiceListener {

    private static final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();;

    @Override
    public void serviceChanged( ServiceEvent event ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void start( BundleContext bundleContext ) throws Exception {
        ServiceReference configurationAdminServiceReference = bundleContext.getServiceReference(CONFIG_ADMIN_NAME);

        if (null != configurationAdminServiceReference) {
            getActivatorHelper().verifyConfiguration(configurationAdminServiceReference);
        } else {
            bundleContext.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + CONFIG_ADMIN_NAME + ")");
        }
    }

    @Override
    public void stop( BundleContext context ) throws Exception {
        // TODO Auto-generated method stub

    }

    protected ActivatorHelper getActivatorHelper() {
        return new ActivatorHelper();
    }

}
