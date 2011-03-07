package com.sourcesense.stone.jcr.modeshape.server;

import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.googleCommons;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stoneConfiguration;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.api.SecurityContextCredentials;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository;
import com.sourcesense.stone.jcr.modeshape.server.security.CustomSecurityContext;

import com.sourcesense.stone.test.services.CallingComponentService;
import com.sourcesense.stone.test.services.SimpleService;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest {

    @Inject
    BundleContext bundleContext;
    
    @Configuration
    public Option[] configuration() {
        return options(slingBasicConfiguration(), googleCommons(), stoneConfiguration());
    }
    
	@Test
	@Ignore
	public void shouldTakeTheDefaultWorkspace() throws Exception {
		ServiceReference[] serviceReference = bundleContext
				.getAllServiceReferences(SimpleService.class.getName(),
						"(component.name=com.sourcesense.stone.test.services.CallingComponentService)");
		assertNotNull(serviceReference);

		CallingComponentService ssr = (CallingComponentService) bundleContext
				.getService(serviceReference[0]);
		SlingRepository slingRepository = ssr.getComponent();
		assertNotNull(slingRepository);
		assertNotNull(slingRepository.getDefaultWorkspace());
	}

	@Test
	public void shouldGetValidSlingRepository() throws Exception {

		SlingRepository slingRepository = IntegrationTestUtil
				.getSlingRepositoryFromServiceList(bundleContext);
		assertNotNull(slingRepository);
		assertTrue(slingRepository instanceof SlingServerRepository);
	}
    
	@Test
	public void shouldSuccessfullyLoginToModeShapeRepository() throws Exception {
		SlingRepository slingRepository = IntegrationTestUtil
				.getSlingRepositoryFromServiceList(bundleContext);

		Session session = slingRepository.login(new SecurityContextCredentials(
				new CustomSecurityContext()));
		assertNotNull(session);
        assertNotNull(slingRepository);
        assertTrue(slingRepository instanceof SlingServerRepository);        
    }
	
	@Test
	public void shouldWriteEtcMapNodesIfNotFound() throws Exception {
		SlingRepository slingRepository = IntegrationTestUtil
				.getSlingRepositoryFromServiceList(bundleContext);

		Session session = slingRepository.login(new SecurityContextCredentials(
				new CustomSecurityContext()));
        assertNotNull(session);
		assertFalse(session.nodeExists("/etc/map"));

		Node root = session.getNode("/");
		Node etc = root.addNode("etc");
		etc.addNode("map");

		session.save();

		assertTrue(session.nodeExists("/etc/map"));
		session.logout();
	}

}
