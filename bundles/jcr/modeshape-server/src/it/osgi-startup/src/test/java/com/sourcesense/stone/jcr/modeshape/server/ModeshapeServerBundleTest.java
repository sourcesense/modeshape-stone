package com.sourcesense.stone.jcr.modeshape.server;

import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.getBundle;
import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.symbolicNamesListFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import java.util.Dictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeServerBundleTest {

    @Configuration
    public static Option[] configuration() {
        return options(felix(),
                       mavenConfiguration(),
                       wrappedBundle(mavenBundle("com.google.collections", "google-collections").version("1.0-rc3")));
    }

    @Test
    public void shouldHaveNotNullBundleContext( BundleContext bundleContext ) throws Exception {
        assertNotNull(bundleContext);
    }

    @Test
    public void shouldHaveModeshapeServerBundleLoaded( BundleContext bundleContext ) {
        Bundle[] bundles = bundleContext.getBundles();
        assertTrue(bundles.length != 0);

        assertTrue(symbolicNamesListFor(bundles).contains("com.sourcesense.stone.jcr.modeshape.server"));
    }

    @Test
    public void shouldHaveReferencesToModeshapeLibraries( BundleContext bundleContext ) throws Exception {
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());

        @SuppressWarnings( "unchecked" )
        Dictionary<String, String> manifestContent = modeshapeServerBundle.getHeaders();

        String bundleClassPath = manifestContent.get("Bundle-ClassPath");

        assertTrue(bundleClassPath.contains("modeshape-jcr-2.3.0.Final.jar"));
    }

    @Test
    public void shouldHaveModeshapeServerStarted( BundleContext bundleContext ) throws Exception {
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());

        assertEquals(Bundle.ACTIVE, modeshapeServerBundle.getState());
    }
}
