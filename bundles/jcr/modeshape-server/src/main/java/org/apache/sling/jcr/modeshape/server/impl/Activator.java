package org.apache.sling.jcr.modeshape.server.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class Activator implements BundleActivator, ServiceListener {

	public void serviceChanged(ServiceEvent event) {
		throw new RuntimeException("To be implemented");
	}

	public void start(BundleContext context) throws Exception {
		throw new RuntimeException("To be implemented");
	}

	public void stop(BundleContext context) throws Exception {
		throw new RuntimeException("To be implemented");
	}

}
