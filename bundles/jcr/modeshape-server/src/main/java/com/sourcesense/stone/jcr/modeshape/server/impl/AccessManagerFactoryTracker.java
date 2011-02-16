package com.sourcesense.stone.jcr.modeshape.server.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class AccessManagerFactoryTracker extends ServiceTracker {

    private BundleContext bundleContext;

    public AccessManagerFactoryTracker(BundleContext bundleContext) {
        super(bundleContext, AccessManagerPluginFactory.class.getName(), null);
        this.bundleContext = bundleContext;
    }

    public BundleContext getAssociatedBundleContext() {
        return this.bundleContext;
    }

}
