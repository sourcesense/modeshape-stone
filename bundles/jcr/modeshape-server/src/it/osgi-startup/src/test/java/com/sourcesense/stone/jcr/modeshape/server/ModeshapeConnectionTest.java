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

@RunWith( JUnit4TestRunner.class )
public class ModeshapeConnectionTest extends AbstractTestCase {

    @Test
    public void shouldTakeTheDefaultWorkspace( BundleContext bundleContext ) throws Exception {
        ServiceReference serviceReference = null;
        try {
            Dictionary<String, String> serviceProperties = new Hashtable<String, String>();
            serviceProperties.put(Constants.SERVICE_VENDOR, "JBoss");
            serviceProperties.put(Constants.SERVICE_DESCRIPTION, "Test service for SlingRepository");
            bundleContext.registerService(SlingServerRepository.class.getName(), new SlingServerRepository(), serviceProperties);
            serviceReference = bundleContext.getServiceReference(SlingServerRepository.class.getName());
            
            SlingServerRepository ssr = (SlingServerRepository)bundleContext.getService(serviceReference);
            
            assertNotNull(ssr);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (null != serviceReference) {
                bundleContext.ungetService(serviceReference);
            }
        }
    }
}
