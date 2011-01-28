package com.sourcesense.stone.jcr.modeshape.server.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Formatter;
import javax.jcr.Repository;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.base.AbstractSlingRepository;
import org.modeshape.common.collection.Problem;
import org.modeshape.common.component.ClassLoaderFactory;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final String REPOSITORY_CONFIG_URL = "config";

    /**
     * @scr.property value=""
     */
    public static final String REPOSITORY_REGISTRATION_NAME = "name";

    private static final Repository NO_REPOSITORY = null;

    private JcrEngine engine;

    private static final Logger log = LoggerFactory.getLogger(SlingServerRepository.class);

    @Override
    public Repository acquireRepository() {
        Repository repository = super.acquireRepository();
        try {
            return repository == NO_REPOSITORY ? findSuitableRepository() : repository;
        } catch (Exception e) {
            log.error("Could not initialize repository", e);
        }
        return NO_REPOSITORY;
    }

    private Repository findSuitableRepository() throws Exception {

        String configFilePath = (String)getComponentContext().getProperties().get("config");

        log.info("Reading configuration from {}", configFilePath);

        URL configURL = new URL(configFilePath);

//        ExecutionContext executionContext = new ExecutionContext() {
//            @Override
//            public ClassLoader getClassLoader( String... classpath ) {
//                return new ClassLoader() {
//                    @Override
//                    protected Class<?> findClass( String name ) throws ClassNotFoundException {
//
//                        Bundle[] bundles = getComponentContext().getBundleContext().getBundles();
//                        for (Bundle bundle : bundles) {
//                            try {
//                                return bundle.loadClass(name);
//                            } catch (ClassNotFoundException e) {
//                                if (log.isInfoEnabled()) {
//                                    log.info("Bundle {} does not contain class {}", bundle.getSymbolicName(), name);
//                                }
//                            }
//                        }
//                        throw new ClassNotFoundException(String.format("Class with name %s not found in loaded bundles", name));
//                    }
//                };
//            }
//        };
        
        JcrConfiguration configuration = new JcrConfiguration();
        configuration.withClassLoaderFactory(new ClassLoaderFactory() {
            
            @Override
            public ClassLoader getClassLoader( String... classpath ) {
                return new ClassLoader() {
                    @Override
                    protected Class<?> findClass( String name ) throws ClassNotFoundException {

                        Bundle[] bundles = getComponentContext().getBundleContext().getBundles();
                        for (Bundle bundle : bundles) {
                            try {
                                return bundle.loadClass(name);
                            } catch (ClassNotFoundException e) {
                                if (log.isInfoEnabled()) {
                                    log.info("Bundle {} does not contain class {}", bundle.getSymbolicName(), name);
                                }
                            }
                        }
                        throw new ClassNotFoundException(String.format("Class with name %s not found in loaded bundles", name));
                    }
                };
            }
        });
        
        
        try {
            configuration.loadFrom(configURL);
            if (!configuration.getProblems().isEmpty()) {
                Formatter fmt = new Formatter().format("Some error occurred while loading configuration '%s'%n%n", configURL);
                int index = 1;
                for (Problem problem : configuration.getProblems()) {
                    fmt.format("%s) %s%n", index++, problem.getMessageString());

                    Throwable cause = problem.getThrowable();
                    if (cause != null) {
                        StringWriter writer = new StringWriter();
                        cause.printStackTrace(new PrintWriter(writer));
                        fmt.format("Caused by: %s", writer.getBuffer());
                    }

                    fmt.format("%n");
                }

                if (configuration.getProblems().size() == 1) {
                    fmt.format("1 error");
                } else {
                    fmt.format("%s errors", configuration.getProblems().size());
                }
                if (log.isInfoEnabled()) {
                    log.info(fmt.toString());
                }
            } else {
                engine = configuration.build();
                engine.start();
                return engine.getRepository("MyRepository");
            }
        } catch (Exception e) {
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
