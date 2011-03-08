package com.sourcesense.stone.jcr.modeshape.server.performances;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * <code>ReadPropertyTest</code> implements a performance test, which reads
 * three properties: one with a jcr prefix, one with the empty prefix and a
 * third one, which does not exist.
 */
class ReadPropertyTest extends AbstractTest {

    private Session session;

    private Node root;

    @Override
    protected void beforeSuite() throws Exception {
        session = getRepository().login(getCredentials());
        root = session.getRootNode().addNode(
                getClass().getSimpleName(), "nt:unstructured");
        root.setProperty("property", "value");
        session.save();
    }

    @Override
    protected void runTest() throws Exception {
        for (int i = 0; i < 100000; i++) {
            root.getProperty("jcr:primaryType");
            root.getProperty("property");
            root.hasProperty("does-not-exist");
        }
    }

    @Override
    protected void afterSuite() throws Exception {
        root.remove();
        session.save();
        session.logout();
    }
}
