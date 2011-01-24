package com.sourcesense.stone.jcr.modeshape.server.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

public class Activator implements BundleActivator, ServiceListener {

    private static final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();
    private AccessManagerFactoryTracker accessManagerFactoryTracker;
    private ActivatorHelper activatorHelper;
    private BundleContext bundleContext;

    @Override
    public void serviceChanged( ServiceEvent event ) {
        if (event.getType() == ServiceEvent.REGISTERED) {

            getActivatorHelper().verifyConfiguration(event.getServiceReference(), bundleContext);
            bundleContext.removeServiceListener(this);
        }
    }

    @Override
    public void start( BundleContext bundleContext ) throws Exception {
        this.bundleContext = bundleContext;

        if (null == activatorHelper) {
            this.activatorHelper = getActivatorHelper();
        }

        ServiceReference configurationAdminServiceReference = bundleContext.getServiceReference(CONFIG_ADMIN_NAME);

        if (null != configurationAdminServiceReference) {
            activatorHelper.verifyConfiguration(configurationAdminServiceReference, bundleContext);
        } else {
            bundleContext.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + CONFIG_ADMIN_NAME + ")");
        }

        AccessManagerFactoryTracker accessManagerFactoryTracker = getAccessManagerFactoryTracker();
        if (null == accessManagerFactoryTracker) {
            accessManagerFactoryTracker = this.accessManagerFactoryTracker = activatorHelper.createAccessManagerFactoryTracker(bundleContext);
        }
        accessManagerFactoryTracker.open();
    }

    @Override
    public void stop( BundleContext bundleContext ) throws Exception {
        AccessManagerFactoryTracker managerFactoryTracker = getAccessManagerFactoryTracker();

        if (null != managerFactoryTracker) {
            managerFactoryTracker.close();
        }
    }

    protected ActivatorHelper getActivatorHelper() {
        return new ActivatorHelper();
    }

    protected AccessManagerFactoryTracker getAccessManagerFactoryTracker() {
        return this.accessManagerFactoryTracker;
    }
}
