package com.sourcesense.stone.extensions.it;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Workspace;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;
import org.modeshape.jcr.JcrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modeshape2JackrabbitConnectorIT {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JcrEngine engine;

    public static void main( String[] args ) {
        Modeshape2JackrabbitConnectorIT modeshape2JackrabbitConnectorIT = new Modeshape2JackrabbitConnectorIT();
        modeshape2JackrabbitConnectorIT.setup();
        modeshape2JackrabbitConnectorIT.runTests();
        modeshape2JackrabbitConnectorIT.tearDown();
    }

    private void setup() {
        logger.info("Setting up");
        JcrConfiguration configuration = new JcrConfiguration();
        try {
            configuration.loadFrom(this.getClass().getResource("modeshape-repository-jackrabbit.xml"));
            logger.info("Configuration loaded");
            this.engine = configuration.build();
            logger.info("Engine built");
            this.engine.start();
            logger.info("Engine started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runTests() {
        logger.info("Starting tests");
        try {
            JcrRepository repository = engine.getRepository("test");
            logger.info("Got repository test");
            Session session = repository.login();
            logger.info("Got valid session");
            Node rootNode = session.getRootNode();
            logger.info("Got root node: " + rootNode.getPath());
            Workspace workspace = session.getWorkspace();
            logger.info("Current workspace: " + workspace.getName());
            rootNode.addNode("childNode", "nt:unstructured");
            session.save();
            session.logout();
            logger.info("Logged out");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tearDown() {
        logger.info("Tearing down");
        if (null != engine) {
            engine.shutdown();
        }
    }

}
