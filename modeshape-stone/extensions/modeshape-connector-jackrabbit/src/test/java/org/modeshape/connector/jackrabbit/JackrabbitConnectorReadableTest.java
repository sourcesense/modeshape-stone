package org.modeshape.connector.jackrabbit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.jcr.Repository;
import org.junit.AfterClass;
import org.modeshape.connector.jackrabbit.JackrabbitRepositorySource;
import org.modeshape.connector.jackrabbit.RepositoryFactory;
import org.modeshape.graph.Graph;
import org.modeshape.graph.connector.RepositorySource;
import org.modeshape.graph.connector.test.ReadableConnectorTest;
import org.modeshape.jcr.JcrEngine;

public class JackrabbitConnectorReadableTest extends ReadableConnectorTest {

    private static JcrEngine engine;
    private static RepositoryFactory repositoryFactory;

    @Override
    protected RepositorySource setUpSource() throws Exception {

        if (null == engine) {
            engine = JackrabbitConnectorTestUtil.loadEngine();
            Repository carsRepository = engine.getRepository(JackrabbitConnectorTestUtil.CARS_REPOSITORY_NAME);
            Repository aircraftRepository = engine.getRepository(JackrabbitConnectorTestUtil.AIRCRAFT_REPOSITORY_NAME);

            repositoryFactory = mock(RepositoryFactory.class);
            when(repositoryFactory.createRepository("http://localhost:8080/server/cars")).thenReturn(carsRepository);
            when(repositoryFactory.createRepository("http://localhost:8080/server/aircraft")).thenReturn(aircraftRepository);
        }

        JackrabbitRepositorySource source = new JackrabbitRepositorySource() {
            @Override
            protected RepositoryFactory getRepositoryFactory() {
                return super.getRepositoryFactory();
            }
        };

        return source;
    }

    @Override
    public void afterEach() throws Exception {
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
