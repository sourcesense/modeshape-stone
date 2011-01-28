package com.sourcesense.stone.jcr.modeshape.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import com.sourcesense.stone.test.services.EmptyService;
import com.sourcesense.stone.test.services.SlingServerRepositoryService;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest extends AbstractTestCase {

	@Test
	public void shouldTakeTheDefaultWorkspace()
			throws Exception {
		/*
		ServiceReference serviceReference = bundleContext
				.getServiceReference(SlingServerRepositoryService.class.getName());

		assertNull(serviceReference);

		ServiceReference serviceReference2 = bundleContext
				.getServiceReference(EmptyService.class.getName());

		assertNotNull(serviceReference2);
		*/
	}
}
