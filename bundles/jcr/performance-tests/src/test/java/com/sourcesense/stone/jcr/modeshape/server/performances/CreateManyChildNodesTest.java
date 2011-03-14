package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Test for measuring the performance of creating a node with
 * {@value #CHILD_COUNT} child nodes.
 */
class CreateManyChildNodesTest extends AbstractTest {

    private static final int CHILD_COUNT = 10 * 1000;

    private Session session;

    public void beforeSuite() throws RepositoryException {
        session = loginWriter();
    }

    public void beforeTest() throws RepositoryException {
    }

    public void runTest() throws Exception {
        Node node = session.getRootNode().addNode("testnode", "nt:unstructured");
        for (int i = 0; i < CHILD_COUNT; i++) {
            node.addNode("node" + i, "nt:unstructured");
        }
        session.save();
    }

    public void afterTest() throws RepositoryException {
        session.getRootNode().getNode("testnode").remove();
        session.save();
    }

}
