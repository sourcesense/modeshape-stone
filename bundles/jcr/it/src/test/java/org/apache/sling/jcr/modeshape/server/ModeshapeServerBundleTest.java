package org.apache.sling.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.apache.sling.jcr.modeshape.server.impl.SlingServerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeServerBundleTest {

    @Configuration
    public static Option[] configuration() {
        return options(felix(), mavenConfiguration());
    }

    @Test
    public void testMethod( BundleContext bundleContext ) {
        assertNotNull(bundleContext);
        System.out.println(bundleContext);
        new SlingServerRepository();
    }

}
