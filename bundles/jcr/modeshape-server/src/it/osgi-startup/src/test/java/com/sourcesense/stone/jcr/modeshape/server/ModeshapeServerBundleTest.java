package com.sourcesense.stone.jcr.modeshape.server;

import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.getBundle;
import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.symbolicNamesListFor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Dictionary;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeServerBundleTest extends AbstractTestCase {

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
    @Ignore
    public void shouldHaveReferencesToModeshapeLibraries( BundleContext bundleContext ) throws Exception {
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());

        @SuppressWarnings( "unchecked" )
        Dictionary<String, String> manifestContent = modeshapeServerBundle.getHeaders();

        String bundleClassPath = manifestContent.get("Bundle-ClassPath");

        assertTrue(bundleClassPath.contains("modeshape-jcr-2.3.0.Final.jar"));
    }

    @Test
    public void shouldHaveModeshapeServerActive( BundleContext bundleContext ) throws Exception {
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());

        assertEquals(Bundle.ACTIVE, modeshapeServerBundle.getState());
    }
    
    @Test
    public void shouldHaveActivatorRegistered(BundleContext bundleContext ) throws Exception {
        
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());
        
        @SuppressWarnings( "unchecked" )
        Dictionary<String, String> manifestContent = modeshapeServerBundle.getHeaders();
        
        String activatorClassName = manifestContent.get("Bundle-Activator");
        
        assertEquals("com.sourcesense.stone.jcr.modeshape.server.impl.Activator", activatorClassName);
    }
}
