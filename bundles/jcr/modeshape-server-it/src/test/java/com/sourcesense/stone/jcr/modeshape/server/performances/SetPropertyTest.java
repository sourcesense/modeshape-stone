package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Test for measuring the performance of setting a single property and
 * saving the change.
 */
class SetPropertyTest extends AbstractTest {

    private Session session;

    private Node node;

    public void beforeSuite() throws RepositoryException {
        session = getRepository().login(getCredentials());
        node = session.getRootNode().addNode("testnode", "nt:unstructured");
        session.save();
    }

    public void beforeTest() throws RepositoryException {
        node.setProperty("count", -1);
        session.save();
    }

    public void runTest() throws Exception {
        for (int i = 0; i < 1000; i++) {
            node.setProperty("count", i);
            session.save();
        }
    }

    public void afterTest() throws RepositoryException {
    }

    public void afterSuite() throws RepositoryException {
        session.getRootNode().getNode("testnode").remove();
        session.save();
        session.logout();
    }

}
