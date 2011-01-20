package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeServerConfigurationTest extends AbstractTestCase {

    @Test
    @Ignore("We need to load Pax ConfMan first")
    public void shouldHaveConfigAdminServiceRegistered( BundleContext bundleContext ) throws Exception {

        final String CONFIG_ADMIN_NAME = ConfigurationAdmin.class.getName();
        ServiceReference sr = bundleContext.getServiceReference(CONFIG_ADMIN_NAME);

        assertNotNull(sr);
    }
}
