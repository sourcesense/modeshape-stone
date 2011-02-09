package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import javax.jcr.Repository;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.JcrRepository;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.ServiceReference;
import com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository;
import com.sourcesense.stone.test.services.CallingComponentService;
import com.sourcesense.stone.test.services.SimpleService;

@RunWith(JUnit4TestRunner.class)
public class ModeshapeConnectionTest extends AbstractTestCase {

	@Test
	@Ignore
	public void shouldTakeTheDefaultWorkspace()
			throws Exception {
		ServiceReference[] serviceReference = bundleContext.getAllServiceReferences(SimpleService.class.getName(), "(component.name=com.sourcesense.stone.test.services.CallingComponentService)");
		assertNotNull(serviceReference);
		
		CallingComponentService ssr = (CallingComponentService) bundleContext
		.getService(serviceReference[0]);
		SlingRepository slingRepository = ssr.getComponent();
		assertNotNull(slingRepository);
		assertNotNull(slingRepository.getDefaultWorkspace());
	}
	
	@Test
	@Ignore
    public void shouldGetValidModeShapeRepository() throws Exception {
        
	    SlingServerRepository slingServerRepository = new SlingServerRepository();
	    
	    Repository repository = slingServerRepository.acquireRepository();
	    
	    System.err.println("*************   " + repository);
	    
	    assertTrue(repository instanceof JcrRepository);
    }
}
