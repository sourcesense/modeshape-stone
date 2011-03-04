package com.sourcesense.stone.jcr.modeshape.server;

import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.countModeShapeRepositoryConfigurations;
import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.removeModeShapeRepositoryConfigurations;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.slingBasicConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.stoneConfiguration;
import static com.sourcesense.stone.jcr.modeshape.server.PaxConfigurations.googleCommons;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.options;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import com.sourcesense.stone.jcr.modeshape.server.impl.Activator;

@RunWith( JUnit4TestRunner.class )
public class ModeShapeActivatorTest {

    @Inject
    BundleContext bundleContext;

    @Configuration
    public Option[] configuration() {
        return options(slingBasicConfiguration(), googleCommons(), stoneConfiguration());
    }
    
    @Test
    public void shouldNotCreateANewConfigurationBecauseOneIsAlreadyStored() throws Exception {
        int previousNumberOfModeShapeRepositoryConfigurations = countModeShapeRepositoryConfigurations(bundleContext);

        Activator activator = new Activator();
        activator.start(bundleContext);

        int currentNumberOfModeShapeRepositoryConfigurations = countModeShapeRepositoryConfigurations(bundleContext);
        assertEquals(previousNumberOfModeShapeRepositoryConfigurations, currentNumberOfModeShapeRepositoryConfigurations);
    }

    @Test
    public void shouldCreateANewConfigurationWhenNoConfigurationIsFound() throws Exception {

        removeModeShapeRepositoryConfigurations(bundleContext);
        assertEquals(0, countModeShapeRepositoryConfigurations(bundleContext));

        Activator activator = new Activator();
        activator.start(bundleContext);

        int currentNumberOfModeShapeRepositoryConfigurations = countModeShapeRepositoryConfigurations(bundleContext);
        assertEquals(1, currentNumberOfModeShapeRepositoryConfigurations);
    }
}
