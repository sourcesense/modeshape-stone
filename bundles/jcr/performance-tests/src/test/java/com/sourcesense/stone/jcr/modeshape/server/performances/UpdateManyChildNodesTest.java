package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Test for measuring the performance of adding one extra child node to 
 * node with {@value #CHILD_COUNT} existing child nodes.
 */
class UpdateManyChildNodesTest extends AbstractTest {

    private static final int CHILD_COUNT = 10 * 1000;

    private Session session;

    private Node node;

    public void beforeSuite() throws RepositoryException {
        session = getRepository().login(getCredentials());
        node = session.getRootNode().addNode("testnode", "nt:unstructured");
        for (int i = 0; i < CHILD_COUNT; i++) {
            node.addNode("node" + i, "nt:unstructured");
        }
    }

    public void beforeTest() throws RepositoryException {
    }

    public void runTest() throws Exception {
        node.addNode("onemore", "nt:unstructured");
        session.save();
    }

    public void afterTest() throws RepositoryException {
        node.getNode("onemore").remove();
        session.save();
    }

    public void afterSuite() throws RepositoryException {
        session.getRootNode().getNode("testnode").remove();
        session.save();
        session.logout();
    }

}
