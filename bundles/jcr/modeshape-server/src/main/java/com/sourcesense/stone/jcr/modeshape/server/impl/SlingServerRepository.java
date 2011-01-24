package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.ServiceLoader;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.AbstractSlingRepository;
import org.osgi.service.component.ComponentContext;

/**
 * The <code>SlingServerRepository</code> TODO
 * add  policy="require"
 * @scr.component label="%repository.name" description="%repository.description" name=
 *                "com.sourcesense.stone.jcr.modeshape.server.SlingServerRepository" configurationFactory="true" policy="require"
 * @scr.property name="service.vendor" value="JBoss"
 * @scr.property name="service.description" value="Factory for embedded Modeshape Repository Instances"
 */
public class SlingServerRepository extends AbstractSlingRepository implements Repository, SlingRepository {

    /**
     * The name of the configuration property defining the URL to the repository configuration file (value is "config").
     * <p>
     * If the configuration file is located in the local file system, the "file:" scheme must still be specified.
     * <p>
     * This parameter is mandatory for this activator to start the repository.
     * 
     * @scr.property value=""
     */
    public static final String REPOSITORY_CONFIG_URL = "file:repository.xml?repositoryName=MyRepository";

    private static final Repository NO_REPOSITORY = null;

    private RepositoryFactory repositoryFactory;

    @Override
    protected void activate( ComponentContext arg0 ) throws Exception {
        System.out.println("*************************");
        System.out.println("*************************");
        System.out.println("*************************");
        System.out.println("*************************");
        System.out.println("*************************");
        super.activate(arg0);
    }
    
    @Override
     public Repository acquireRepository() {
        Repository repository = super.acquireRepository();
        return repository != NO_REPOSITORY ? repository : findSuitableRepository();
    }

    private Repository findSuitableRepository() {
        @SuppressWarnings( "unchecked" )
        Dictionary<String, Object> environment = this.getComponentContext().getProperties();
        //String configURL = (String)environment.get(REPOSITORY_CONFIG_URL);
        URL configURL = this.getClass().getResource("/repository.xml");

        Map<String, String> parameters = Collections.singletonMap("org.modeshape.jcr.URL", configURL.toString() + "?repositoryName=MyRepository");
        Repository repository = null;

        for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
            try {
                repository = factory.getRepository(parameters);
                if (repository != null) {
                    repositoryFactory = factory;
                    return repository;
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return NO_REPOSITORY;
    }

    @Override
    public void disposeRepository( Repository repository ) {
        super.disposeRepository(repository);

        if (repositoryFactory instanceof org.modeshape.jcr.api.RepositoryFactory) {
            ((org.modeshape.jcr.api.RepositoryFactory)repositoryFactory).shutdown();
        }
    }
}
