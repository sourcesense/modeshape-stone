package org.modeshape.connector.jackrabbit;

import javax.jcr.Repository;
import org.junit.AfterClass;
import org.modeshape.graph.Graph;
import org.modeshape.graph.connector.RepositorySource;
import org.modeshape.graph.connector.test.ReadableConnectorTest;
import org.modeshape.jcr.JcrEngine;

public class JackrabbitConnectorReadableTest extends ReadableConnectorTest {

    private static JcrEngine engine;
    private Repository carsRepository;

    @Override
    protected RepositorySource setUpSource() throws Exception {

        if (null == engine) {
            engine = JackrabbitConnectorTestUtil.loadEngine();
            carsRepository = engine.getRepository(JackrabbitConnectorTestUtil.CARS_REPOSITORY_NAME);
        }

        JackrabbitRepositorySource source = new JackrabbitRepositorySource() {

            @Override
            public String getName() {
                return "Cars source";
            }
            
            @Override
            protected synchronized Repository getRepository() {
                return carsRepository;
            }

        };
        source.setUrl("http://localhost:8080/server");
        source.setUsername("admin");
        source.setPassword("admin");
        return source;
    }

    @Override
    public void afterEach() throws Exception {

        try {
            engine.getGraph(JackrabbitConnectorTestUtil.CARS_SOURCE_NAME).delete("/");
        } catch (Exception e) {
        }
        shutdownRepository();
    }

    @AfterClass
    public static void afterAll() throws Exception {
        try {
            engine.shutdown();
        } finally {
            engine = null;
        }
    }

    @Override
    protected void initializeContent( Graph graph ) throws Exception {
    }

}
