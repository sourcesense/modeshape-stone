package org.modeshape.connector.jackrabbit;

import org.jboss.security.config.IDTrustConfiguration;
import org.modeshape.common.SystemFailureException;
import org.modeshape.common.collection.Problem;
import org.modeshape.connector.jackrabbit.JackrabbitRepositorySource;
import org.modeshape.graph.ExecutionContext;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;
import org.modeshape.jcr.JcrRepository.Option;

public class JackrabbitConnectorTestUtil {

    protected static final String JAAS_CONFIG_FILE_PATH = "security/jaas.conf.xml";
    protected static final String JAAS_POLICY_NAME = "modeshape-jcr";

    public static final String CARS_REPOSITORY_NAME = "Cars";
    public static final String AIRCRAFT_REPOSITORY_NAME = "Aircraft";

    protected static final String CARS_SOURCE_NAME = "Cars Source";
    protected static final String AIRCRAFT_SOURCE_NAME = "Aircraft Source";

    static {
        String configFile = JAAS_CONFIG_FILE_PATH;
        IDTrustConfiguration idtrustConfig = new IDTrustConfiguration();

        try {
            idtrustConfig.config(configFile);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static JcrEngine loadEngine() {
        final ClassLoader classLoader = JackrabbitConnectorTestUtil.class.getClassLoader();

        ExecutionContext newContext = new ExecutionContext();
        JcrConfiguration configuration = new JcrConfiguration(newContext);

        configuration
        .repositorySource(CARS_SOURCE_NAME)
        .setProperty("url", "http://localhost:8080/server")
        .setProperty("username", "admin")
        .setProperty("password", "admin")
        .usingClass(JackrabbitRepositorySource.class.getName())
        .loadedFromClasspath()
        .setDescription("Repository source with cars")
        .and()
        .repository(CARS_REPOSITORY_NAME)
        .setDescription("JCR Repository with cars")
        .setSource(CARS_SOURCE_NAME)
        .addNodeTypes(classLoader.getResource("cars.cnd"))
        .setOption(Option.JAAS_LOGIN_CONFIG_NAME,JAAS_POLICY_NAME);
        
        configuration
        .repositorySource(AIRCRAFT_SOURCE_NAME)
        .setProperty("url", "http://localhost:8080/server")
        .setProperty("username", "admin")
        .setProperty("password", "admin")
        .usingClass(JackrabbitRepositorySource.class.getName())
        .loadedFromClasspath()
        .setDescription("Repository source with aircraft")
        .and()
        .repository(AIRCRAFT_REPOSITORY_NAME)
        .setDescription("JCR Repository with aircraft")
        .setSource(AIRCRAFT_SOURCE_NAME)
        .addNodeTypes(classLoader.getResource("aircraft.cnd"))
        .setOption(Option.JAAS_LOGIN_CONFIG_NAME,JAAS_POLICY_NAME);

        configuration.save();

        JcrEngine engine = configuration.build();
        engine.start();
        if (engine.getProblems().hasProblems()) {
            for (Problem problem : engine.getProblems()) {
                System.err.println(problem.getMessageString());
            }
            throw new RuntimeException("Could not start due to problems");
        }

        try {
            engine.getGraph(CARS_SOURCE_NAME).importXmlFrom(classLoader.getResource("cars.xml").toURI()).into("/");
            engine.getGraph(AIRCRAFT_SOURCE_NAME).importXmlFrom(classLoader.getResource("aircraft.xml").toURI()).into("/");
        } catch (Throwable t) {
            throw new SystemFailureException("Could not import the content into the repositories", t);
        }

        return engine;
    }

}
