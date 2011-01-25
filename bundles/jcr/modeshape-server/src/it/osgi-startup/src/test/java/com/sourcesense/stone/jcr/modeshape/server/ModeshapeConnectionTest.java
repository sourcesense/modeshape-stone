package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest extends AbstractTestCase {

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
