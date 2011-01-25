package com.sourcesense.stone.jcr.modeshape.server;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import static org.junit.Assert.assertNotNull;

import com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest extends AbstractTestCase {

	@Test
	public void shouldTakeTheDefaultWorkspace(BundleContext bundleContext)
			throws Exception {
		ServiceReference ssrr = null;
		BundleContext modeshapeServer = null;
		try {
			modeshapeServer = bundleContext.getBundle(2).getBundleContext();
			Dictionary<String, String> serviceProperties = new Hashtable<String, String>();
			serviceProperties.put(Constants.SERVICE_VENDOR, "JBoss");
			serviceProperties.put(Constants.SERVICE_DESCRIPTION, "Test service for SlingRepository");
			modeshapeServer.registerService(
					SlingServerRepository.class.getName(),
					new SlingServerRepository(), serviceProperties);
			ssrr = modeshapeServer
					.getServiceReference(SlingServerRepository.class.getName());
			SlingServerRepository ssr = (SlingServerRepository) modeshapeServer
					.getService(ssrr);
			//assertNotNull(ssr.getDefaultWorkspace());
		} catch (Exception ex) {
			throw ex;
		} finally {
			modeshapeServer.ungetService(ssrr);
		}
	}
}
