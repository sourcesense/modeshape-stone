package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest {

	@Configuration
	public static Option[] configuration() {
		return options(
				felix(),
				mavenConfiguration(),
				wrappedBundle(mavenBundle("com.google.collections",
						"google-collections").version("1.0-rc3")));
	}

	@Test
	public void shouldTakeTheDefaultWorkspace(BundleContext bundleContext)
			throws Exception {
		ServiceReference ssrr = null;
		try {
			Dictionary<String, String> serviceProperties = new Hashtable<String, String>();
			serviceProperties.put(Constants.SERVICE_VENDOR, "JBoss");
			serviceProperties.put(Constants.SERVICE_DESCRIPTION, "Test service for SlingRepository");
			bundleContext.registerService(
					SlingRepositoryService.class.getName(),
					new SlingRepositoryService(), serviceProperties);
			ssrr = bundleContext
					.getServiceReference(SlingRepositoryService.class.getName());
			SlingRepositoryService ssr = (SlingRepositoryService) bundleContext
					.getService(ssrr);
			//assertNotNull(ssr.getRepository());
		} catch (Exception ex) {
			throw ex;
		} finally {
			bundleContext.ungetService(ssrr);
		}
	}
}
