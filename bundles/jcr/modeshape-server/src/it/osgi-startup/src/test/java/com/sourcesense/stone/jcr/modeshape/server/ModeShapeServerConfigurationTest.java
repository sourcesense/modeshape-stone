package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

@RunWith( JUnit4TestRunner.class )
public class ModeShapeServerConfigurationTest extends AbstractTestCase {

    @Test
    public void shouldHaveConfigAdminServiceReference() throws Exception {
        ServiceReference sr = getConfigurationAdminServiceReferenceFrom(bundleContext);

        assertNotNull(sr);
    }

    @Test
    public void shouldHaveConfigAdminServiceRegistered() throws Exception {

        ServiceReference sr = getConfigurationAdminServiceReferenceFrom(bundleContext);
        ConfigurationAdmin ca = (ConfigurationAdmin)bundleContext.getService(sr);

        assertNotNull(ca);
    }

    private ServiceReference getConfigurationAdminServiceReferenceFrom( BundleContext bundleContext ) {
        final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();
        ServiceReference sr = bundleContext.getServiceReference(CONFIG_ADMIN_NAME);
        return sr;
    }
}
