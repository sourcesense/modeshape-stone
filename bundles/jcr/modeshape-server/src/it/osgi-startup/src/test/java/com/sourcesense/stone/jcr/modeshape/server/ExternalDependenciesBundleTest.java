package com.sourcesense.stone.jcr.modeshape.server;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.graph.ExecutionContext;
import org.modeshape.jcr.JcrConfiguration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith( JUnit4TestRunner.class )
public class ExternalDependenciesBundleTest extends AbstractTestCase {

    @Test
    public void shouldExposeExecutionContextClass() throws Exception {
        
        ExecutionContext executionContext = new ExecutionContext();
        assertNotNull(executionContext);
    }
    
    @Test
    public void shouldExposeJcrConfiguration() throws Exception {
        JcrConfiguration configuration = new JcrConfiguration();
        assertNotNull(configuration);
    }
}
