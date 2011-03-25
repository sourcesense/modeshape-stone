package com.sourcesense.stone.jaas.modeshape;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * TODO fill me
 * 
 * @version $Id: Activator.java 41258 2011-02-16 11:07:32Z s.tripodi $
 */
public class Activator implements BundleActivator, ServiceListener {

	private BundleContext bundleContext;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serviceChanged(ServiceEvent event) {
		if (ServiceEvent.REGISTERED == event.getType()) {

			bundleContext.removeServiceListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}
}
