package com.sourcesense.stone.jcr.modeshape.server;

import static com.sourcesense.stone.jcr.modeshape.server.IntegrationTestUtil.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import com.sourcesense.stone.jcr.modeshape.server.impl.Activator;

@RunWith( JUnit4TestRunner.class )
public class ModeShapeActivatorTest extends AbstractTestCase {

    @Test
    public void shouldNotCreateANewConfigurationBecauseOneIsAlreadyStored() throws Exception {
        int previousNumberOfModeShapeRepositoryConfigurations = countModeShapeRepositoryConfigurations(bundleContext);

        Activator activator = new Activator();
        activator.start(bundleContext);

        int currentNumberOfModeShapeRepositoryConfigurations = countModeShapeRepositoryConfigurations(bundleContext);
        assertEquals(previousNumberOfModeShapeRepositoryConfigurations, currentNumberOfModeShapeRepositoryConfigurations);
    }
}
