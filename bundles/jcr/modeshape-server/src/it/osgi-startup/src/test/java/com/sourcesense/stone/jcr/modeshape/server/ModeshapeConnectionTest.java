package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.ServiceReference;
import com.sourcesense.stone.jcr.modeshape.server.impl.SlingServerRepository;
import com.sourcesense.stone.test.services.CallingComponentService;
import com.sourcesense.stone.test.services.SimpleService;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeConnectionTest extends AbstractTestCase {

    @Test
    @Ignore
    public void shouldTakeTheDefaultWorkspace() throws Exception {
        ServiceReference[] serviceReference = bundleContext.getAllServiceReferences(SimpleService.class.getName(),
                                                                                    "(component.name=com.sourcesense.stone.test.services.CallingComponentService)");
        assertNotNull(serviceReference);

        CallingComponentService ssr = (CallingComponentService)bundleContext.getService(serviceReference[0]);
        SlingRepository slingRepository = ssr.getComponent();
        assertNotNull(slingRepository);
        assertNotNull(slingRepository.getDefaultWorkspace());
    }

    @Test
    @Ignore
    public void shouldGetValidSlingRepository() throws Exception {

        ServiceReference slingRepositoryServiceReference = bundleContext.getServiceReference(SlingRepository.class.getName());
        SlingRepository slingRepository = (SlingRepository)bundleContext.getService(slingRepositoryServiceReference);

        assertNotNull(slingRepository);
        assertTrue(slingRepository instanceof SlingServerRepository);        
    }
}
