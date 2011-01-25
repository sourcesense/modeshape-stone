package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.io.IOException;
import java.net.URL;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.AbstractSlingRepository;
import org.modeshape.common.collection.Problem;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;
import org.xml.sax.SAXException;

/**
 * The <code>SlingServerRepository</code> TODO add policy="require"
 * 
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

    private JcrEngine engine;

    @Override
    public Repository acquireRepository() {
        Repository repository = super.acquireRepository();
        return repository == NO_REPOSITORY ? findSuitableRepository() : repository;
    }

    private Repository findSuitableRepository() {
//        URL configURL = new URL("file://Users/anv/modeshape-repository.xml");

//        Map<String, String> parameters = Collections.singletonMap("org.modeshape.jcr.URL", configURL.toString()
//                                                                                           + "?repositoryName=MyRepository");
//        Repository repository = null;

        JcrConfiguration configuration = new JcrConfiguration();

        try {
            configuration.loadFrom("file://Users/anv/modeshape-repository.xml");
            if (!configuration.getProblems().isEmpty()) {
                for (Problem problem : configuration.getProblems()) {
                    System.out.println("********* " + problem.getMessageString());
                }
            }
            else {
                engine = configuration.build();
                engine.start();
                return engine.getRepository("MyRepository");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return NO_REPOSITORY;
    }

    @Override
    public void disposeRepository( Repository repository ) {
        super.disposeRepository(repository);

        engine.shutdown();
    }
}
