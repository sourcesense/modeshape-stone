package com.sourcesense.stone.jcr.modeshape.server.impl;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * TODO fill me
 *
 * @version $Id$
 */
public class AccessManagerFactoryTracker extends ServiceTracker {

    private BundleContext bundleContext;

    /**
     * 
     *
     * @param bundleContext
     */
    public AccessManagerFactoryTracker(BundleContext bundleContext) {
        super(bundleContext, AccessManagerPluginFactory.class.getName(), null);
        this.bundleContext = bundleContext;
    }

    /**
     * 
     *
     * @return
     */
    public BundleContext getAssociatedBundleContext() {
        return this.bundleContext;
    }

}
