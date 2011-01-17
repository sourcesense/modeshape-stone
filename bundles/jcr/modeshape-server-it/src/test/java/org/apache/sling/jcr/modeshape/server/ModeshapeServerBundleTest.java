package org.apache.sling.jcr.modeshape.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.mavenConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@RunWith( JUnit4TestRunner.class )
public class ModeshapeServerBundleTest {

    @Configuration
    public static Option[] configuration() {
        return options(felix(), mavenConfiguration(), wrappedBundle(mavenBundle("com.google.collections", "google-collections").version("1.0-rc3")));
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
    public void shouldHaveModeshapeServerStarted( BundleContext bundleContext ) throws Exception {
        Bundle modeshapeServerBundle = getBundle("com.sourcesense.stone.jcr.modeshape.server", bundleContext.getBundles());

        assertEquals(Bundle.ACTIVE, modeshapeServerBundle.getState());
    }

    private List<String> symbolicNamesListFor( Bundle[] bundles ) {

        return Lists.transform(Arrays.asList(bundles), new Function<Bundle, String>() {

            @Override
            public String apply( Bundle bundle ) {
                return bundle.getSymbolicName();
            }
        });
    }

    private Bundle getBundle( String bundleSymbolicName,
                              Bundle[] bundles ) {

        for (Bundle bundle : bundles) {
            if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
                return bundle;
            }
        }

        return null;
    }
}
