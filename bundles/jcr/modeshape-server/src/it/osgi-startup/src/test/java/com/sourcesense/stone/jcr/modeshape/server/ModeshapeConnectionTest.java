package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.ServiceReference;

import com.sourcesense.stone.test.services.EmptyService;
import com.sourcesense.stone.test.services.SimpleService;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest extends AbstractTestCase {

	@Test
	public void shouldTakeTheDefaultWorkspace()
			throws Exception {
		
		ServiceReference serviceReference = bundleContext
				.getServiceReference(EmptyService.class.getName());

		assertNull(serviceReference);

		ServiceReference serviceReference2 = bundleContext
				.getServiceReference(SimpleService.class.getName());

		assertNotNull(serviceReference2);
		
		EmptyService ssr = (EmptyService) bundleContext
				.getService(serviceReference2);

		assertNotNull(ssr);
	}
}
