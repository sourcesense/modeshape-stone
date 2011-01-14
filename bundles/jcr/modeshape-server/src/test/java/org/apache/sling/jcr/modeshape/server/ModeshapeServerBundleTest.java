package org.apache.sling.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.frameworks;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
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
        return options(frameworks(felix()),
                       provision(mavenBundle().groupId("javax.jcr").artifactId("jcr").version("2.0"),
                                 mavenBundle().groupId("org.osgi").artifactId("osgi.osgi.core").version("4.1.0"),
                                 mavenBundle().groupId("org.osgi").artifactId("osgi.osgi.compendium").version("4.1.0"),
                                 mavenBundle().groupId("org.apache.jackrabbit").artifactId("jackrabbit-api").version("2.1.1"),
                                 mavenBundle().groupId("org.apache.jackrabbit").artifactId("jackrabbit-jcr-commons").version("2.1.1"),
                                 mavenBundle().groupId("org.apache.jackrabbit").artifactId("jackrabbit-jcr-rmi").version("2.1.1"),
                                 mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").version("1.5.2"),
                                 mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").version("1.5.2"),
                                 mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.jcr.api").version("2.1.0"),
                                 mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.jcr.base").version("2.1.0"),
                                 mavenBundle().groupId("com.sourcesense.stone").artifactId("com.sourcesense.stone.jcr.modeshape.server")));
    }

    @Test
    public void testMethod( BundleContext bundleContext ) {
        assertNotNull(bundleContext);
    }
}
